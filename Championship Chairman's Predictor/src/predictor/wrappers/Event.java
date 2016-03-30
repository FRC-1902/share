package predictor.wrappers;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.Main;
import java.util.ArrayList;
import java.util.List;

public class Event {

    public final String key;
    private List<Team> teams = null;

    public Event(String s) {
        key = s;
    }

    public List<Team> getTeams() {
        if (teams == null) {
            teams = new ArrayList<>();
            JSONArray jsonTeams = Main.getArray("event/" + key + "/teams");
            for (JSONObject jT : Main.getObjects(jsonTeams)) {
                teams.add(new Team(jT));
            }
        }
        return new ArrayList<>(teams);
    }
}
