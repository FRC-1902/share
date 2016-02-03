package main;

import com.explodingbacon.bcnlib.vision.Camera;
import com.explodingbacon.bcnlib.vision.Contour;
import com.explodingbacon.bcnlib.vision.Image;
import org.opencv.core.*;
import java.awt.*;
import java.util.List;

public class Main {

    Camera c;

    public Main() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        try {
            /*
            c = new Camera(0);
            System.out.println("Taking picture in three seconds, say \"cheese\"!");
            for (int i = 3; i > 0; i--) {
                System.out.println(i + "");
                Thread.sleep(1000);
            }
            c.getImage(); //Clears out the old crusty image we don't want
            Image i = c.getImage();
            */

            Image i = Image.fromFile("image.png");

            Image filtered = i.colorRange(new Color(109, 39, 27), new Color(255, 160, 66));

            List<Contour> cons = filtered.getContours();

            int coCount = 1;
            for (Contour c : cons) {
                System.out.println("Contour " + coCount + " | X = " + c.getX() + " | Y = " + c.getY());
                coCount++;
            }

            i.drawContours(cons, new Color(0, 255, 0));

            /*
            Mat epsilon = Imgproc.arcLength(c.getContours().get(0), true);

            epsilon = 0.1*cv2.arcLength(cnt,True)
            2 approx = cv2.approxPolyDP(cnt,epsilon,True)*/

            //TODO: Figure out how to get a rectangle around our target and get the width/height of it

            //i.saveAs("image.png");
            filtered.saveAs("filtered.png");
            //System.out.println("Picture taken!");
            System.exit(0);
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        new Main();
    }

}
