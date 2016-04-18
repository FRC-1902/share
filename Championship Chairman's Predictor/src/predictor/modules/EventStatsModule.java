package predictor.modules;

import predictor.graph.CSV;
import predictor.main.Main;
import predictor.main.Processing;
import predictor.main.Utils;
import predictor.tba.Award;
import predictor.tba.Event;
import predictor.tba.Team;
import java.util.ArrayList;
import java.util.List;

public class EventStatsModule implements Module {

    final int year;

    public EventStatsModule(int y) {
        year = y;
    }

    @Override
    public List<Team> getTeams() {
        return new ArrayList<>();
    }

    @Override
    public void processTeam(Team t) {}

    @Override
    public void finish() {
        CSV caWinners = new CSV();
        CSV averageCPR = new CSV();
        int event = 1;
        List<Event> events = Event.getEventsFrom(year);
        Utils.log("Starting to calculate event stats...there are " + events.size() + " events.");
        for (Event e : events) {
            if (e.official) {
                final Object WINNERS_USE = new Object();
                List<Team> winners = new ArrayList<>();

                Processing.processTeams(e.getTeams(), (t) -> {
                    if (t.hasWonAward(Award.CHAIRMANS)) {
                        synchronized (WINNERS_USE) {
                            winners.add(t);
                        }
                    }
                }, Main.threadsToUse, false);

                caWinners.addData(e.key, winners.size());
            /*
            EventCPRResult result = e.getCPRPredictions();
            double cprAvg = 0;
            for (EventCPRResult.TeamData d : result.getAllData()) {
                cprAvg += d.cpr;
            }
            cprAvg /= result.getAllData().size();
            averageCPR.addData(e.key, cprAvg);
            */
                Utils.log("Calculated for " + e.name + " (" + event + " / " + events.size() + ")");
            }
            event++;
        }
        caWinners.saveAs("winners.csv");
        Utils.log("Done!");
    }
}
