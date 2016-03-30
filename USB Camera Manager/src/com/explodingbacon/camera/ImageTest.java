package com.explodingbacon.camera;

import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.bcnlib.vision.Color;
import com.explodingbacon.bcnlib.vision.Contour;
import com.explodingbacon.bcnlib.vision.Image;
import com.explodingbacon.bcnlib.vision.Vision;

import java.util.Collections;

public class ImageTest {

    private static final TargetType TARGET_TYPE = TargetType.SHAPE;
    private static Image goalSample = null;

    public static void main(String[] args) {
        Vision.init();
        goalSample = Image.fromFile("output/goal_sample.png").inRange(new Color(244, 244, 244), new Color(255, 255, 255));
        Image i = Image.fromFile("output/sample.png");
        filter(i).saveAs("output/filtered.png");
        Contour c = findGoal(i, 320);
        Image render = i.copy();
        drawIndicators(render, 320, c);
        render.saveAs("output/targeting.png");
        System.out.println("Done!");
        System.exit(0);
    }

    private static final Color min = new Color(28, 220, 187), max = new Color(255, 255, 255); //max = new Color(206, 255, 240)

    private static Image filter(Image i) {
        return i.inRange(min, max);
    }

    private static Contour findGoal(Image i, double target) {
        Image filtered = filter(i);
        Contour goal = null;
        for (Contour c : filtered.getContours()) {
            if (c.getWidth() < 300 && c.getWidth() > 20 && c.getHeight() < 300 && c.getHeight() > 20) { //TODO: These 20's used to be 10's. If things are bad, go back to 10's
                if (goal == null) {
                    goal = c;
                } else {
                    if (TARGET_TYPE == TargetType.CLOSEST_TO_TARGET) {
                        double cTargetError = Math.abs(c.getMiddleX() - target);
                        double goalTargetError = Math.abs(goal.getMiddleX() - target);
                        if (cTargetError < goalTargetError) {
                            goal = c;
                        }
                    } else if (TARGET_TYPE == TargetType.CLOSEST_TO_BOTTOM) {
                        double height = i.getHeight();
                        double cTargetError = Math.abs(c.getMiddleY() - height);
                        double goalTargetError = Math.abs(goal.getMiddleY() - height);
                        if (cTargetError < goalTargetError) {
                            goal = c;
                        }
                    } else if (TARGET_TYPE == TargetType.BIGGEST) {
                        if (c.getArea() > goal.getArea()) {
                            goal = c;
                        }
                    } else if (TARGET_TYPE == TargetType.SHAPE) {
                        double v;
                        if ((v = c.compareTo(goalSample)) > goal.compareTo(goalSample)) {
                            System.out.println("Current goal's comparison value is " + v);
                            goal = c;
                        }
                    } else {
                        Log.e("Unsupported TargetType \"" + TARGET_TYPE + "\" selected in VisionTargeting!");
                    }
                }
            }
        }

        goal = goal != null ? goal.approxEdges(0.01) : null;

        if (goal != null) {
            Log.d("Goal's width is " + goal.getWidth() + ", height is " + goal.getHeight());
        }

        return goal;
    }

    private static void drawIndicators(Image i, double targetPos, Contour goal) {
        if (goal != null) {
            i.drawContours(Collections.singletonList(goal), Color.RED); //Red outline of the goal
            i.drawRectangle(goal.getBoundingBox(), Color.TEAL); //Blue rectangle of goal bounding box
            i.drawLine(Utils.round(goal.getMiddleX()), Color.GREEN); //Green line of middle of goal
        }
        i.drawLine(Utils.round(targetPos), Color.BLUE); //Blue line of target position
    }

    public enum TargetType {
        BIGGEST,
        CLOSEST_TO_TARGET,
        CLOSEST_TO_BOTTOM,
        SHAPE
    }
}
