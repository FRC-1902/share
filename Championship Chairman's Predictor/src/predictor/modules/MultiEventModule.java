package predictor.modules;

import predictor.main.*;
import predictor.tba.Event;
import predictor.tba.Team;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiEventModule implements Module {

    Event sampleEvent;
    DisplayType type;

    public MultiEventModule(String key, DisplayType t) {
        sampleEvent = Event.getEvent(key);
        type = t;
    }

    @Override
    public List<Team> getTeams() {
        return new ArrayList<>();
    }

    @Override
    public void processTeam(Team t) {}

    @Override
    public void finish() {
        List<Event> concurrent = Event.getConcurrentEvents(sampleEvent);
        Utils.log("Found " + concurrent.size() + " concurrent events.");
        Message m = new Message();
        int current = 0;
        for (Event e : concurrent) {
            current++;
            Utils.makeSeparator();
            Utils.log("Processing \"" + e.name + "\" (" + e.key + "). (" + current + " / " + concurrent.size() + ")");
            Utils.makeSeparator();
            List<Team> teams = e.getTeams();

            Utils.log("Filtering teams...");
            for (Team t : new ArrayList<>(teams)) {
                if (!t.isEligibleForChairmans(e)) teams.remove(t);
            }

            Utils.log("Calculating simple CPRs...");
            Processing.processTeams(teams, (Team t) -> {
                CPR.calculateSimpleCPR(t, e.date);
            }, Main.threadsToUse, true);

            Utils.log("Filtering more teams...");
            for (Team t : new ArrayList<>(teams)) {
                if (t.cpr == 0 || t.isHOF(e.date)) teams.remove(t);
            }

            Utils.log("Calculating complex CPRs...");
            Processing.processTeams(teams, (Team t) -> {
                CPR.calculateComplexCPR(t, e.date, false);
            }, Main.threadsToUse, true);


            Collections.sort(teams, CPR.cprComp);

            if (type == DisplayType.NORMAL) {
                int pos = 1;
                m.add("");
                m.add("");
                m.addSeparator();
                m.add("Found " + teams.size() + " relevant teams at the " + e.year + " " + e.name + " (" + e.key + ")" + ". They are:");
                m.addSeparator();
                for (Team t : teams) {
                    m.add(pos + ". " + t.number + " (" + t.name + ") - " + Utils.roundToPlace(t.cpr, 2) + " CPR");
                    pos++;
                }
            } else if (type == DisplayType.SLACK) {
                int pos = 1;
                m.add("");
                m.add(e.name + ":");
                m.add("```");
                for (Team t : teams) {
                    if (pos < 4) {
                        m.add(pos + ". " + t.number + " (" + t.name + ") - " + Utils.roundToPlace(t.cpr, 2) + " CPR");
                        pos++;
                    }
                }
                m.add("```");
            }
        }
        try {
            File file = new File("output/event_output.txt");
            if (file.exists()) file.delete();
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(m.getMessage());
            writer.flush();
            writer.close();
            Utils.log("Multievent calculations complete!");
        } catch (Exception e) {
            Utils.log("MultiEventModule file saving step error!");
            e.printStackTrace();
        }
    }

    public enum DisplayType {
        NORMAL,
        SLACK
    }
}
