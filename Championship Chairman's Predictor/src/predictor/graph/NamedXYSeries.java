package predictor.graph;

import org.jfree.data.xy.XYSeries;

public class NamedXYSeries extends XYSeries {

    String name;

    public NamedXYSeries(String key) {
        super(key);
        name = key;
    }

    public String getName() {
        return name;
    }
}
