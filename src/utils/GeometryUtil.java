package utils;

import model.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeometryUtil {
    private GeometryUtil() {
    }

    public static double distanceSquaredToSegment(int px, int py, int ax, int ay, int bx, int by) {
        //parameter to line equation
        double t = projectOntoSegment(px, py, ax, ay, bx, by);

        //calculate the point on the segment that is closest to the observed point
        //parametrized line equation
        double closestX = lerp(ax, bx, t);
        double closestY = lerp(ay, by, t);

        //calculate the length of the vector from the closest point to the observed point
        return distanceSquared(closestX, closestY, px, py);
    }

    public static Point2D closestPointOnSegment(int px, int py, int ax, int ay, int bx, int by) {
        //parameter to line equation
        double t = projectOntoSegment(px, py, ax, ay, bx, by);

        //calculate the point on the segment that is closest to the observed point
        //parametrized line equation
        double closestX = lerp(ax, bx, t);
        double closestY = lerp(ay, by, t);

        return new Point2D((int) Math.round(closestX), (int) Math.round(closestY));
    }

    private static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    private static double projectOntoSegment(int px, int py, int ax, int ay, int bx, int by) {
        //vector of the segment
        double vx = bx - ax;
        double vy = by - ay;

        //vector from A to Point
        double ux = px - ax;
        double uy = py - ay;

        //squared length of the segment
        double vLengthSq = lengthSquared(vx, vy);

        //squared length of the segment must not be zero to avoid division by zero
        if (vLengthSq <= 0) return 0;

        //dot product of the segment vector and the observed point vector
        double dotProduct = dot(vx, vy, ux, uy);

        //scalar product divided by the squared length gives us the position of the closest point as a portion of the segment
        double t = dotProduct / vLengthSq;

        //handle edge cases where the observed point lies before the start or past the end of the segment, so the closest point is one of the segment's endpoints
        if (t < 0) t = 0;
        else if (t > 1) t = 1;

        return t;
    }

    public static double distanceSquared(double x1, double y1, double x2, double y2) {
        //calculate squared distance between two point
        double diffX = x2 - x1;
        double diffY = y2 - y1;
        return diffX * diffX + diffY * diffY;
    }

    public static double dot(double vx, double vy, double ux, double uy) {
        return vx * ux + vy * uy;
    }

    public static long dot(long vx, long vy, long ux, long uy) {
        return vx * ux + vy * uy;
    }

    //2D CROSS PRODUCT - z-component of the 3D cross product (vx, vy, 0) x (ux, uy, 0)
    //both vectors lie in the XY plane, so their normal points along the Z axis and x, y components are always 0,
    //that is why only one number is left
    //sign      : direction of the turn from v to u (on screen with Y axis down: > 0 = right, < 0 = left)
    //magnitude : |v| * |u| * sin(angle) = area of the parallelogram spanned by v and u (triangle = half of it)
    //not to be confused with the 2D normal (-vy, vx), which is perpendicular to v and lies in the plane
    public static double cross(double vx, double vy, double ux, double uy) {
        return vx * uy - vy * ux;
    }

    //same as above, exact integer version
    public static long cross(long vx, long vy, long ux, long uy) {
        return vx * uy - vy * ux;
    }

    public static double lengthSquared(double x, double y) {
        return x * x + y * y;
    }

    public static double length(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    //tan(22.5°) - boundary between axis and diagonal direction (halfway between 0° and 45°)
    //in the condition it stands for steps on the other axis at the current position on the base axis
    private static final double SNAP_THRESHOLD = Math.tan(Math.toRadians(22.5));

    public static Point2D snapToAxisOrDiagonal(int startX, int startY, int endX, int endY) {
        int dx = endX - startX;
        int dy = endY - startY;
        int adx = Math.abs(dx);
        int ady = Math.abs(dy);

        //attach to Y axis
        if (ady <= adx * SNAP_THRESHOLD)
            return new Point2D(endX, startY);

        //attach to X axis
        if (adx <= ady * SNAP_THRESHOLD)
            return new Point2D(startX, endY);

        //attach to diagonal
        int longerCoordinate = Math.max(adx, ady);
        return new Point2D(startX + Integer.signum(dx) * longerCoordinate, startY + Integer.signum(dy) * longerCoordinate);
    }

    //intersection between two lines AB and CD, returns point of the intersection when there is any and null when the lines are parallel or lays on each other or the length of one line is zero because points are coincident
    //t is on AB, so AB = clipped edge, CD = clip edge
    public static Vec2 lineIntersection(Vec2 a, Vec2 b, Vec2 c, Vec2 d) {
        double numerator = (c.x() - a.x()) * (d.y() - c.y()) + (a.y() - c.y()) * (d.x() - c.x());
        double denominator = (b.x() - a.x()) * (d.y() - c.y()) + (a.y() - b.y()) * (d.x() - c.x());
        if (denominator == 0)
            return null;
        double t = numerator / denominator;
        return new Vec2(lerp(a.x(), b.x(), t), lerp(a.y(), b.y(), t));
    }

    //rounded version
    public static Point2D lineIntersection(Point2D a, Point2D b, Point2D c, Point2D d) {
        Vec2 intersection = lineIntersection(Vec2.of(a), Vec2.of(b), Vec2.of(c), Vec2.of(d));
        return intersection == null ? null : intersection.toPoint2D();
    }

    //sign of the side test: t = P - A, n = normal of t = (-ty, tx), result = dot(v, n)
    //DOT > 0 : P is on the LEFT of A->B as seen on screen (Y axis points down)
    //DOT < 0 : P is on the RIGHT of A->B as seen on screen
    //DOT = 0 : P lies on the line AB
    public static long pointSide(Point2D a, Point2D b, Point2D p) {
        //v = B - A
        long vx = b.getX() - a.getX();
        long vy = b.getY() - a.getY();

        //t = P - A
        long tx = p.getX() - a.getX();
        long ty = p.getY() - a.getY();

        //normal of vector t -> n = (-ty, tx)
        long nx = -ty;
        long ny = tx;

        //dot(n, v)
        return dot(vx, vy, nx, ny);
    }

    public static double pointSide(Vec2 a, Vec2 b, Vec2 p) {
        double vx = b.x() - a.x();
        double vy = b.y() - a.y();
        double tx = p.x() - a.x();
        double ty = p.y() - a.y();
        return dot(vx, vy, -ty, tx);
    }

    public static boolean isPointInsideCCWEdge(Point2D a, Point2D b, Point2D p) {
        long result = pointSide(a, b, p);
        return result >= 0;
    }

    //point on the edge can come out as -0.000000000001
    private static final double SIDE_EPSILON = 1e-9;

    public static boolean isPointInsideCCWEdge(Vec2 a, Vec2 b, Vec2 p) {
        return pointSide(a, b, p) >= -SIDE_EPSILON;
    }

    public static boolean isPointInsideConvexCCWPolygon(List<Point2D> points, Point2D p) {
        int n = points.size();
        for (int i = 0; i < n; i++) {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get((i + 1) % n);
            if (!isPointInsideCCWEdge(p1, p2, p))
                return false;
        }
        return true;
    }

    public static boolean isPolygonConvex(List<Point2D> points) {
        if (points.size() < 3) return false;

        int n = points.size();
        double turnSide = 0;
        double turnAngle = 0;
        for (int i = 0; i < n; i++) {
            Point2D prev = points.get(i);
            Point2D curr = points.get((i + 1) % n);
            Point2D next = points.get((i + 2) % n);

            long result = pointSide(prev, curr, next);

            if (result == 0)
            {
                //same direction = ok, going back or zero length edge = not convex
                long dot = dot(curr.getX() - prev.getX(), curr.getY() - prev.getY(),
                        next.getX() - curr.getX(), next.getY() - curr.getY());
                if (dot <= 0)
                    return false;
                continue;
            }
            int direction = Long.signum(result);

            if (turnSide == 0) {
                turnSide = direction;
            } else if (turnSide != direction)
                return false;

            turnAngle += angleBetweenEdgesDeg(prev, curr, next);
        }

        //the sum of turns is always a whole number of rounds: 360 for convex polygon, 720+ for a star
        //compare with the value in between, comparing with exactly 360 fails on rounding errors
        // turnSide == 0 means that all points are in aligned in line
        if (turnSide == 0 || Math.abs(turnAngle) > 540)
            return false;
        return true;
    }

    public static double angleBetweenEdgesRad(Point2D prev, Point2D curr, Point2D next) {

        //vector for Edge 1
        double e1x = curr.getX() - prev.getX();
        double e1y = curr.getY() - prev.getY();

        //vector for Edge 2
        double e2x = next.getX() - curr.getX();
        double e2y = next.getY() - curr.getY();

        // mathematical prescription using atan that gives us oriented angle between two vectors
        // math.atan2(u[0]*v[1] - u[1]*v[0], u[0]*v[0] + u[1]*v[1])
        return Math.atan2(cross(e1x, e1y, e2x, e2y), dot(e1x, e1y, e2x, e2y));
    }

    public static double angleBetweenEdgesDeg(Point2D prev, Point2D curr, Point2D next) {
        return Math.toDegrees(angleBetweenEdgesRad(prev, curr, next));
    }

    //SHOELACE FORMULA - tells us the area of the polygon and which way it goes (CW / CCW)
    //for every edge we do cross(point i, point i+1) and sum it up, the last point connects back to the first one
    //
    //every edge makes a triangle with the point (0, 0) and cross gives us its area times 2,
    //  with + or - depending on which way we go around (0, 0)
    //the space between (0, 0) and the polygon gets counted twice, once with + and once with -, so it just disappears
    //only the polygon itself is left and whether it ends up + or - tells us the direction
    //
    //on the canvas (Y goes down):
    //area2 < 0 : polygon goes counter-clockwise on screen
    //area2 > 0 : polygon goes clockwise on screen
    //area2 = 0 : all points are on one line, no direction at all
    //
    //it is 2x the area so we can keep it in long, the real area is |area2| / 2
    public static long signedArea2(List<Point2D> points) {
        long area2 = 0;
        int n = points.size();
        for (int i = 0; i < n; i++) {
            Point2D curr = points.get(i);
            Point2D next = points.get((i + 1) % n);
            area2 += cross((long) curr.getX(), curr.getY(), next.getX(), next.getY());
        }
        return area2;
    }

    //returns new list of Point2Ds that is always oriented CCW
    public static List<Point2D> orientatePolygonCCW(List<Point2D> points) {
        List<Point2D> result = new ArrayList<>(points);

        //if the area has positive sign
        if (signedArea2(result) > 0)
            Collections.reverse(result);
        return result;
    }




}
