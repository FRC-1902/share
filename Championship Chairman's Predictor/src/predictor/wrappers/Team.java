package predictor.wrappers;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.main.Utils;

import java.util.*;

public class Team {

    public final String name;
    public final String fullName;
    public final int number;
    public final int rookieYear;
    public double cpr = -1;
    private List<Award> awards = null;
    private List<Event> events = null;
    private List<String> eventKeys = null;

    //TODO: getAwardsInRange(start, finish)

    public Team(JSONObject o) {
        if (!o.isNull("nickname")) name = o.getString("nickname");
        else name = "null";
        if (!o.isNull("name")) fullName = o.getString("name");
        else fullName = "null";
        if (!o.isNull("team_number")) number = o.getInt("team_number");
        else number = -1;
        if (!o.isNull("rookie_year")) {
            Object yearObj = o.get("rookie_year");
            if (yearObj instanceof String) {
                rookieYear = Integer.parseInt(yearObj.toString().replace("B", ""));
            } else {
                rookieYear = Integer.parseInt(yearObj.toString());
            }
        } else {
            rookieYear = -1;
        }
    }

    /**
     * Gets all the awards this Team won in a certain year.
     *
     * @param year The year.
     * @return All the awards this Team won in a certain year.
     */
    public List<Award> getAwardsFrom(int year) {
        List<Award> aws = new ArrayList<>();
        for (Award a : getAllAwards()) {
            if (a.year == year) {
                aws.add(a);
            }
        }
        return aws;
    }

    /**
     * Gets all the awards this Team won before a certain event.
     *
     * @param key An event key (i.e. 2014flor or 2016cmp).
     * @return All the awards this Team won before a certain time.
     */
    public List<Award> getAwardsBefore(String key) {
        Event before = new Event(key);
        before.initInfo();
        return getAwardsBefore(before);
    }

    /**
     * Gets all the awards this Team won before a certain event.
     *
     * @param before The Event.
     * @return All the awards this Team won before the Event.
     */
    public List<Award> getAwardsBefore(Event before) {
        return getAwardsBefore(before.date);
    }

    /**
     * Gets all the awards this Team won before a certain date.
     *
     * @param before The Date.
     * @return All the awards this Team won before the Date.
     */
    public List<Award> getAwardsBefore(Date before) {
        List<Award> aws = new ArrayList<>();
        for (Award a : getAllAwards()) {
            Event awardingEvent = getEvent(a.event);
            if (awardingEvent.date.before(before)) {
                aws.add(a);
            }
        }
        return aws;
    }

    /**
     * Gets all awards this Team has won.
     *
     * @return All the awards this Team has won.
     */
    public List<Award> getAllAwards() {
        if (eventKeys == null) getAllEvents();
        if (awards == null) {
            awards = new ArrayList<>();
            JSONArray jsonAwards = Utils.getArray("team/frc" + number + "/history/awards");
            for (JSONObject jA : Utils.getObjects(jsonAwards)) {
                Award a = new Award(jA);
                if (eventKeys.contains(a.event)) {
                    awards.add(new Award(jA));
                }
            }
            Collections.sort(awards, awardYear);
        }
        return new ArrayList<>(awards);
    }

    /**
     * Gets an existing Event object.
     *
     * @param key The event key.
     * @return An existing Event object.
     */
    public Event getEvent(String key) {
        for (Event e : getAllEvents()) {
            if (e.key.equalsIgnoreCase(key)) {
                return e;
            }
        }
        return null;
    }
    /**
     * Gets every Event this Team has attended.
     *
     * @return Every Event this Team has attended.
     */
    public List<Event> getAllEvents() {
        if (events == null) {
            events = new ArrayList<>();
            eventKeys = new ArrayList<>();
            JSONArray jsonEvents = Utils.getArray("team/frc" + number + "/history/events");
            for (JSONObject o : Utils.getObjects(jsonEvents)) {
                Event e = new Event(o);
                if (e.official) {
                    events.add(e);
                    eventKeys.add(e.key);
                }
            }
            Collections.sort(events, eventOrder);
        }
        return new ArrayList<>(events);
    }

    private static final Comparator<Event> eventOrder = (e1, e2) -> {
        return e2.date.compareTo(e1.date);
    };

    private static final Comparator<Award> awardYear = (a1, a2) -> {
        return a2.year - a1.year;
    };
}
