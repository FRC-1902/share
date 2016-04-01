package predictor.wrappers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Event {

    //TODO: boolean for official event or not
    public String key;
    public boolean init = false;
    public String name = null;
    public Integer year = null;
    public Boolean district = null;
    public Integer districtID = null;
    public Boolean official = null;
    public Date date = null;
    public Type type = null;
    private List<Team> teams = null;

    public Event(String s) {
        key = s;
    }

    public Event(JSONObject o) {
        initInfo(o);
    }

    public void initInfo(JSONObject o) {
        try {
            if (!init) {
                key = o.getString("key");
                name = o.getString("name");
                year = o.getInt("year");
                district = !o.isNull("event_district") && o.getInt("event_district") != 0;
                districtID = district ? o.getInt("event_district") : 0;
                official = o.getBoolean("official");
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    date = format.parse(o.getString("end_date"));
                } catch (Exception e) {
                    date = format.parse("0001-01-01");
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
            }
        } catch (Exception e) {
            System.out.println("Event.initInfo() exception!");
            e.printStackTrace();
        }
    }

    public void initInfo() {
        initInfo(Utils.getObject("event/" + key));
    }

    public List<Team> getTeams() {
        if (teams == null) {
            teams = new ArrayList<>();
            JSONArray jsonTeams = Utils.getArray("event/" + key + "/teams");
            for (JSONObject jT : Utils.getObjects(jsonTeams)) {
                teams.add(new Team(jT));
            }
        }
        return new ArrayList<>(teams);
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
