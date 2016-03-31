package predictor;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.wrappers.Award;
import predictor.wrappers.Event;
import predictor.wrappers.Team;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Main {

    //TODO: For checking events that have already played, filter out awards the team won after that event but within the same year
    //TODO: Reprogram this. It's such a mess :(

    private static final String tbaAPI = "https://www.thebluealliance.com/api/v2/";
    private static final String tbaID = "?X-TBA-App-Id=frc1902:chairmans_predictions:v0.1";
    private static HashMap<String, Double> teamData = new HashMap<>();
    private static final String[] allDivisions = new String[]{"arc", "cars", "carv", "cur", "gal", "hop", "new", "tes"};
    public static final String[] originalDivisions = new String[]{"gal", "arc", "cur", "new"};

    private static final String event = "all"; //use "champs" for champs and "all" for all teams in the world
    private static final boolean useDivisions = false; //Only used if doing champs
    private static int year = 2016; //Only used if doing champs

    public static final int CHAIRMANS = 0;
    public static final int ENGINEERING_INSPIRATION = 9;

    public Main() {
        boolean champs = event.equals("champs");
        boolean all = event.equals("all");
        List<Team> teams = new ArrayList<>();
        if (champs) {
            if (useDivisions) {
                System.out.println("Reading teams from divisions...");
                String[] divs;
                if (year <= 2014) divs = originalDivisions;
                else divs = allDivisions;
                for (String s : divs) {
                    Event e = new Event(year + s);
                    for (Team t : e.getTeams()) {
                        teams.add(t);
                    }
                }
            } else {
                Event e = new Event(year + "cmp");
                teams = e.getTeams();
            }
        } else if (all) {
            System.out.println("Getting all teams in the world...please be patient.");
            for (int i=0; i<13; i++) {
                System.out.println("Page " + (i + 1));
                JSONArray jsonTeams = getArray("teams/" + i);
                for (JSONObject o : getObjects(jsonTeams)) {
                    teams.add(new Team(o));
                }
            }
        } else {
            year = Integer.parseInt(event.substring(0, 4));
            teams = new Event(event).getTeams();
        }
        List<Team> competitors = new ArrayList<>();
        if (champs) {
            System.out.println(teams.size() + " teams at " + year + " Champs, finding that year's Chairman's winners...");
        } else if (all) {
            System.out.println(teams.size() + " teams being anaylzed for Chairman's. This may take a while.");
        } else {
            System.out.println(teams.size() + " teams at " + event + ". Gathering some data on them...");
        }
        double startMillis = System.currentTimeMillis();
        int currTeam = 0;
        for (Team t : teams) {
            double chairmanWins = 0;
            for (Award a : t.getAllAwards()) {
                if (a.type == CHAIRMANS && !a.name.contains("District") && !a.event.equalsIgnoreCase(event)) {
                    int aYear = Integer.parseInt(a.event.substring(0, 4));
                    if (aYear <= year) {
                        chairmanWins++;
                        if (champs) {
                            if (a.event.contains(year + "")) {
                                if (!competitors.contains(t)) {
                                    competitors.add(t);
                                    System.out.println("" + t.number);
                                }
                            }
                        }
                    }
                }
            }
            teamData.put(t.number + "chairmans", chairmanWins);
            if (all) {
                currTeam++;
                System.out.println("" + t.number + " " + ((currTeam / teams.size()) * 100) + "%");
            }
        }
        if (champs) {
            System.out.println("Time taken to find winners: " + ((System.currentTimeMillis() - startMillis) / 1000) + " seconds");
            System.out.println(competitors.size() + " " + year + " Chairman's winners found. Finding competitors...");
        } else {
            competitors = new ArrayList<>(teams);
            System.out.println("Finding Chairman's competitors...");
        }
        for (Team t : new ArrayList<>(competitors)) {
            double chairmanWins = teamData.get(t.number + "chairmans");
            if (champs) {
                if (chairmanWins < 4) {
                    competitors.remove(t);
                }
            }
        }

        competitors.forEach(Main::calculateCPR);

        if (!champs) {
            for (Team t : new ArrayList<>(competitors)) {
                double cpr = getData(t, "cpr");
                if (cpr == 0) competitors.remove(t);
            }
        }

        final Comparator<Team> comp = (t1, t2) -> {
            Double data = getData(t2, "cpr") - getData(t1, "cpr");
            data *= 1000;
            return data.intValue();
        };

        Collections.sort(competitors, comp);

        if (champs) {
            System.out.println(competitors.size() + " Chairman's competitors found. They are:");
        } else {
            System.out.println(competitors.size() + " Chairman's competitors found at " + event + ". They are:");
        }
        int pos = 1;
        for (Team t : competitors) {
            System.out.println(pos + ". " + t.number + " (" + t.name + ") - " + roundToPlace(getData(t, "cpr"), 2) + " CPR");
            pos++;
        }
    }

    public static void calculateCPR(Team t) {
        double cpr;
        double caWins = 0;
        double eiWins = 0;
        boolean chairmansThisYear = false;
        for (Award a : t.getAllAwards()) {
            if (!a.event.equalsIgnoreCase(event)) { //If this event has already been played, we do not want any awards this team won here to affect the calculations
                if (a.year == year || a.year == year - 1 || a.year == year - 2 || a.year == year - 3) {
                    if (a.type == CHAIRMANS) caWins++;
                    if (a.type == ENGINEERING_INSPIRATION) eiWins++;
                }
                if (a.year == year && a.type == CHAIRMANS) chairmansThisYear = true;
            }
        }
        int streak = 0;
        int currYear = year - 1;
        while (true) {
            boolean streakOver = true;
            for (Award a : t.getAllAwards()) {
                if (a.year == currYear && !a.event.equalsIgnoreCase(event)) {
                    if (a.type == CHAIRMANS || a.type == ENGINEERING_INSPIRATION) {
                        streak++;
                        currYear--;
                        streakOver = false;
                        //System.out.println(t.number + "'s streak continued by " + a.event);
                        break;
                    }
                }
            }
            if (streakOver) break;
        }
        if (chairmansThisYear && caWins > 2) streak++;
        //System.out.println(t.number + "'s final streak is " + streak);
        cpr = caWins + (eiWins * .75) + streak;
        setData(t, "cpr", cpr);
        //System.out.println(t.number + " CPR breakdown: " + caWins + " RCA wins, " + eiWins + " EI win(s), and a RCA/EI streak of " + streak + ".");
    }

    public static double getData(Team t, String key) {
        return teamData.get(t.number + key);
    }

    public static void setData(Team t, String key, double value) {
        teamData.put(t.number + key, value);
    }

    public static double roundToPlace(double d, double place) {
        double amount = 1;
        for (int i=0;i<place;i++) {
            amount *= 10;
        }
        return (double)Math.round(d * amount) / amount;
    }

    public static void main(String[] args) {
        new Main();
    }

    public static List<JSONObject> getObjects(JSONArray a) {
        List<JSONObject> objects = new ArrayList<>();
        for (int i=0; i<a.length(); i++) {
            JSONObject jO = a.getJSONObject(i);
            if (jO != null) objects.add(jO);
        }
        return objects;
    }

    public static JSONArray getArray(String s) {
        String html = getHTML(tbaAPI + s + tbaID);
        if (html != null) {
            return new JSONArray(html);
        } else {
            return null;
        }
    }

    public static JSONObject getObject(String s) {
        String html = getHTML(tbaAPI + s + tbaAPI);
        if (html != null) {
            return new JSONObject(html);
        } else {
            return null;
        }
    }

    public static String getHTML(String s) {
        try {
            s = s.replace(" ", "%20");

            URLConnection uc = new URL(s).openConnection();

            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

            InputStream is = uc.getInputStream();

            int ptr = 0;
            StringBuffer buffer = new StringBuffer();
            while ((ptr = is.read()) != -1) {
                buffer.append((char) ptr);
            }
            return buffer.toString();
        } catch (Exception e) {
            System.out.println("Error getting HTML for \"" + s + "\".");
            e.printStackTrace();
            return null;
        }
    }
}
