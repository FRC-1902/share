package predictor;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.wrappers.Award;
import predictor.wrappers.Event;
import predictor.wrappers.Team;
import predictor.wrappers.Utils;
import java.util.*;

public class Main {

    public static final String[] allDivisions = new String[]{"arc", "cars", "carv", "cur", "gal", "hop", "new", "tes"};
    public static final String[] originalDivisions = new String[]{"gal", "arc", "cur", "new"};

    private static String event = "2016flwp"; //use "YEARchamps" for champs1 *
    private static final int year = Integer.parseInt(event.substring(0, 4));
    private static final boolean champs = event.contains("champs");
    private static boolean districtEvent;
    private static Event eventObj = null;

    private static List<Team> teams = new ArrayList<>();
    private static int currentTeam = 0;
    private static final Object TEAMS_USE = new Object();

    private static List<Team> relevant = new ArrayList<>();
    private static final Object RELEVANT_USE = new Object();

    private static final int threadsToUse = 30;
    public static int threadsDone = 0;

    private static final boolean doAllTeams = false;

    private static final double rcaPoint = 1, eiPoint = .75, dcaPoint = .5, deiPoint = .35;


    public static void main(String args[]) {
        String eventName;
        if (champs) {
            event = year + "cmp";
            eventName = year + " Championship";
            districtEvent = false;
            if (year <= 2015) {
                log("Reading teams from Championship divisions...");
                String[] divs;
                if (year <= 2014) divs = originalDivisions;
                else divs = allDivisions;
                for (String s : divs) {
                    Event e = new Event(year + s);
                    e.initInfo();
                    log(e.name + "...");
                    for (Team t : e.getTeams()) {
                        teams.add(t);
                    }
                }
            } else {
                teams = new Event(year + "cmp").getTeams();
            }
        } else if (doAllTeams) {
            eventName = "Earth";
            event = "2016cmp";
            districtEvent = false;
            System.out.println("Getting all teams in the world...please be patient.");
            for (int i = 0; i < 13; i++) {
                System.out.println("Page " + (i + 1));
                JSONArray jsonTeams = Utils.getArray("teams/" + i);
                for (JSONObject o : Utils.getObjects(jsonTeams)) {
                    teams.add(new Team(o));
                }
            }
        } else {
            eventObj = new Event(event);
            teams = eventObj.getTeams();
            eventObj.initInfo();
            eventName = eventObj.name;
            districtEvent = eventObj.district;
        }
        log(teams.size() + " teams at the " + eventName + ".");

        if (champs) {
            log("This event is the World Championship, so teams that have not won a RCA this year or have less than four RCA wins will be excluded.");
        } else if (doAllTeams) {
            log("This is processing all teams, so any team with a CPR of 0 will be excluded.");
        } else if (eventObj.type == Event.Type.DISTRICT_CHAMPIONSHIP) {
            log("This event is a District Championship, so teams that have not won a DCA in this District will be excluded.");
        } else {
            log("This event is a normal Regional or District, so any team with a CPR of 0 will be excluded.");
        }

        double getStart = System.currentTimeMillis();

        for (int i=0; i<threadsToUse; i++) {
            Thread thread = new Thread(Main::processTeams);
            thread.setName("Team Processing Thread #" + (i + 1));
            thread.start();
        }

        while (threadsDone != threadsToUse) {
            try {
                Thread.sleep(25);
            } catch (Exception ignored) {}
        }

        log("Time taken: " + ((System.currentTimeMillis() - getStart) / 1000) + "s");

        relevant.forEach(Main::calculateCPR);
        for (Team t : new ArrayList<>(relevant)) {
            if (t.cpr == 0) relevant.remove(t);
        }
        Collections.sort(relevant, cprComp);
        int pos = 1;
        System.out.println("------------------------");
        System.out.println("Found " + relevant.size() + " relevant teams. They are:");
        for (Team t : relevant) {
            log(pos + ". " + t.number + " (" + t.name + ") - " + Utils.roundToPlace(t.cpr, 2) + " CPR");
            pos++;
        }
    }

