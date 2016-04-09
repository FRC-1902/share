package predictor.graph;

import predictor.main.Utils;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CSV {

    private HashMap<String, List<Double>> allData = new HashMap<>();

    public void addData(String category, double... val) {
        List<Double> data = allData.get(category);
        if (data == null) data = new ArrayList<>();
        for (double d : val) {
            data.add(d);
        }
        allData.put(category, data);
    }

    public void saveAs(String dir) {
        try {
            File f = new File(dir);
            f.mkdirs();
            if (f.exists()) f.delete();
            FileWriter w = new FileWriter(f);
            w.write(csvList(allData.keySet()) + "\n");
            //Utils.log("Wrote key set");
            int index = 0;
            boolean dataLeft = true;

            while (dataLeft) {
                List<String> parts = new ArrayList<>();
                for (String s : allData.keySet()) {
                    List<Double> data = allData.get(s);
                    if (index < data.size()) {
                        parts.add(data.get(index) + "");
                    } else {
                        parts.add("0.0");
                    }
                }
                w.write(csvList(parts) + "\n");

                index++;
                dataLeft = false;
                for (String s : allData.keySet()) {
                    if (index < allData.get(s).size()) {
                        dataLeft = true;
                        break;
                    }
                }
            }

            w.flush();
            w.close();
            Utils.log("CSV file \"" + dir + "\" saved!");
        } catch (Exception e) {
            Utils.log("CSV.save() exception!");
            e.printStackTrace();
        }
    }

    private String csvList(Collection<String> list) {
        String string = "";
        int index = 0;
        for (String s : list) {
            string = string + (index != 0 ? ", " : "") + s;
            index++;
        }
        return string;
    }
}
