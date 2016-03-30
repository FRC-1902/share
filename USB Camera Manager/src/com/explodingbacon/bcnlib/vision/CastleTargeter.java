/*
package com.explodingbacon.bcnlib.vision;

import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.utils.Utils;
import java.util.Collections;

public class CastleTargeter implements Targeter {



    DO NOT USE THIS FILE UNTIL AFTER COMPETITION SEASON IS OVER

    THANK YOU




    TargetType type;

    public CastleTargeter(TargetType t) {
        type = t;
    }

    @Override
    public Image filter(Image i, Object... args) { //filter(image)
        return i.inRange(new Color(230, 230, 230), new Color(255, 255, 255));
    }

    @Override
    public boolean isValid(Contour c, Object... args) { //isValid(contour)
        return (c.getWidth() < 300 && c.getWidth() > 10 && c.getHeight() < 300 && c.getHeight() > 5);
    }

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
*/
