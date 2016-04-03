package predictor.main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {

    private static final String tbaAPI = "https://www.thebluealliance.com/api/v2/";
    private static final String tbaID = "?X-TBA-App-Id=frc1902:chairmans_predictions:v0.1";

    public static void log(String s) {
        System.out.println(s);
    }

    public static int getYear(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.YEAR);
    }

    public static double roundToPlace(double d, double place) {
        double amount = 1;
        for (int i=0;i<place;i++) {
            amount *= 10;
        }
        return (double)Math.round(d * amount) / amount;
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
        String html = getHTML(tbaAPI + s + tbaID);
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

            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");

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
