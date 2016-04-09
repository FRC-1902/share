package predictor.graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import predictor.main.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LineGraph extends Graph {

    private XYSeriesCollection dataset = new XYSeriesCollection();
    private List<NamedXYSeries> series = new ArrayList<>();
    private CSV csv = new CSV();

    public LineGraph(String n, String x, String y) {
        super(n, x, y);
    }

    public void addData(String category, double x, double y) {
        NamedXYSeries correct = null;
        for (NamedXYSeries s : series) {
            if (s.getName().equals(category)) {
                correct = s;
                break;
            }
        }
        if (correct == null) {
            correct = new NamedXYSeries(category);
            series.add(correct);
        }
        correct.add(x, y);
        csv.addData("Year", x);
        csv.addData(category, y);
    }

    @Override
    public void saveAs(String s) {
        try {
            for (NamedXYSeries xy : series) {
                dataset.addSeries(xy);
            }
            final JFreeChart chart = ChartFactory.createXYLineChart(name, xName, yName, dataset, PlotOrientation.VERTICAL, true, true, false);
            final XYPlot plot1 = chart.getXYPlot();
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot1.getRenderer();
            renderer.setBaseShapesVisible(true);

            File file = new File(s);
            ChartUtilities.saveChartAsJPEG(file, chart, getWidth(), getHeight());
        } catch (Exception e) {
            Utils.log("LineGraph.saveAs() exception!");
            e.printStackTrace();
        }
    }

    @Override
    public void saveAsCSV(String s) {
        csv.saveAs(s);
    }
}
