package predictor.main;

import predictor.modules.EventModule;
import predictor.modules.Module;
import predictor.wrappers.*;
import java.util.*;

public class Main {

    //TODO: Look at Frog Force's 2015 Champs CPR and figure out why it is so high

    private static final Module module = new EventModule("2015champs");

    public static final String[] allDivisions = new String[]{"arc", "cars", "carv", "cur", "gal", "hop", "new", "tes"};
    public static final String[] originalDivisions = new String[]{"gal", "arc", "cur", "new"};

    private static List<Team> teams = null;
    private static List<Team> relevant = new ArrayList<>();

    private static int currentTeam = 0;
    private static int processedTeams = 0;
    private static final Object TEAMS_USE = new Object();

    private static final int threadsToUse = 30;
    public static int threadsDone = 0;

    public static void main(String args[]) {

        teams = module.getTeams();

        double getStart = System.currentTimeMillis();

        for (int i=0; i<threadsToUse; i++) {
            Thread thread = new Thread(Main::processTeams);
            thread.setName("Team Processing Thread #" + (i + 1));
            thread.start();
        }

        while (threadsDone < threadsToUse) {
            try {
                Thread.sleep(25);
            } catch (Exception ignored) {}
        }

        double secondsTaken = (System.currentTimeMillis() - getStart) / 1000;
        if (secondsTaken > 60) {
            Utils.log("Time taken: " + (secondsTaken / 60) + "m");
        } else {
            Utils.log("Time taken: " + secondsTaken + "s");
        }

        module.finish(relevant);
    }

    /**
     * Starts processing all the teams in the Team list.
     */
    public static void processTeams() {
        while (currentTeam < teams.size()) {
            Team t;
            synchronized (TEAMS_USE) {
                t = teams.get(currentTeam);
                currentTeam++;
            }
            boolean keep = module.processTeam(t);
            if (keep) {
                relevant.add(t);
            }
            processedTeams++;
            Utils.log("Processed team " + t.number + " - (" + processedTeams + " / " + teams.size() + ")");
            //TODO: bring back percentage of completion
        }
        threadsDone++;
    }
}
