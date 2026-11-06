package clip;

import model.Point2D;
import utils.GeometryUtil;

public class CohenSutherland {
    private CohenSutherland() {
    }

    private static final byte CENTER = 0b0000;
    private static final byte TOP = 0b0001;
    private static final byte BOTTOM = 0b0010;
    private static final byte RIGHT = 0b0100;
    private static final byte LEFT = 0b1000;

    public static Point2D[] clip(Point2D a, Point2D b, ClipWindow clipWindow) {
        byte aCode = getPointCode(a, clipWindow);
        byte bCode = getPointCode(b, clipWindow);

        // step by step, or direction by direction, clipping the point by the ones in its byte code
        // For example: p has code 1001, so the cycle is there because first we get a new intersection for the LEFT, which is the xMin line
        // then in the next cycle we recalculate the code for p and get 0001, so we calculate the intersection again, but this time with the TOP, which is the yMin line
        // as a result we get the intersection of the line right at the border of the clip window
        while (true) {
            if ((aCode | bCode) == 0) {
                return new Point2D[]{a, b};
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

    private static Point2D intersect(Point2D a, Point2D b, byte code, ClipWindow clipWindow) {
        int xMax = clipWindow.getXMax();
        int yMax = clipWindow.getYMax();
        int xMin = clipWindow.getXMin();
        int yMin = clipWindow.getYMin();

        //lineIntersection works with infinite lines, so any two points on the edge line are enough
        // code & DIRECTION is basic AND operation so we will get number that isnt 0 when there are any common bytes in both numbers / bytes
        if ((code & LEFT) != 0)
            return GeometryUtil.lineIntersection(a, b, new Point2D(xMin, 0), new Point2D(xMin, 1));
        if ((code & RIGHT) != 0)
            return GeometryUtil.lineIntersection(a, b, new Point2D(xMax, 0), new Point2D(xMax, 1));
        if ((code & TOP) != 0)
            return GeometryUtil.lineIntersection(a, b, new Point2D(0, yMin), new Point2D(1, yMin));
        return GeometryUtil.lineIntersection(a, b, new Point2D(0, yMax), new Point2D(1, yMax));

    }

    private static byte getPointCode(Point2D p, ClipWindow clipWindow) {
        int x = p.getX();
        int y = p.getY();
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
