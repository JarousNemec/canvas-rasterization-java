package clip;

import model.ClipWindow;
import model.Point2D;
import utils.GeometryUtil;
import utils.Vec2;

public class CohenSutherland {
    private CohenSutherland() {
    }

    private static final byte CENTER = 0b0000;
    private static final byte TOP = 0b0001;
    private static final byte BOTTOM = 0b0010;
    private static final byte RIGHT = 0b0100;
    private static final byte LEFT = 0b1000;

    public static Point2D[] clip(Point2D pointA, Point2D pointB, ClipWindow clipWindow) {
        //double points for calculations, rounding the intersections in between would move the line
        //and the next intersection would be calculated on a different line (error up to tens of pixels)
        Vec2 a = Vec2.of(pointA);
        Vec2 b = Vec2.of(pointB);
        byte aCode = getPointCode(a, clipWindow);
        byte bCode = getPointCode(b, clipWindow);

        // step by step, or direction by direction, clipping the point by the bits set in its code
        // For example: p has code 1001, so the cycle is there because first we get a new intersection for the LEFT, which is the xMin line
        // then in the next cycle we recalculate the code for p and get 0001, so we calculate the intersection again, but this time with the TOP, which is the yMin line
        // as a result we get the intersection of the line right at the border of the clip window
        while (true) {
            if ((aCode | bCode) == 0) {
                //round only at the very end
                return new Point2D[]{a.toPoint2D(), b.toPoint2D()};
            }
            if ((aCode & bCode) != 0) {
                return new Point2D[]{};
            }

            if (aCode != CENTER) {
                a = intersect(a, b, aCode, clipWindow);
                aCode = getPointCode(a, clipWindow);
            } else {
                b = intersect(b, a, bCode, clipWindow);
                bCode = getPointCode(b, clipWindow);
            }
        }
    }

    private static Vec2 intersect(Vec2 a, Vec2 b, byte code, ClipWindow clipWindow) {
        int xMax = clipWindow.getXMax();
        int yMax = clipWindow.getYMax();
        int xMin = clipWindow.getXMin();
        int yMin = clipWindow.getYMin();

        //lineIntersection works with infinite lines, so any two points on the edge line are enough
        // code & DIRECTION is a basic AND operation, so we get a number that isn't 0 when both codes have any common bits
        //the intersection lies exactly on the clip edge, so that coordinate is set directly,
        //otherwise double error (e.g. 242.9999999) could leave the point just outside the window
        if ((code & LEFT) != 0) {
            Vec2 p = GeometryUtil.lineIntersection(a, b, new Vec2(xMin, 0), new Vec2(xMin, 1));
            return new Vec2(xMin, p.y());
        }
        if ((code & RIGHT) != 0) {
            Vec2 p = GeometryUtil.lineIntersection(a, b, new Vec2(xMax, 0), new Vec2(xMax, 1));
            return new Vec2(xMax, p.y());
        }
        if ((code & TOP) != 0) {
            Vec2 p = GeometryUtil.lineIntersection(a, b, new Vec2(0, yMin), new Vec2(1, yMin));
            return new Vec2(p.x(), yMin);
        }
        Vec2 p = GeometryUtil.lineIntersection(a, b, new Vec2(0, yMax), new Vec2(1, yMax));
        return new Vec2(p.x(), yMax);
    }

    private static byte getPointCode(Vec2 p, ClipWindow clipWindow) {
        double x = p.x();
        double y = p.y();
        int xMax = clipWindow.getXMax();
        int yMax = clipWindow.getYMax();
        int xMin = clipWindow.getXMin();
        int yMin = clipWindow.getYMin();
        byte pointCode = CENTER;

        if (x < xMin) {
            pointCode |= LEFT;
        } else if (x > xMax) {
            pointCode |= RIGHT;
        }
        if (y < yMin) {
            pointCode |= TOP;
        } else if (y > yMax) {
            pointCode |= BOTTOM;
        }

        return pointCode;
    }
}
