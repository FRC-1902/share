package main;

import com.explodingbacon.bcnlib.vision.Camera;
import com.explodingbacon.bcnlib.vision.Contour;
import com.explodingbacon.bcnlib.vision.Image;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

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
                for (int j = 3; j > 0; j--) {
                    System.out.println(j + "");
                    Thread.sleep(1000);
                }
                c.getImage(); //Clears out the old crusty image we don't want
                i = c.getImage();
            } else {
                i = Image.fromFile("workingimage.png");
            }

            Image filtered = i.colorRange(new Color(230, 230, 230), new Color(255, 255, 255));

            List<Contour> cons = filtered.getContours();
            List<Contour> approxcons = new ArrayList<>();

            int coCount = 1;
            for (Contour c : cons) {
                //System.out.println("Contour " + coCount + " | X = " + c.getX() + " | Y = " + c.getY());
                coCount++;
                approxcons.add(c.approxEdges(0.01));
                Rect r = Imgproc.boundingRect(c.getMatOfPoint());
                r.br();

            }

            if (!camera) i.drawContours(approxcons, new Color(0, 255, 0));

            if (!camera) {
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

}
