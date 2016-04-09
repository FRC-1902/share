package predictor.main;

import predictor.tba.Team;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Processing {

    //TODO: figure out occasional freeze on the last team to be processed

    public static void processTeams(List<Team> teams, Consumer<Team> teamProcessor, int threads, boolean verbose) {
        TeamProcessTracker tracker = new TeamProcessTracker(teams);
        threadedProcess(threads, "Team", (con) -> {
            doProcessTeams(teamProcessor, con, verbose);
        }, tracker);
    }

    private static void doProcessTeams(Consumer<Team> teamProcessor, ThreadProcessTracker threadTracker, boolean verbose) {
        TeamProcessTracker tracker = (TeamProcessTracker) threadTracker;
        int currentTeam;
        synchronized (tracker.TRACKER_USE) {
            currentTeam = tracker.currentTeam;
        }
        while (currentTeam < tracker.teams.size()) { //TODO: synchronize tracker.currentTeam?
            Team t = null;
            boolean stillGo;
            synchronized (tracker.TEAMS_USE) {
                stillGo = tracker.currentTeam < tracker.teams.size();
                if (stillGo) {
                    t = tracker.teams.get(tracker.currentTeam);
                    tracker.currentTeam++;
                }
            }
            if (stillGo) {
                teamProcessor.accept(t);

                int processedTeams;
                synchronized (tracker.TRACKER_USE) {
                    tracker.processedTeams++;
                    processedTeams = tracker.processedTeams;
                }

                if (verbose) {
                    Double percent = processedTeams / (tracker.teams.size() * 1.0);
                    percent = Utils.roundToPlace(percent * 100, 0);
                    Utils.log("Processed team " + t.number + " - (" + processedTeams + " / " + tracker.teams.size() + ") " + percent.intValue() + "%");
                }
            }
            synchronized (tracker.TRACKER_USE) {
                currentTeam = tracker.currentTeam;
            }
        }
        synchronized (tracker.TRACKER_USE) {
            tracker.threadsDone++;
        }
    }


    public static void threadedProcess(int threads, String name, Consumer<ThreadProcessTracker> con) {
        threadedProcess(threads, name, con, new ThreadProcessTracker());
    }

    public static void threadedProcess(int threads, String name, Consumer<ThreadProcessTracker> con, ThreadProcessTracker tracker) {
        for (int i=0; i<threads; i++) {
            Thread thread = new Thread(() -> con.accept(tracker));
            thread.setName(name + " Processing Thread #" + (i + 1));
            thread.start();
        }

        while (tracker.threadsDone < threads) {
            try {
                Thread.sleep(25);
            } catch (Exception e) {
                Utils.log("Processing.threadedProcess() wait exception!");
                e.printStackTrace();
            }
        }
    }

    public static class ThreadProcessTracker {
        public int threadsDone = 0;
    }

    public static class TeamProcessTracker extends ThreadProcessTracker {
        public final Object TRACKER_USE = new Object();
        public List<Team> teams;
        public final Object TEAMS_USE = new Object();
        public int currentTeam = 0;
        public int processedTeams = 0;

        public TeamProcessTracker(List<Team> t) {
            teams = new ArrayList<>(t);
        }
    }
}
