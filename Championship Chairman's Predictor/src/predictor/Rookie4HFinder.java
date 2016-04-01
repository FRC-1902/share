package predictor;

import org.json.JSONArray;
import predictor.wrappers.Event;
import predictor.wrappers.Team;
import predictor.wrappers.Utils;

import java.util.ArrayList;
import java.util.List;

public class Rookie4HFinder {

    private static final String[] allDivisions = new String[]{"arc", "cars", "carv", "cur", "gal", "hop", "new", "tes"};
    public static final String[] originalDivisions = new String[]{"gal", "arc", "cur", "new"};

    private static final int year = 2016;
    private static boolean useDivisions = false;

    public static void main(String[] args) {
        List<Team> champsTeams = new ArrayList<>();
        if (useDivisions) {
            System.out.println("Reading teams from divisions...");
            String[] divs;
            if (year <= 2014) divs = originalDivisions;
            else divs = allDivisions;
            for (String s : divs) {
                Event e = new Event(year + s);
                for (Team t : e.getTeams()) {
                    champsTeams.add(t);
                }
            }
        } else {
            Event e = new Event(year + "cmp");
            champsTeams = e.getTeams();
        }

        System.out.println("Starting scan for 4-H teams...");
        for (Team t : champsTeams) {
            String full = t.fullName.toLowerCase();
            if (full.contains("4h") || full.contains("4-h")) {
                System.out.println(t.number + " (" + t.name + ") is a 4-H team!");
            }
            //System.out.println(t.fullName.toLowerCase());
        }
        JSONArray events = Utils.getArray("events/2016");
        System.out.println(events.length() + " events this year");
    }
}
