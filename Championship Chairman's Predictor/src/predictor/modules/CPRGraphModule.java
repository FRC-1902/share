package predictor.modules;

import predictor.main.CPR;
import predictor.main.Main;
import predictor.main.Processing;
import predictor.main.Utils;
import predictor.wrappers.Team;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class CPRGraphModule implements Module {

    List<Team> teams = new ArrayList<>();
    String name;

    public CPRGraphModule(String na, int...numbers) {
        name = na;
        for (int n : numbers) {
            teams.add(Team.getTeam(n));
        }
    }

    @Override
    public List<Team> getTeams() {
        return teams;
    }

    @Override
    public void processTeam(Team t) {}

    @Override
    public void finish() {
        try {
            File file = new File(name + ".csv");
            if (file.exists()) file.delete();
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            String valueKey = "Year, ";
            int currYear = Integer.MAX_VALUE;
            for (Team t : teams) {
                valueKey = valueKey + ", \"" + t.name + "\"";
                if (t.rookieYear < currYear) {
                    currYear = t.rookieYear;
                }
            }
            writer.write(valueKey + "\n");
            while (currYear <= Main.thisYear) {
                String values = currYear + ", ";
                final int currCurrYear = currYear;
                Processing.processTeams(teams, (t) -> {
                    CPR.calculateComplexCPR(t, Utils.makeDate(currCurrYear + "-12-31"));
                }, Main.threadsToUse);

                Utils.log(currYear + " complete!");

                for (Team t : teams) {
                    values = values + ", " + t.cpr;
                }
                writer.write(values + "\n");
                currYear++;
            }
            writer.flush();
            writer.close();
            Utils.log(name + ".csv has been generated.");
        } catch (Exception e) {
            Utils.log("CPRGraphModule.finish() exception!");
            e.printStackTrace();
        }
    }
}