    /**
     * Starts processing all the teams in the Team list.
     */
    public static void processTeams() {
        while (currentTeam < teams.size()) {
            Team t;
            synchronized (TEAMS_USE) {
                t = teams.get(currentTeam);
                currentTeam++;
            }
            log("Processing team " + t.number + " - (" + currentTeam + " / " + teams.size() + ")");
            boolean caThisYear = false;
            boolean hof = false;
            List<Integer> dcaWins = new ArrayList<>();
            int caWins = 0;
            List<Award> awards = doAllTeams ? t.getAllAwards() : t.getAwardsBefore(event);
            for (Award a : awards) {
                if (a.type == CHAIRMANS && (!a.district || districtEvent)) {
                    caWins++;
                    if (a.district) {
                        Event e = t.getEvent(a.event);
                        dcaWins.add(e.districtID);
                    }
                    if (a.year == year) caThisYear = true;
                    if (a.event.equals(a.year + "cmp")) hof = true;
                }
            }
            if (champs) {
                if (caWins > 3 && caThisYear) relevant.add(t);
            } else if (doAllTeams) {
                if (hof) {
                    log("Removed " + t.number + " from consideration due to them being a Hall of Fame team.");
                } else {
                    relevant.add(t);
                }
            } else if (eventObj.type == Event.Type.DISTRICT_CHAMPIONSHIP) {
                if (dcaWins.contains(eventObj.districtID)) {
                    relevant.add(t);
                }
            } else {
                if (!caThisYear && !hof) {
                    relevant.add(t);
                } else if (caThisYear) {
                    log("Removed " + t.number + " from consideration due to them having already won a Chairman's award this season.");
                } else if (hof) {
                    log("Removed " + t.number + " from consideration due to them being a Hall of Fame team.");
                }
            }
        }
        threadsDone++;
    }

    public static void calculateCPR(Team t) {
        int rcaWins = 0;
        int dcaWins = 0;
        int eiWins = 0;
        int deiWins = 0;
        int streakLength = 0;
        double streakPoints = 0;
        List<Award> awards;
        if (doAllTeams) {
            awards = t.getAllAwards();
        } else {
            awards = t.getAwardsBefore(event);
        }
        for (Award a : awards) {
            if (a.year <= year && a.year >= (year - 3)) { //If the award is within a four year range
                if (a.type == CHAIRMANS) {
                    if (a.district) dcaWins++;
                    else rcaWins++;
                } else if (a.type == ENGINEERING_INSPIRATION) {
                    if (a.district) deiWins++;
                    else eiWins++;
                }
            }
        }
        //TODO: include the current year in the streak IF there has been an EI or CA win (?)
        int currYear = year - 1;
        boolean streakContinued = true;
        while (streakContinued) {
            streakContinued = false;
            for (Award a : awards) {
                if (a.year == currYear) {
                    if (a.type == CHAIRMANS) {
                        if (a.district) streakPoints += dcaPoint;
                        else streakPoints += rcaPoint;
                        streakLength++;
                        streakContinued = true;
                    }
                    if (a.type == ENGINEERING_INSPIRATION) {
                        if (a.district) streakPoints += deiPoint;
                        else streakPoints += eiPoint;
                        streakLength++;
                        streakPoints += .75;
                        streakContinued = true;
                    }
                }
            }
            currYear--;
        }
        //System.out.println(t.number + " has " + (rcaWins + dcaWins) + " Chairman's wins and " + (eiWins + deiWins) + " EI wins.");
        //if (streakLength != 0) System.out.println(t.number + " has a Chairman/EI streak of " + streakLength + ", which started in " + (currYear + 2) + ".");
        t.cpr = (rcaWins * rcaPoint) + (eiWins * eiPoint) + (dcaWins * dcaPoint) + (deiWins * deiPoint) + streakPoints;
    }

    public static void log(String s) {
        System.out.println(s);
    }

    public static final Comparator<Team> cprComp = (t1, t2) -> {
        Double data = t2.cpr - t1.cpr;
        data *= 1000;
        return data.intValue();
    };

    public static final int CHAIRMANS = 0;
    public static final int ENGINEERING_INSPIRATION = 9;
}
