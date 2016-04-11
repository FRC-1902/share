package predictor.tba;

import org.json.JSONArray;
import org.json.JSONObject;
import predictor.main.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Award implements Serializable {

    public final String name;
    public final int type;
    public final boolean official;
    public final boolean regional;
    public final boolean district;
    public final boolean champs;
    public final String eventKey;
    public Event event = null;
    public final int year;
    public final boolean oneWinner;
    public final int winner;
    public final List<Integer> winners = new ArrayList<>();

    public Award(JSONObject o) {
        name = o.getString("name");
        type = o.getInt("award_type");
        eventKey = o.getString("event_key");
        event = Event.getEvent(eventKey);
        official = event.official;
        regional = event.type == Event.Type.REGIONAL || event.type == Event.Type.DISTRICT_CHAMPIONSHIP;
        district = event.type == Event.Type.DISTRICT;
        champs = event.type == Event.Type.CHAMPIONSHIP_DIVISION || event.type == Event.Type.CHAMPIONSHIP;
        year = Integer.parseInt(eventKey.substring(0, 4));
        JSONArray jsonWinners = o.getJSONArray("recipient_list");
        for (JSONObject jW : Utils.getObjects(jsonWinners)) {
            int winnerNumber = -1;
            if (!jW.isNull("team_number")) {
                Object numObj = jW.get("team_number");
                try {
                    if (numObj instanceof String) {
                        winnerNumber = Integer.parseInt(numObj.toString().replace("B", ""));
                    } else {
                        winnerNumber = Integer.parseInt(numObj.toString());
                    }
                    //winnerNumber = jW.getInt("team_number");
                } catch (Exception e) {
                    System.out.println("Team number error.");
                    System.out.println("Object: " + numObj + ", " + numObj.getClass());
                    e.printStackTrace();
                }
            }
            winners.add(winnerNumber);
        }
        if (winners.size() == 1) {
            oneWinner = true;
            winner = winners.get(0);
        } else {
            oneWinner = false;
            winner = -1;
        }
    }

    public static final int CHAIRMANS = 0;
    public static final int ENGINEERING_INSPIRATION = 9;
}
