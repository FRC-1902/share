package predictor.modules;

import predictor.graph.CSV;
import predictor.graph.LineGraph;
import predictor.main.*;
import predictor.tba.Team;
import java.io.File;
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
            CSV csv = new CSV();

            File graphFile = new File("output/" + name + "_image.png");
            graphFile.mkdirs();
            if (graphFile.exists()) graphFile.delete();
            LineGraph g = new LineGraph("Team CPRs", "Year", "CPR");

            int currYear = Integer.MAX_VALUE;
            for (Team t : teams) {
                if (t.rookieYear < currYear) {
                    currYear = t.rookieYear;
                }
            }
            boolean lastWasAllZeros = false;
            while (currYear <= Main.thisYear) {
                csv.addData("Year", currYear); //csv
                //String values = currYear + "";
                final int currCurrYear = currYear;
                Processing.processTeams(teams, (t) -> {
                    CPR.calculateComplexCPR(t, Utils.makeDate(currCurrYear + "-12-31"), true);
                }, Main.threadsToUse, true);

                Utils.log(currYear + " complete!");

                int zeros = 0;
                for (Team t : teams) {
                    csv.addData(t.name, t.cpr); //csv
                    //values = values + ", " + t.cpr;
                    if (t.cpr == 0) zeros++;
                }
                if (zeros < teams.size()) {
                    for (Team t : teams) {
                        g.addData(t.name, currYear, t.cpr);
                        if (lastWasAllZeros) g.addData(t.name, currYear - 1, 0);
                    }
                    if (lastWasAllZeros) lastWasAllZeros = false;
                } else {
                    lastWasAllZeros = true;
                }
                //writer.write(values + "\n");
                currYear++;
            }
            csv.saveAs("output/" + name + ".csv");
            //writer.flush();
            //writer.close();
            Utils.log("Graph path: " + graphFile.getPath());
            g.saveAs(graphFile.getPath());
            //Utils.log(name + ".csv and " + name + "_graph.png has been generated.");
        } catch (Exception e) {
            Utils.log("CPRGraphModule.finish() exception!");
            e.printStackTrace();
        }
    }
}
