package predictor.graph;

public abstract class Graph {

    protected String name, xName, yName;
    protected int width = 640, height = 480;

    protected Graph(String n, String x, String y) {
        name = n;
        xName = x;
        yName = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int w, int h) {
        width = w;
        height = h;
    }

    public abstract void saveAs(String s);

    public abstract void saveAsCSV(String s);
}


