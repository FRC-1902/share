package predictor.modules;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.main.*;
import predictor.stuff.EventCPRResult;
import predictor.tba.Award;
import predictor.tba.Event;
import predictor.tba.Team;
import java.util.ArrayList;
import java.util.List;

public class EventModule implements Module {

    String startEventKey;

    int year;
    boolean champs;
    boolean everyone;
    Event event;

    List<Team> teams = new ArrayList<>();
    List<Team> relevant = new ArrayList<>();

    /**
     * Creates an EventModule, which is a module that calculates CPR for the relevant Teams at a given Event.
     *
     * @param key The Event key. Use TBA event keys for normal events and "YEAReveryone" if you want all teams to be processed.
     */
    public EventModule(String key) {
        startEventKey = key;
        year = Integer.parseInt(startEventKey.substring(0, 4));
        champs = startEventKey.equalsIgnoreCase(year + "cmp");
        everyone = startEventKey.contains("everyone");
        event = null;
    }

    @Override
    public List<Team> getTeams() {
        String eventName;
        if (champs) {
            event = Event.getChampionship(year);
            eventName = event.name;
            //teams = event.getTeams();
            Utils.log("Getting the RCA winners at champs...");
            int index = 1;
            List<Event> events = Event.getEventsBefore(event.date);
            for (Event e : events) {
                if (!e.district) {
                    Utils.log("Processing " + e.shortName + " (" + index + " / " + events.size() + ")");
                    for (Team t : e.getWinnersOf(Award.CHAIRMANS)) {
                        if (!teams.contains(t)) teams.add(t);
                    }
                }
                index++;
            }
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
        EventCPRResult result = event.getCPRPredictions(relevant);
        Utils.log(result.getString());
    }
}