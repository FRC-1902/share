package predictor.main;

import java.util.ArrayList;
import java.util.List;

public class Message {

    List<String> parts = new ArrayList<>();

    public void addSeparator() {
        add("------------------------");
    }

    public void add(String s) {
        parts.add(s);
    }

    public String getMessage() {
        String message = "";
        for (String s : parts) {
            message = message + s + "\n";
        }
        return message;
    }
}
