package main;

import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.bcnlib.vision.Camera;
import com.explodingbacon.bcnlib.vision.Contour;
import com.explodingbacon.bcnlib.vision.Image;
import org.opencv.core.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    Camera c;
    boolean camera = false;

    public Main() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        try {
            Image i;
            if (camera) {
                c = new Camera(0);
                System.out.println("Taking picture in three seconds, say \"cheese\"!");
                for (int j = 2; j > 0; j--) {
                    System.out.println(j + "");
                    Thread.sleep(1000);
                }
                c.getImage(); //Clears out the old crusty image we don't want
                i = c.getImage();
            } else {
                i = Image.fromFile("workingimage.png");
            }

            Image filtered = i.colorRange(new Color(200, 200, 200), new Color(255, 255, 255));

            List<Contour> cons = filtered.getContours();
            List<Contour> relevant = new ArrayList<>();

            for (Contour c : cons) {
                Contour n = c.approxEdges(0.01);
                if (n.getWidth() > 35) {
                    relevant.add(n);
                }
            }

            /*
            double midX = i.getWidth() / 2;
            Contour closest = null;
            for (Contour c : relevant) {
                if (closest == null) {
                    closest = c;
                } else {
                    double diff = Math.abs(c.getMiddleX() - midX);
                    double cdiff = Math.abs(closest.getMiddleX() - midX);
                    if (diff < cdiff) {
                        closest = c;
                    }
                }
            }*/


            /*
            Rectangle2D.Double rect = closest.getBoundingBox();
            int width = Utils.round(rect.getWidth());
            System.out.println("Distance: " + getDistanceFromPx(width) + ", width: " + width);
            //System.out.println("Distance: " + getDistanceFromPx(c.getWidth()) + ", size: " + c.getWidth() + ", area: " + c.getArea() + ", x: " + c.getX() + ", y: " + c.getY());
*/
            //System.out.println(i.getWidth());

            if (!camera) {
                i.drawContours(relevant, new Color(255, 0, 0));
                //i.saveAs("workingimage.png");
                i.saveAs("image.png");
                filtered.saveAs("filtered.png");
                System.out.println("Complete!");
            } else {
                i.saveAs("workingimage.png");
                System.out.println("Picture taken!");
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Main();
    }

    public double getDistanceFromPx(int sizeInPx) {
        return 10.25 / Math.tan(Math.toRadians((62.5 / 1280) * sizeInPx));
    }

}
