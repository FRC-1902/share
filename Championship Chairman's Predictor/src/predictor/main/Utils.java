package predictor.main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {

    private static List<String> logs = new ArrayList<>();

    private static final String tbaAPI = "https://www.thebluealliance.com/api/v2/";
    private static final String tbaID = "?X-TBA-App-Id=frc1902:chairmans_predictions:v0.1";

    public static void log(String s) {
        logs.add(s);
        System.out.println(s);
    }

    /**
     * If d is null, a new Date will be returned. Otherwise, d will be returned.
     *
     * @param d The Date to check.
     * @return If d is null, a new Date will be returned. Otherwise, d will be returned.
     */
    public static Date makeSafe(Date d) {
        try {
            if (d == null) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                return format.parse("2016-12-31"); //TODO: change this to use current system year
            }
            return d;
        } catch (Exception e) {
            return d;
        }
    }

    public static Date makeDate(String date) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            return format.parse(date); //TODO: change this to use current system year
        } catch (Exception e) {
            log("Date.makeDate() exception!");
            e.printStackTrace();
            return null;
        }
    }

    public static int getYear(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.YEAR);
    }

    public static int getMonth(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.MONTH);
    }

    public static int getDay(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.DAY_OF_MONTH);
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

            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko");

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

    public static void makeSeparator() {
         log("------------------------");
    }
}
