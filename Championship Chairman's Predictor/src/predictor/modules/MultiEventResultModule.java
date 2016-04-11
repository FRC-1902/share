package predictor.modules;

import predictor.main.Utils;
import predictor.stuff.EventCPRResult;
import predictor.tba.Award;
import predictor.tba.Event;
import predictor.tba.Team;
import java.util.ArrayList;
import java.util.List;

public class MultiEventResultModule implements Module {

    Event sample;

    public MultiEventResultModule(String key) {
        sample = Event.getEvent(key);
    }

    @Override
    public List<Team> getTeams() {
        return new ArrayList<>();
    }

    @Override
    public void processTeam(Team t) {}

    @Override
    public void finish() {
        List<Event> c = Event.getConcurrentEvents(sample);
        Utils.log("Concurrent events: " + c.size());
        for (Event e : c) {
            Utils.log(e.name + " " + e.year + ":");
            Utils.makeSeparator();
            List<Team> chairmans = e.getWinnersOf(Award.CHAIRMANS);
            List<Team> ei = e.getWinnersOf(Award.ENGINEERING_INSPIRATION);
            List<Integer> accuracies = new ArrayList<>();
            if (Utils.exists("output/results/" + e.key + ".event")) {
                EventCPRResult result = (EventCPRResult) Utils.deserialize("results/" + e.key + ".event");
                int caWins = chairmans.size();
                int eiWins = ei.size();
                if (caWins > 0) {
                    List<Integer> caRanks = new ArrayList<>();
                    for (int i=1; i<= caWins; i++) {
                        caRanks.add(i);
                    }
                    for (Team t : chairmans) {
                        if (result.contains(t)) {
                            EventCPRResult.TeamData d = result.getData(t);
                            if (caRanks.contains(d.rank)) {
                                Utils.log(t.number + " (" + t.name + ") won Chairman's and was rank " + d.rank + ". 100% accuracy.");
                                accuracies.add(100);
                            } else {
                                double diff = Math.abs(d.rank - caRanks.get(caRanks.size() - 1));
                                Double accuracy = diff / (result.getHighestRank() * 1.0);
                                accuracy = Utils.roundToPlace(accuracy * 100, 0);
                                int accInt = (100 - (accuracy.intValue()) / 2);
                                Utils.log(t.number + " (" + t.name + ") won Chairman's and was rank " + d.rank + ". " + accInt + "% accuracy.");
                                accuracies.add(accInt);
                            }
                        } else {
                            Utils.log(t.number + " (" + t.name + ") won Chairman's and was not on the ranking list at all. 0% accuracy.");
                            accuracies.add(0);
                        }
                    }
                }
                if (eiWins > 0) {
                    List<Integer> eiRanks = new ArrayList<>();
                    for (int i=caWins + 1; i <= eiWins + caWins; i++) {
                        eiRanks.add(i);
                    }
                    for (Team t : ei) {
                        if (result.contains(t)) {
                            EventCPRResult.TeamData d = result.getData(t);
                            if (eiRanks.contains(d.rank)) {
                                Utils.log(t.number + " (" + t.name + ") won E.I. and was rank " + d.rank + ". 100% accuracy.");
                                accuracies.add(100);
                            } else {
                                double diff = Math.abs(d.rank - eiRanks.get(eiRanks.size() - 1));
                                Double accuracy = diff / (result.getHighestRank() * 1.0);
                                accuracy = Utils.roundToPlace(accuracy * 100, 0);
                                int accInt = (100 - (accuracy.intValue()) / 2);
                                Utils.log(t.number + " (" + t.name + ") won E.I. and was rank " + d.rank + ". " + accInt + "% accuracy.");
                                accuracies.add(accInt);
                            }
                        } else {
                            Utils.log(t.number + " (" + t.name + ") won E.I. and was not on the ranking list at all. 0% accuracy.");
                            accuracies.add(0);
                        }
                    }
                }
            } else {
                for (Team t : chairmans) {
                    Utils.log(t.number + " (" + t.name + ") won Chairman's.");
                }
                for (Team t : ei) {
                    Utils.log(t.number + " (" + t.name + ") won Engineering Inspiration.");
                }
            }
            if (!accuracies.isEmpty()) {
                int accSum = 0;
                for (int i : accuracies) {
                    accSum += i;
                }
                Utils.makeSeparator();
                Utils.log("Overall accuracy: " + (accSum / accuracies.size()) + "%");
            }
            Utils.makeSeparator();
            Utils.log("");
        }
    }
}
