package predictor.wrappers;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.Main;
import java.util.ArrayList;
import java.util.List;

public class Team {

    public final String name;
    public final String fullName;
    public final int number;
    public final int rookieYear;
    private List<Award> awards = null;

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

    public List<Award> getAllAwards() {
        if (awards == null) {
            awards = new ArrayList<>();
            JSONArray jsonAwards = Main.getArray("team/frc" + number + "/history/awards");
            for (JSONObject jA : Main.getObjects(jsonAwards)) {
                awards.add(new Award(jA));
            }
        }
        return new ArrayList<>(awards);
    }
}
