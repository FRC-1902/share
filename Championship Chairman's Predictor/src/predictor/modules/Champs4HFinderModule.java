package predictor.modules;

import predictor.main.Utils;
import predictor.tba.Event;
import predictor.tba.Team;

import java.util.ArrayList;
import java.util.List;

public class Champs4HFinderModule implements Module {

    final int year;
    List<Team> relevant = new ArrayList<>();

    public Champs4HFinderModule(int y) {
        year = y;
    }

    @Override
    public List<Team> getTeams() {
        return Event.getTeamsAtChamps(year);
    }

    @Override
    public void processTeam(Team t) {
        String full = t.fullName.toLowerCase();
        if (full.contains("4h") || full.contains("4-h") || full.contains("4 h")) {
            relevant.add(t);
        }
    }

    @Override
    public void finish() {
        Utils.log("Found " + relevant.size() + " 4-H teams! They are:");
        for (Team t : relevant) {
            Utils.log(t.number + " (" + t.name + ")");
        }
    }
}
