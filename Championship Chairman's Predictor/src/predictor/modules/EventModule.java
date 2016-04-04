package predictor.modules;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.main.CPR;
import predictor.main.Main;
import predictor.main.Processing;
import predictor.wrappers.Award;
import predictor.wrappers.Event;
import predictor.wrappers.Team;
import predictor.main.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventModule implements Module {

    String startEventKey;

    int year;
    boolean champs;
    boolean everyone;
    Event event;

    List<Team> teams = new ArrayList<>();
    List<Team> relevant = new ArrayList<>();

    final boolean complexCPR = true;

    /**
     * Creates an EventModule, which is a module that calculates CPR for the relevant Teams at a given Event.
     *
     * @param key The Event key. Use TBA event keys for normal events, "YEARchamps" for Championships, and "YEAReveryone" if you want all teams to be processed.
     */
    public EventModule(String key) {
        startEventKey = key;
        year = Integer.parseInt(startEventKey.substring(0, 4));
        champs = startEventKey.contains("champs");
        everyone = startEventKey.contains("everyone");
        event = null;
    }

    //TODO: getChampsEvent() function(?)

    @Override
    public List<Team> getTeams() {
        String eventName;
        if (champs) {
            event = Event.getEvent(year + (year == 2008 ? "ein" : "cmp"));
            eventName = year + " Championships";
            teams = Event.getTeamsAtChamps(year);
        } else if (everyone) {
            eventName = "All Teams " + year;
            event = Event.getEvent(year + (year == 2008 ? "ein" : "cmp"));
            System.out.println("Getting all the teams in the world...");
            for (int i = 0; i < 13; i++) {
                System.out.println("Page " + (i + 1));
                JSONArray jsonTeams = Utils.getArray("teams/" + i);
                for (JSONObject o : Utils.getObjects(jsonTeams)) {
                    teams.add(new Team(o));
                }
            }
        } else {
            event = Event.getEvent(startEventKey);
            teams = event.getTeams();
            eventName = event.name;
        }
        Utils.log(teams.size() + " teams at the " + eventName + ".");

        if (champs) {
            Utils.log("This event is the World Championship, so teams that have not won a RCA this year or have less than four RCA wins will be excluded (unless this is pre-2011).");
        } else if (everyone) {
            Utils.log("This is processing all teams, so any team with a CPR of 0 will be excluded.");
        } else if (event.type == Event.Type.DISTRICT_CHAMPIONSHIP) {
            Utils.log("This event is a District Championship, so teams that have not won a DCA in this District will be excluded.");
        } else {
            Utils.log("This event is a normal Regional or District, so any team with a CPR of 0 will be excluded.");
        }
        return teams;
    }

    @Override
    public void processTeam(Team t) {
        boolean rcaThisYear = false;
        boolean hof = false;
        List<Integer> dcaWins = new ArrayList<>();
        int rcaWins = 0;
        List<Award> awards = everyone ? t.getAllAwards() : t.getAwardsBefore(event);
        //Utils.log(t.number + " has " + awards.size() + " awards before " + event.date.toString());
        for (Award a : awards) {
            if (a.type == Award.CHAIRMANS) {
                if (!a.district) rcaWins++;
                else {
                    Event e = t.getEvent(a.eventKey);
                    dcaWins.add(e.districtID);
                }
                if (a.year == year && !a.district) rcaThisYear = true;
                if (a.eventKey.equals(a.year + "cmp")) hof = true;
            }
        }
        if (champs) {
            if ((rcaWins > 3 || year <= 2010) && rcaThisYear) relevant.add(t);
        } else if (everyone) {
            if (!hof) {
                relevant.add(t);
            } else {
                Utils.log("Removed " + t.number + " from consideration due to them being a Hall of Fame team.");
            }
        } else if (event.type == Event.Type.DISTRICT_CHAMPIONSHIP) {
            if (dcaWins.contains(event.districtID)) {
                relevant.add(t);
            }
        } else {
            if (!rcaThisYear && !hof) {
                relevant.add(t);
            } else if (rcaThisYear) {
                Utils.log("Removed " + t.number + " from consideration due to them having already won a Chairman's award this season.");
            } else if (hof) {
                Utils.log("Removed " + t.number + " from consideration due to them being a Hall of Fame team.");
            }
        }
    }

    @Override
    public void finish() {
        if (complexCPR) {
            for (Team t : new ArrayList<>(relevant)) {
                CPR.calculateSimpleCPR(t, event.date);
                if (t.cpr == 0) relevant.remove(t);
            }
            Processing.processTeams(relevant, (Team t) -> {
                CPR.calculateComplexCPR(t, event.date);
            }, Main.threadsToUse);
        } else {
            relevant.forEach((t) -> CPR.calculateSimpleCPR(t, event.date));
        }

        /*
        relevant.forEach((t) -> {
            CPR.calculateSimpleCPR(t, event.date);
        });
        */
        for (Team t : new ArrayList<>(relevant)) {
            if (t.cpr == 0) relevant.remove(t);
        }
        Collections.sort(relevant, CPR.cprComp);
        int pos = 1;
        System.out.println("------------------------");
        System.out.println("Found " + relevant.size() + " relevant teams at the " + year + " " + event.name + ". They are:");
        for (Team t : relevant) {
            Utils.log(pos + ". " + t.number + " (" + t.name + ") - " + Utils.roundToPlace(t.cpr, 2) + " CPR");
            pos++;
        }
    }
}