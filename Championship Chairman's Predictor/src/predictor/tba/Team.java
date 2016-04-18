package predictor.tba;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.main.Utils;
import java.io.Serializable;
import java.util.*;

public class Team implements Serializable {

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

    public boolean isEligibleForChairmans(Event e) {
        final int year = Utils.getYear(e.date);
        if (isHOF(e.date)) return false;
        if (e.type == Event.Type.REGIONAL) { //Must not have won a RCA yet
            return !hasWonRCA(e.date);
        } else if (e.type == Event.Type.DISTRICT) { //Must not have won DCA or RCA
            return !hasWonDCA(e.districtID, e.date) && !hasWonRCA(e.date);
        } else if (e.type == Event.Type.DISTRICT_CHAMPIONSHIP) { //Must have won DCA and not RCA
            return hasWonDCA(e.districtID, e.date) && !hasWonRCA(e.date);
        } else if (e.type == Event.Type.CHAMPIONSHIP) { //Must have won RCA
            int rcaWins = 0;
            for (Award a : getAwardsBefore(e)) {
                if (a.type == Award.CHAIRMANS && a.regional) {
                    rcaWins++;
                }
            }
            return (rcaWins > 3 || year <= 2010) && hasWonRCA(e.date);
        } else {
            return false;
        }
    }

    /**
     * Checks if this Team had won the RCA during the year of the given Date, but before the Date.
     *
     * @param d The Date.
     * @return if this Team had won the RCA during the year of the given Date.
     */
    public boolean hasWonRCA(Date d) {
        int year = Utils.getYear(d);
        for (Award a : getAwardsBefore(d)) {
            if (a.year == year && a.type == Award.CHAIRMANS && a.regional) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this Team had won the DCA during the year of the given Date, but before the Date.
     *
     * @param districtID The ID of the District we're looking for DCA's in.
     * @param d The Date.
     * @return if this Team had won the DCA during the year of the given Date.
     */
    public boolean hasWonDCA(int districtID, Date d) {
        int year = Utils.getYear(d);
        for (Award a : getAwardsBefore(d)) {
            if (a.year == year && a.type == Award.CHAIRMANS && a.district && a.event.districtID == districtID) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this Team is in the Hall of Fame (AKA has won the Championship Chairman's Award).
     *
     * @param d The date from which this check is being made.
     * @return If this Team is in the Hall of Fame.
     */
    public boolean isHOF(Date d) {
        for (Award a : getAwardsBefore(d)) {
            if (a.type == Award.CHAIRMANS && a.champs) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this Team has ever won a certain award.
     *
     * @param awardID The award ID.
     * @return If this Team has ever won a certain award.
     */
    public boolean hasWonAward(int awardID) {
        for (Award a : getAllAwards()) {
            if (a.type == awardID) {
                return true;
            }
        }
        return false;
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
            if (a.event.date.before(before)) {
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
                if (eventKeys.contains(a.eventKey)) {
                    awards.add(new Award(jA));
                }
            }
            Collections.sort(awards, awardYear);
        }
        return new ArrayList<>(awards);
    }

    /**
     * Gets every Event this Team has attended before a certain Date.
     *
     * @param d The Date.
     * @return Every Event this Team has attended before the Date.
     */
    public List<Event> getEventsBefore(Date d) {
        List<Event> es = new ArrayList<>();
        for (Event e : getAllEvents()) {
            if (e.date.before(d)) {
                es.add(e);
            }
        }
        return es;
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof Team) {
            Team t = (Team) o;
            if (number == t.number) return true;
        }
        return false;
    }

    /**
     * Gets a Team. TODO: Do not use until this uses the same saving system as Event.getEvent()
     *
     * @param number The Team's number.
     * @return A Team.
     */
    public static Team getTeam(int number) {
        JSONObject team = Utils.getObject("team/frc" + number);
        return new Team(team);
    }

    private static final Comparator<Event> eventOrder = (e1, e2) -> {
        return e2.date.compareTo(e1.date);
    };

    private static final Comparator<Award> awardYear = (a1, a2) -> {
        return a2.year - a1.year;
    };
}
