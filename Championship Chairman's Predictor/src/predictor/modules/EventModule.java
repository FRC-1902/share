package predictor.modules;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.main.CPR;
import predictor.main.Main;
import predictor.wrappers.Award;
import predictor.wrappers.Event;
import predictor.wrappers.Team;
import predictor.main.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventModule implements Module {

    private static String startEventKey;

    private static int year;
    private static boolean champs;
    private static boolean everyone;
    private static Event event;

    private static List<Team> teams = new ArrayList<>();

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

    @Override
    public List<Team> getTeams() {
        String eventName;
        if (champs) {
            event = new Event(year + "cmp");
            event.initInfo();
            eventName = year + " Championships";
            teams = Event.getTeamsAtChamps(year);
        } else if (everyone) {
            eventName = "All Teams " + year;
            event = new Event(year + "cmp");
            event.initInfo();
            System.out.println("Getting all the teams in the world...");
            for (int i = 0; i < 13; i++) {
                System.out.println("Page " + (i + 1));
                JSONArray jsonTeams = Utils.getArray("teams/" + i);
                for (JSONObject o : Utils.getObjects(jsonTeams)) {
                    teams.add(new Team(o));
                }
            }
        } else {
            event = new Event(startEventKey);
            event.initInfo();
            teams = event.getTeams();
            eventName = event.name;
        }
        Utils.log(teams.size() + " teams at the " + eventName + ".");

        if (champs) {
            Utils.log("This event is the World Championship, so teams that have not won a RCA this year or have less than four RCA wins will be excluded.");
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
    public boolean processTeam(Team t) {
        boolean good = false;
        boolean caThisYear = false;
        boolean hof = false;
        List<Integer> dcaWins = new ArrayList<>();
        int caWins = 0;
        List<Award> awards = everyone ? t.getAllAwards() : t.getAwardsBefore(event);
        for (Award a : awards) {
            if (a.type == Award.CHAIRMANS && (!a.district || event.district)) {
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
            if (caWins > 3 && caThisYear) good = true;
        } else if (everyone) {
            if (!hof) {
                good = true;
            } else {
                Utils.log("Removed " + t.number + " from consideration due to them being a Hall of Fame team.");
            }
        } else if (event.type == Event.Type.DISTRICT_CHAMPIONSHIP) {
            if (dcaWins.contains(event.districtID)) {
                good = true;
            }
        } else {
            if (!caThisYear && !hof) {
                good = true;
            } else if (caThisYear) {
                Utils.log("Removed " + t.number + " from consideration due to them having already won a Chairman's award this season.");
            } else if (hof) {
                Utils.log("Removed " + t.number + " from consideration due to them being a Hall of Fame team.");
            }
        }
        return good;
    }

    @Override
    public void finish(List<Team> relevant) {
        relevant.forEach((t) -> {
            CPR.calculateCPR(t, event.date);
        });
        for (Team t : new ArrayList<>(relevant)) {
            if (t.cpr == 0) relevant.remove(t);
        }
        Collections.sort(relevant, CPR.cprComp);
        int pos = 1;
        System.out.println("------------------------");
        System.out.println("Found " + relevant.size() + " relevant teams. They are:");
        for (Team t : relevant) {
            Utils.log(pos + ". " + t.number + " (" + t.name + ") - " + Utils.roundToPlace(t.cpr, 2) + " CPR");
            pos++;
        }
    }
}