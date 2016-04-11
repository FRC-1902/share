package predictor.modules;

import predictor.main.*;
import predictor.stuff.EventCPRResult;
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

            EventCPRResult result = e.getCPRPredictions(teams);

            if (type == DisplayType.NORMAL) {
                m.add(result.getString());
            } else if (type == DisplayType.SLACK) {
                m.add(result.getSlackString());
            }
        }
        Utils.log(m.getMessage());

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
