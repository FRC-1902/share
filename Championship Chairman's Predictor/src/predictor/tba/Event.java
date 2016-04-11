package predictor.tba;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.main.CPR;
import predictor.main.Main;
import predictor.main.Processing;
import predictor.main.Utils;
import predictor.stuff.EventCPRResult;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Event implements Serializable {

    protected static List<Event> allEvents = new ArrayList<>();
    protected static Object ALL_EVENTS_USE = new Object();

    public String key;
    public boolean init = false;
    public String name = null;
    public String shortName = null;
    public Integer year = null;
    public Boolean champs = null;
    public Boolean district = null;
    public Boolean regional = null;
    public Integer districtID = null;
    public Boolean official = null;
    public Date date = null;
    public Type type = null;
    protected List<Team> teams = null;
    protected List<Award> awards = null;

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
            if (o.has("event_type") && !o.isNull("event_type")) {
                int typeInt = o.getInt("event_type");
                if (typeInt == 0) type = Type.REGIONAL;
                else if (typeInt == 1) type = Type.DISTRICT;
                else if (typeInt == 2) type = Type.DISTRICT_CHAMPIONSHIP;
                else if (typeInt == 3) type = Type.CHAMPIONSHIP_DIVISION;
                else if (typeInt == 4) type = Type.CHAMPIONSHIP;
                else if (typeInt == 99) type = Type.OFFSEASON;
                else if (typeInt == 100) type = Type.PRESEASON;
                else {
                    type = Type.UNKNOWN;
                    if (typeInt != -1) Utils.log("Unknown event type \"" + typeInt + "\".");
                }
            } else {
                if (district) type = Type.DISTRICT;
                else type = Type.REGIONAL;
            }

            regional = type == Type.REGIONAL;
            champs = type == Type.CHAMPIONSHIP_DIVISION || type == Type.CHAMPIONSHIP;
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

    public EventCPRResult getCPRPredictions() {
        return getCPRPredictions(getTeams());
    }

    public EventCPRResult getCPRPredictions(List<Team> ts) {
        Utils.log("Starting calculation of simple CPR ratings...");

        Processing.processTeams(ts, (Team t) -> {
            CPR.calculateSimpleCPR(t, date);
        }, Main.threadsToUse, true);

        for (Team t : new ArrayList<>(ts)) {
            if (t.cpr == 0) ts.remove(t);
        }

        Utils.log("Starting calculation of complex CPR ratings...");

        Processing.processTeams(ts, (Team t) -> {
            CPR.calculateComplexCPR(t, date, false);
        }, Main.threadsToUse, true);

        for (Team t : new ArrayList<>(ts)) {
            if (t.isHOF(date) || !t.isEligibleForChairmans(this)) ts.remove(t);
        }

        Collections.sort(ts, CPR.cprComp);
        EventCPRResult result = new EventCPRResult(this, ts);
        Utils.serialize(result, "results/" + key + ".event");
        return result;
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
     * Gets the winner(s) of a certain Award at this Event.
     * .
     * @param award The award type.
     * @return The winner(s) of a certain Award.
     */
    public List<Team> getWinnersOf(int award) {
        List<Team> winners = new ArrayList<>();
        for (Award a : getAwards()) {
            if (a.type == award) {
                for (int i : a.winners) {
                    winners.add(getTeam(i));
                }
            }
        }
        return winners;
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

    public static Championship getChampionship(int year) {
        synchronized (ALL_EVENTS_USE) {
            for (Event e : allEvents) {
                if (e.key.equalsIgnoreCase(year + "cmp")) {
                    if (e instanceof Championship) {
                        return ((Championship) e);
                    }
                }
            }
        }
        Championship c = new Championship(year);
        return c;
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
     * Gets any other Events that are running at a similar time as "event".
     *
     * @param event The Event.
     * @return Any other Events that are running at a similar time as "event".
     */
    public static List<Event> getConcurrentEvents(Event event) {
        List<Event> es = new ArrayList<>();
        int month = Utils.getMonth(event.date);
        int day = Utils.getDay(event.date);
        for (Event e : getEventsFrom(Utils.getYear(event.date))) {
            int eMonth = Utils.getMonth(e.date);
            int eDay = Utils.getDay(e.date);
            if (month == eMonth) {
                if ((day + 4) >= eDay && (day - 2) <= eDay) { //TODO: tweak?
                    //Utils.log(eDay + " is a similar day.");
                    es.add(e);
                }
            }
        }
        return es;
    }

    private static List<Integer> gotEventsForYear = new ArrayList<>();

    /**
     * Gets all the Events from a given year.
     *
     * @param year The year.
     * @return All the events from the given year.
     */
    public static List<Event> getEventsFrom(int year) {
        List<Event> es = new ArrayList<>();
        if (gotEventsForYear.contains(year)) {
            for (Event e : getAllSavedEvents()) {
                if (e.year == year) es.add(e);
            }
        } else {
            JSONArray eventArray = Utils.getArray("events/" + year);
            for (JSONObject jE : Utils.getObjects(eventArray)) {
                es.add(new Event(jE));
            }
            gotEventsForYear.add(year);
        }
        return es;
    }

    /**
     * Gets all the Event objects that have been previously created.
     *
     * @return All the Event objects that have been previously created.
     */
    public static List<Event> getAllSavedEvents() {
        synchronized (ALL_EVENTS_USE) {
            return new ArrayList<>(allEvents);
        }
    }

    public enum Type {
        REGIONAL,
        DISTRICT,
        DISTRICT_CHAMPIONSHIP,
        CHAMPIONSHIP_DIVISION,
        CHAMPIONSHIP,

        OFFSEASON,
        PRESEASON,

        UNKNOWN
    }
}
