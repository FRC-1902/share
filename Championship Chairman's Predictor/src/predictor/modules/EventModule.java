package predictor.modules;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.graph.BarGraph;
import predictor.main.*;
import predictor.graph.Graph;
import predictor.tba.Event;
import predictor.tba.Team;

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
        if (everyone) relevant.add(t);
        else if (t.isEligibleForChairmans(event)) relevant.add(t);
    }

    @Override
    public void finish() {
        if (complexCPR) {
            Utils.log("Starting calculation of simple CPR ratings...");

            Processing.processTeams(relevant, (Team t) -> {
                CPR.calculateSimpleCPR(t, event.date);
            }, Main.threadsToUse, true);

            for (Team t : new ArrayList<>(relevant)) {
                if (t.cpr == 0) relevant.remove(t);
            }

            Utils.log("Starting calculation of complex CPR ratings...");

            Processing.processTeams(relevant, (Team t) -> {
                CPR.calculateComplexCPR(t, event.date, false);
            }, Main.threadsToUse, true);
        } else {
            Processing.processTeams(relevant, (Team t) -> {
                CPR.calculateSimpleCPR(t, event.date);
            }, Main.threadsToUse, true);
        }

        /*
        relevant.forEach((t) -> {
            CPR.calculateSimpleCPR(t, event.date);
        });
        */
        for (Team t : new ArrayList<>(relevant)) {
            if (t.cpr == 0 || (everyone && t.isHOF(event.date))) relevant.remove(t);
        }
        BarGraph g = new BarGraph(event.name + " " + event.year + " CPR Ratings", "Team", "CPR");
        Collections.sort(relevant, CPR.cprComp);
        int pos = 1;
        Utils.makeSeparator();
        Utils.log("Found " + relevant.size() + " relevant teams at the " + year + " " + event.name + " (" + event.key + ")" + ". They are:");
        Utils.makeSeparator();
        for (Team t : relevant) {
            Utils.log(pos + ". " + t.number + " (" + t.name + ") - " + Utils.roundToPlace(t.cpr, 2) + " CPR");
            g.addData(t.name, "Team", t.cpr);
            pos++;
        }
        g.saveAs("output/" + event.key + "_graph.png");
    }
}