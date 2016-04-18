package predictor.stuff;

import predictor.main.Message;
import predictor.main.Utils;
import predictor.tba.Event;
import predictor.tba.Team;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventCPRResult implements Serializable {

    private Event event;
    private List<TeamData> data = new ArrayList<>();

    public EventCPRResult(Event e, List<Team> teams) {
        event = e;
        int rank = 1;
        for (Team t : teams) {
            data.add(new TeamData(t, rank, t.cpr));
            t.cpr = 0;
            rank++;
        }
    }

    public Event getEvent() {
        return event;
    }

    public TeamData getData(Team t) {
        for (TeamData d : data) {
            if (d.team.equals(t)) {
                return d;
            }
        }
        return null;
    }

    public List<TeamData> getAllData() {
        return new ArrayList<>(data);
    }

    public boolean contains(Team t) {
        return getTeams().contains(t);
    }

    public List<Team> getTeams() {
        List<Team> teams = new ArrayList<>();
        for (TeamData d : data) {
            teams.add(d.team);
        }
        return teams;
    }

    public int getHighestRank() {
        return getTeams().size();
    }

    public String getString() {
        Message m = new Message();
        m.add("Found " + data.size() + " relevant teams at the " + event.year + " " + event.name + " (" + event.key + ")" + ". They are:");
        m.addSeparator();
        for (TeamData d : data) {
            m.add(d.rank + ". " + d.team.number + " (" + d.team.name + ") - " + Utils.roundToPlace(d.cpr, 2) + " CPR");
        }
        m.add("");
        return m.getMessage();
    }

    public String getSlackString() {
        Message m = new Message();
        int pos = 1;
        m.add(event.shortName + ":");
        m.add("```");
        for (TeamData d : data) {
            if (pos < (event.districtChamps ? 5 : 4)) { //Since district champs give out more RCA/EI, include rank 4 if this is a district championship
                m.add(pos + ". " + d.team.number + " (" + d.team.name + ") - " + Utils.roundToPlace(d.cpr, 2) + " CPR");
                pos++;
            }
        }
        m.add("```");
        return m.getMessage();
    }


    public class TeamData implements Serializable {
        public final Team team;
        public final int rank;
        public final double cpr;

        public TeamData(Team t, int r, double c) {
            team = t;
            rank = r;
            cpr = c;
        }
    }
}
