package predictor.wrappers;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.main.Main;
import predictor.main.Utils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Event {

    private static List<Event> allEvents = new ArrayList<>();
    private static Object ALL_EVENTS_USE = new Object();

    public String key;
    public boolean init = false;
    public String name = null;
    public String shortName = null;
    public Integer year = null;
    public Boolean district = null;
    public Integer districtID = null;
    public Boolean official = null;
    public Date date = null;
    public Type type = null;
    private List<Team> teams = null;
    private List<Award> awards = null;

    protected Event(String s) {
        key = s.replace(" ", "");
    }

    protected Event(JSONObject o) {
        initInfo(o);
    }

    public void initInfo(JSONObject o) {
        try {
            key = o.getString("key");
            name = o.getString("name");
            while (name.charAt(name.length() - 1) == ' ') name = name.substring(0, name.length() - 1);
            if (!o.isNull("short_name")) shortName = o.getString("short_name");
            else shortName = name;
            year = o.getInt("year");
            district = !o.isNull("event_district") && o.getInt("event_district") != 0;
            districtID = district ? o.getInt("event_district") : 0;
            official = o.getBoolean("official");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                date = format.parse(o.getString("end_date"));
            } catch (Exception e) {
                date = Utils.makeSafe(date);
            }
            if (o.has("event_type_string") && !o.isNull("event_type_string")) {
                String typeString = o.getString("event_type_string");
                if (typeString.equalsIgnoreCase("regional")) type = Type.REGIONAL;
                else if (typeString.equalsIgnoreCase("district")) type = Type.DISTRICT;
                else if (typeString.equalsIgnoreCase("district championship")) type = Type.DISTRICT_CHAMPIONSHIP;
                else if (typeString.equalsIgnoreCase("championship division")) type = Type.CHAMPIONSHIP_DIVISION;
                else if (typeString.equalsIgnoreCase("championship finals")) type = Type.CHAMPIONSHIP_FINALS;
                else if (typeString.equalsIgnoreCase("offseason")) type = Type.OFFSEASON;
                else if (typeString.equalsIgnoreCase("preseason")) type = Type.PRESEASON;
                else System.out.println("Unexpected event type \"" + typeString + "\"!");
            } else {
                if (district) type = Type.DISTRICT;
                else type = Type.REGIONAL;
            }
            init = true;
            boolean duplicate = false;
            synchronized (ALL_EVENTS_USE) {
                for (Event e : allEvents) {
                    if (e.key.equalsIgnoreCase(key)) {
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) allEvents.add(this);
            }
        } catch (Exception e) {
            System.out.println("Event.initInfo() exception!");
            e.printStackTrace();
        }
        if (date == null) System.out.println("Event \"" + name + "\" has a null date!");
    }

    public void initInfo() {
        if (!init) {
            initInfo(Utils.getObject("event/" + key));
        }
    }

    /**
     * Gets the Team object for a Team that attended this Event.
     *
     * @param number The Team's number.
     * @return The Team object for a Team that attended this Event.
     */
    public Team getTeam(int number) {
        for (Team t : getTeams()) {
            if (t.number == number) return t;
        }
        return null;
    }

    /**
     * Gets all the Teams that attended this Event.
     *
     * @return All the Teams that attended this Event.
     */
    public List<Team> getTeams() {
        if (teams == null) {
            teams = new ArrayList<>();
            try {
                JSONArray jsonTeams = Utils.getArray("event/" + key + "/teams");
                for (JSONObject jT : Utils.getObjects(jsonTeams)) {
                    teams.add(new Team(jT));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(teams);
    }

    /**
     * Gets all the Awards given out at this Event.
     *
     * @return All the Awards given out at this Event.
     */
    public List<Award> getAwards() {
        if (awards == null) {
            awards = new ArrayList<>();
            JSONArray jsonAwards = Utils.getArray("event/" + key + "/awards");
            for (JSONObject jA : Utils.getObjects(jsonAwards)) {
                awards.add(new Award(jA));
            }
        }
        return new ArrayList<>(awards);
    }

    /**
     * Gets an Event.
     *
     * @param key The Event's TBA key.
     * @return An Event.
     */
    public static Event getEvent(String key) {
        synchronized (ALL_EVENTS_USE) {
            for (Event e : allEvents) {
                if (e.key.equalsIgnoreCase(key)) {
                    return e;
                }
            }
        }
        Event e = new Event(key);
        e.initInfo();
        return e;
    }

    /**
     * Gets all the Teams that attended the Championship of a given year.
     *
     * @param year The year.
     * @return All the Teams that attended the Championship of a given year.
     */
    public static List<Team> getTeamsAtChamps(int year) {
        List<Team> teams = new ArrayList<>();
        if (year <= 2015) {
            Utils.log("Reading teams from Championship divisions...");
            String[] divs;
            if (year <= 2014) divs = Main.originalDivisions;
            else divs = Main.allDivisions;
            for (String s : divs) {
                Event e = new Event(year + s);
                e.initInfo();
                Utils.log(e.name + "...");
                for (Team t : e.getTeams()) {
                    teams.add(t);
                }
            }
        } else {
            teams = new Event(year + "cmp").getTeams();
        }
        return teams;
    }

    public enum Type {
        REGIONAL,
        DISTRICT,
        DISTRICT_CHAMPIONSHIP,
        CHAMPIONSHIP_DIVISION,
        CHAMPIONSHIP_FINALS,

        OFFSEASON,
        PRESEASON
    }
}
