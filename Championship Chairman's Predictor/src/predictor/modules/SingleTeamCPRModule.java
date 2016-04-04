package predictor.modules;

import predictor.main.CPR;
import predictor.main.Utils;
import predictor.wrappers.Team;
import java.util.Collections;
import java.util.List;

public class SingleTeamCPRModule implements Module {

    Team t;

    public SingleTeamCPRModule(int teamNum) {
        t = Team.getTeam(teamNum);
    }

    @Override
    public List<Team> getTeams() {
        return Collections.singletonList(t);
    }

    @Override
    public void processTeam(Team t) {}

    @Override
    public void finish() {
        CPR.calculateComplexCPR(t, null);
        Utils.log("Team " + t.number + " (" + t.name + ") has a CPR of " + Utils.roundToPlace(t.cpr, 2) + ".");
    }
}
