package predictor.modules;

import predictor.main.CPR;
import predictor.main.Utils;
import predictor.tba.Team;
import java.util.Collections;
import java.util.List;

public class SingleTeamCPRModule implements Module {

    Team t = null;

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
        Utils.log("talk");
        CPR.calculateComplexCPR(t, null, true);
        Utils.log("Team " + t.number + " (" + t.name + ") has a CPR of " + Utils.roundToPlace(t.cpr, 2) + ".");
    }
}
