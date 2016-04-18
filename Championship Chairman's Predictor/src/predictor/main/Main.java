package predictor.main;

import predictor.modules.Module;
import predictor.modules.*;
import predictor.tba.*;
import java.util.*;

public class Main {

    //TODO: Change modules to not have the weird multiple functions.
    //TODO: Possibly merge the functionality of some of the "Multi" modules with their singular counterparts.

    private static final Module module =
            new EventModule("2015cmp");
    //        new MultiEventModule("2016gacmp", MultiEventModule.DisplayType.NORMAL);
    //        new MultiEventResultModule("2016gacmp");
    //        new Champs4HFinderModule(2016);
    //        new EventStatsModule(2016);
    //        new SingleTeamCPRModule(4013);

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
        /*
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
        */
    }
}
