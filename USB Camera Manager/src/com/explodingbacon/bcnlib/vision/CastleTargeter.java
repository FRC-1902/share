package com.explodingbacon.bcnlib.vision;

import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.utils.Utils;
import java.util.Collections;

/**
 * An implementation of Targeter designed for tracking the retro-reflective tape on the 2016 Stronghold castles.
 *
 * @author Ryan Shavell
 * @version 2016.3.19
 */

public class CastleTargeter implements Targeter {

    TargetType type;

    public CastleTargeter(TargetType t) {
        type = t;
    }

    /**
     * Filters all non-bright white objects from the Image.
     * @param i The Image.
     * @param args Not used here.
     * @return A filtered version of the Image.
     */
    @Override
    public Image filter(Image i, Object... args) { //filter(image)
        return i.colorRange(new Color(230, 230, 230), new Color(255, 255, 255));
    }

    /**
     * Checks to see if the Contour is not too big or small to be the retro-reflective tape on the castle.
     *
     * @param c The Contour to be checked.
     * @param args Not used here.
     * @return If the Contour is valid.
     */
    @Override
    public boolean isValid(Contour c, Object... args) { //isValid(contour)
        return (c.getWidth() < 300 && c.getWidth() > 10 && c.getHeight() < 300 && c.getHeight() > 5);
    }

    /**
     * Finds the retro-reflective tape on the castle and returns it as a Contour.
     *
     * @param i The Image.
     * @param args Pass in a double for the target position on-screen where you want the tape to be moved to.
     * @return The retro-reflective tape on the castle and returns it as a Contour.
     */
    @Override
    public Contour findTarget(Image i, Object... args) { //findTarget(image, target)
        double target = Double.parseDouble(args[0].toString());

        Image filtered = filter(i);
        Contour goal = null;
        for (Contour c : filtered.getContours()) {
            if (isValid(c)) {
                if (goal == null) {
                    goal = c;
                } else {
                    if (type == TargetType.CLOSEST_TO_TARGET) {
                        double cTargetError = Math.abs(c.getMiddleX() - target);
                        double goalTargetError = Math.abs(goal.getMiddleX() - target);
                        if (cTargetError < goalTargetError) {
                            goal = c;
                        }

                    } else if (type == TargetType.CLOSEST_TO_BOTTOM) {
                        double height = i.getHeight();
                        double cTargetError = Math.abs(c.getMiddleY() - height);
                        double goalTargetError = Math.abs(goal.getMiddleY() - height);
                        if (cTargetError < goalTargetError) {
                            goal = c;
                        }
                    } else if (type == TargetType.BIGGEST) {
                        if (c.getArea() > goal.getArea()) {
                            goal = c;
                        }
                    } else {
                        Log.e("Unsupported TargetType \"" + type + "\" selected in VisionTargeting!");
                    }
                }
            }
        }

        goal = goal != null ? goal.approxEdges(0.01) : null;

        if (goal != null) {
            //Log.d("Goal's width is " + goal.getWidth() + ", height is " + goal.getHeight());
        }

        return goal;
    }

    /**
     * Adds visual indicators to the Image to help visualize the robot's process of tracking the castle tape.
     *
     * @param i The Image.
     * @param args Pass in a Contour object for the goal (or null) and a double for the target position on-screen where you want the tape to be moved to.
     */
    @Override
    public void addIndicators(Image i, Object... args) { //addIndicators(image, goal, targetPos)
        Contour goal = args[0] == null ? null : (Contour) args[0];
        double targetPos = Double.parseDouble(args[1].toString());
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
        CLOSEST_TO_BOTTOM
    }
}
