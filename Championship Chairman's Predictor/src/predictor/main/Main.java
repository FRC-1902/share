package predictor.main;

import predictor.modules.CPRGraphModule;
import predictor.modules.Module;
import predictor.wrappers.*;
import java.util.*;

public class Main {

    //private static final Module module = new EventModule("2016champs");
    //private static final Module module = new SingleTeamCPRModule(11);
    //private static final Module module = new Champs4HFinderModule(2015);
    private static final Module module = new CPRGraphModule("2016_champs_team_history", 1241, 932, 2468, 1902, 2486, 987, 2614, 604, 1511, 3132, 537, 1868);

    public static final String[] allDivisions = new String[]{"arc", "cars", "carv", "cur", "gal", "hop", "new", "tes"};
    public static final String[] originalDivisions = new String[]{"gal", "arc", "cur", "new"};

    public static final int thisYear = 2016;

    private static List<Team> teams = null;

    public static final int threadsToUse = 3;
    public static final int yearsBackwards = 3;

    public static void main(String args[]) {

        teams = module.getTeams();
        Utils.log("Teams size: " + teams.size());

        double getStart = System.currentTimeMillis();

        Processing.processTeams(teams, (t) -> module.processTeam(t), threadsToUse);

        //Processing.threadedProcess(threadsToUse, "Team", Main::processTeams);

        double secondsTaken = (System.currentTimeMillis() - getStart) / 1000;
        if (secondsTaken > 60) {
            Utils.log("Time taken: " + (secondsTaken / 60) + "m");
        } else {
            Utils.log("Time taken: " + secondsTaken + "s");
        }

        module.finish();
    }
}
