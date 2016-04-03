package predictor.modules;

import predictor.main.Utils;
import predictor.wrappers.Event;
import predictor.wrappers.Team;

import java.util.List;

public class Champs4HFinderModule implements Module {

    final int year;

    public Champs4HFinderModule(int y) {
        year = y;
    }

    @Override
    public List<Team> getTeams() {
        return Event.getTeamsAtChamps(year);
    }

    @Override
    public boolean processTeam(Team t) {
        String full = t.fullName.toLowerCase();
        if (full.contains("4h") || full.contains("4-h") || full.contains("4 h")) {
            return true;
        }
        return false;
    }

    @Override
    public void finish(List<Team> relevant) {
        for (Team t : relevant) {
            Utils.log(t.number + " (" + t.name + ") is a 4-H team!");
        }
    }
}
