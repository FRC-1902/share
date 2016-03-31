package predictor;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.wrappers.Award;
import predictor.wrappers.Event;
import predictor.wrappers.Team;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Main {

    private static final String tbaAPI = "https://www.thebluealliance.com/api/v2/";
    private static final String tbaID = "?X-TBA-App-Id=frc1902:chairmans_predictions:v0.1";
    private static HashMap<String, Double> teamData = new HashMap<>();
    private static final String[] allDivisions = new String[]{"arc", "cars", "carv", "cur", "gal", "hop", "new", "tes"};
    public static final String[] originalDivisions = new String[]{"gal", "arc", "cur", "new"};

    private static final int year = 2016;
    private static final boolean useDivisions = false;

    public static final int CHAIRMANS = 0;
    public static final int ENGINEERING_INSPIRATION = 9;

    public Main() {
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
        List<Team> competitors = new ArrayList<>();
        System.out.println(champsTeams.size() + " teams at " + year + " champs, finding that year's Chairman's winners...");
        double startMillis = System.currentTimeMillis();
        for (Team t : champsTeams) {
            double chairmanWins = 0;
            for (Award a : t.getAllAwards()) {
                if (a.type == CHAIRMANS && !a.name.contains("District")) {
                    int aYear = Integer.parseInt(a.event.substring(0, 4));
                    //System.out.println(aYear);
                    if (aYear <= year) {
                        chairmanWins++;
                        if (a.event.contains(year + "")) {
                            if (!competitors.contains(t)) {
                                competitors.add(t);
                                System.out.println("" + t.number);
                            }
                        }
                    }
                }
            }
            teamData.put(t.number + "chairmans", chairmanWins);
        }
        System.out.println("Time taken to find winners: " + ((System.currentTimeMillis() - startMillis) / 1000) + " seconds");
        System.out.println(competitors.size() + " " + year + " Chairman's winners found. Finding competitors...");
        for (Team t : new ArrayList<>(competitors)) {
            double chairmanWins = teamData.get(t.number + "chairmans");
            if (chairmanWins < 4) {
                competitors.remove(t);
            }
        }

        competitors.forEach(Main::calculateCPR);

        final Comparator<Team> comp = (t1, t2) -> {
            Double data = getData(t2, "cpr") - getData(t1, "cpr");
            data *= 1000;
            return data.intValue();
        };

        Collections.sort(competitors, comp);

        System.out.println(competitors.size() + " competitors found. They are:");
        int pos = 1;
        for (Team t : competitors) {
            System.out.println(pos + ". " + t.number + " (" + t.name + ") - " + roundToPlace(getData(t, "cpr"), 2) + " CPR");
            pos++;
        }
    }

    public static void calculateCPR(Team t) {
        double cpr;
        double caWins = 0;
        double eiWins = 0;
        for (Award a : t.getAllAwards()) {
            if (a.year == year || a.year == year - 1 || a.year == year - 2 || a.year == year - 3) {
                if (a.type == CHAIRMANS) caWins++;
                if (a.type == ENGINEERING_INSPIRATION) eiWins++;
            }
        }
        int streak = 0;
        int currYear = year - 1;
        while (true) {
            boolean streakOver = true;
            for (Award a : t.getAllAwards()) {
                if (a.year == currYear) {
                    if (a.type == CHAIRMANS || a.type == ENGINEERING_INSPIRATION) {
                        streak++;
                        currYear--;
                        streakOver = false;
                        break;
                    }
                }
            }
            if (streakOver) break;
        }
        cpr = caWins + (eiWins * .75) + streak;
        setData(t, "cpr", cpr);
        //System.out.println(t.number + " CPR breakdown: " + caWins + " RCA wins, " + eiWins + " EI win(s), and a RCA/EI streak of " + streak + ".");
    }

    public static double getData(Team t, String key) {
        return teamData.get(t.number + key);
    }

    public static void setData(Team t, String key, double value) {
        teamData.put(t.number + key, value);
    }

    public static double roundToPlace(double d, double place) {
        double amount = 1;
        for (int i=0;i<place;i++) {
            amount *= 10;
        }
        return (double)Math.round(d * amount) / amount;
    }

    public static void main(String[] args) {
        new Main();
    }

    public static List<JSONObject> getObjects(JSONArray a) {
        List<JSONObject> objects = new ArrayList<>();
        for (int i=0; i<a.length(); i++) {
            JSONObject jO = a.getJSONObject(i);
            if (jO != null) objects.add(jO);
        }
        return objects;
    }

    public static JSONArray getArray(String s) {
        String html = getHTML(tbaAPI + s + tbaID);
        if (html != null) {
            return new JSONArray(html);
        } else {
            return null;
        }
    }

    public static JSONObject getObject(String s) {
        String html = getHTML(tbaAPI + s + tbaAPI);
        if (html != null) {
            return new JSONObject(html);
        } else {
            return null;
        }
    }

    public static String getHTML(String s) {
        try {
            s = s.replace(" ", "%20");

            URLConnection uc = new URL(s).openConnection();

            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

            InputStream is = uc.getInputStream();

            int ptr = 0;
            StringBuffer buffer = new StringBuffer();
            while ((ptr = is.read()) != -1) {
                buffer.append((char) ptr);
            }
            return buffer.toString();
        } catch (Exception e) {
            System.out.println("Error getting HTML for \"" + s + "\".");
            e.printStackTrace();
            return null;
        }
    }
}
