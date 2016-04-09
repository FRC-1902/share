package predictor.main;

import predictor.modules.*;
import predictor.modules.Module;
import predictor.tba.*;
import java.util.*;

public class Main {

    //private static final Module module = new EventModule("2014champs");
    //private static final Module module = new SingleTeamCPRModule(3880);
    //private static final Module module = new Champs4HFinderModule(2016);
    private static final Module module = new CPRGraphModule("cpr_graph", 1902, 1557, 987, 180, 27);
    //private static final Module module = new MultiEventModule("2016azpx", MultiEventModule.DisplayType.NORMAL);

    public static final String[] allDivisions = new String[]{"arc", "cars", "carv", "cur", "gal", "hop", "new", "tes"};
    public static final String[] originalDivisions = new String[]{"gal", "arc", "cur", "new"};

    public static final int thisYear = 2016;

    private static List<Team> teams = null;

    public static final int threadsToUse = 4;
    public static final int yearsBackwards = 3;

    public static void main(String args[]) {

        teams = module.getTeams();
        //Utils.log("Teams size: " + teams.size());

        double getStart = System.currentTimeMillis();

        Processing.processTeams(teams, (t) -> module.processTeam(t), threadsToUse, true);

        //Processing.threadedProcess(threadsToUse, "Team", Main::processTeams);

        module.finish();

        Utils.makeSeparator();
        double timeTaken = (System.currentTimeMillis() - getStart) / 1000;
        if (timeTaken > 60) { //Minutes
            String s = timeTaken + "";
            if (s.contains(".")) {
                //TODO: fix error
                String[] parts = s.split(".");
                int minutes = Integer.parseInt(parts[0]);
                double seconds = 60 * Double.parseDouble("0." + parts[1]);
                seconds = Utils.roundToPlace(seconds, 0);
                Utils.log("Time taken: " + minutes + ":" + seconds + " minutes.");
            } else {
                Utils.log("Time taken: " + (timeTaken / 60) + " minutes.");
            }
        } else { //Seconds
            Utils.log("Time taken: " + timeTaken + " seconds.");
        }
    }
}
