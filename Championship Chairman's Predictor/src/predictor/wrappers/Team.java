package predictor.wrappers;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.Main;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Team {

    public final String name;
    public final String fullName;
    public final int number;
    public final int rookieYear;
    public double cpr = -1;
    private List<Award> awards = null;
    private List<Event> events = null;

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
        List<Award> aws = new ArrayList<>();
        int year = Integer.parseInt(key.substring(0, 4));

        List<String> validKeys = new ArrayList<>();
        boolean before = false;
        //System.out.println("Getting all awards before " + key + " for team " + number);
        for (Event e : getOfficialEvents()) {
            if (before) {
                //System.out.println("Awards from " + e.name + " " + e.year + " will be counted for team " + number);
                validKeys.add(e.key);
            } else {
                //System.out.println("Awards from " + e.name + " " + e.year + " will NOT be counted for team " + number);
                if (key.contains("cmp")) { //If this is champs
                    for (String s : Main.allDivisions) {
                        if (e.key.equalsIgnoreCase(year + s)) {
                            before = true;
                            break;
                        }
                    }
                    if (!before && e.key.contains("cmp")) {
                        before = true;
                    }
                } else {
                    if (e.key.equals(key)) before = true;
                }
            }
        }
        for (Award a : getAllAwards()) {
            if (validKeys.contains(a.event)) {
                aws.add(a);
            }
        }
        //System.out.println("end");
        return aws;
    }

    public List<Award> getAllAwards() {
        if (awards == null) {
            awards = new ArrayList<>();
            JSONArray jsonAwards = Utils.getArray("team/frc" + number + "/history/awards");
            for (JSONObject jA : Utils.getObjects(jsonAwards)) {
                awards.add(new Award(jA));
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
        for (Event e : events) {
            if (e.key.equalsIgnoreCase(key)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Gets every official FIRST Event this Team has attended.
     *
     * @return Every official FIRST Event this Team has attended.
     */
    public List<Event> getOfficialEvents() {
        List<Event> off = getAllEvents().stream().filter(e -> e.official).collect(Collectors.toList());
        return off;
    }

    /**
     * Gets every Event this Team has attended.
     *
     * @return Every Event this Team has attended.
     */
    public List<Event> getAllEvents() {
        if (events == null) {
            events = new ArrayList<>();
            JSONArray jsonEvents = Utils.getArray("team/frc" + number + "/history/events");
            for (JSONObject o : Utils.getObjects(jsonEvents)) {
                events.add(new Event(o));
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
