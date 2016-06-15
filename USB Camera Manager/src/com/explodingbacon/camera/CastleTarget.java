package com.explodingbacon.camera;

import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.bcnlib.vision.Color;
import com.explodingbacon.bcnlib.vision.Contour;
import com.explodingbacon.bcnlib.vision.Image;
import java.util.Collections;

public class CastleTarget {

    private static double MINIMUM_SIMILARITY = 2;
    private static Image goalSample = null;

    private static final TargetType TARGET_TYPE = TargetType.SHAPE;

    public static void drawIndicators(Image i, double targetPos, Contour goal) {
        if (goal != null) {
            i.drawContours(Collections.singletonList(goal), Color.RED); //Red outline of the goal
            i.drawRectangle(goal.getBoundingBox(), Color.TEAL); //Blue rectangle of goal bounding box
            i.drawLine(Utils.round(goal.getMiddleX()), Color.GREEN); //Green line of middle of goal
        }
        i.drawLine(Utils.round(targetPos), Color.BLUE); //Blue line of target position
    }

    private static final Color min = new Color(28, 220, 187), max = new Color(255, 255, 255); //max = new Color(206, 255, 240)

    public static Image filter(Image i) {
        return i.inRange(min, max);
    }

    public static Contour findGoal(Image i) {
        if (goalSample == null) {
            goalSample = Image.fromFile("goal_sample.png").inRange(new Color(244, 244, 244), new Color(255, 255, 255));
        }
        Image filtered = filter(i);
        Contour goal = null;
        for (Contour c : filtered.getContours()) {
            if (c.getWidth() < 300 && c.getWidth() > 20 && c.getHeight() < 300 && c.getHeight() > 20) { //TODO: These 20's used to be 10's. If things are bad, go back to 10's
                if (goal == null) {
                    boolean good;
                    if (TARGET_TYPE == TargetType.SHAPE) {
                        good = c.compareTo(goalSample) > MINIMUM_SIMILARITY;
                    } else {
                        good = true;
                    }
                    if (good) goal = c;
                } else {
                    if (TARGET_TYPE == TargetType.BIGGEST) {
                        if (c.getArea() > goal.getArea()) {
                            goal = c;
                        }
                    } else if (TARGET_TYPE == TargetType.SHAPE) {
                        double comp = c.compareTo(goalSample);
                        if (comp > MINIMUM_SIMILARITY  && comp > goal.compareTo(goalSample)) {
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
            if (TARGET_TYPE == TargetType.SHAPE) {
                //Log.d("Final goal shape rating: " + goal.compareTo(goalSample));
            }
        }

        return goal;
    }

    public enum TargetType {
        BIGGEST,
        SHAPE
    }
}
