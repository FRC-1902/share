package predictor.tba;

import predictor.main.Main;
import predictor.main.Utils;
import java.util.ArrayList;
import java.util.List;

public class Championship extends Event {

    private List<Event> divisions = new ArrayList<>();

    public Championship(int year) {
        super(year + "cmp");
        initInfo();
        name = "FIRST Championship";
        shortName = "Champs";
        String[] divs;
        if (year <= 2014) divs = Main.originalDivisions;
        else divs = Main.allDivisions;
        for (String s : divs) {
            Event e = Event.getEvent(year + s);
            divisions.add(e);
        }
    }

    @Override
    public List<Team> getTeams() {
        if (teams == null) {
            if (year <= 2015) { //TODO: change this once divisions come out this year
                teams = new ArrayList<>();
                Utils.log("Reading teams from Championship divisions...");
                for (Event e : divisions) {
                    Utils.log(e.shortName + "...");
                    for (Team t : e.getTeams()) {
                        teams.add(t);
                    }
                }
            } else {
                teams = super.getTeams();
            }
        }
        return new ArrayList<>(teams);
    }

    @Override
    public List<Award> getAwards() {
        if (awards == null) {
            awards = super.getAwards();
            for (Event e : divisions) {
                for (Award a : e.getAwards()) {
                    awards.add(a);
                }
            }
        }
        return new ArrayList<>(awards);
    }
}
