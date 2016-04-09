package predictor.graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import predictor.main.Utils;
import java.io.File;

public class BarGraph extends Graph {

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    public BarGraph(String n, String x, String y) {
        super(n, x, y);
    }

    public void addData(String category, String x, double y) {
        dataset.addValue( y , category , x);
    }

    @Override
    public void saveAs(String s) {
        try {
            JFreeChart chart = ChartFactory.createBarChart(name, xName, yName, dataset, PlotOrientation.VERTICAL, true, true, false);

            File file = new File(s);
            ChartUtilities.saveChartAsJPEG(file, chart, getWidth(), getHeight());
        } catch (Exception e) {
            Utils.log("BarGraph.saveAs() exception!");
            e.printStackTrace();
        }
    }

    @Override
    public void saveAsCSV(String s) {

    }
}


