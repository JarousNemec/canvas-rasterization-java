package utils;

import model.Point2D;

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
    public static Point2D lineIntersection(Point2D a, Point2D b, Point2D c, Point2D d) {
        double numerator = (c.getX() - a.getX()) * (d.getY() - c.getY()) + (a.getY() - c.getY()) * (d.getX() - c.getX());
        double denominator = (b.getX() - a.getX()) * (d.getY() - c.getY()) + (a.getY() - b.getY()) * (d.getX() - c.getX());
        if (denominator != 0) {
            double t = numerator / denominator;
            int x = (int) Math.round(lerp(a.getX(), b.getX(), t));
            int y = (int) Math.round(lerp(a.getY(), b.getY(), t));
            return new Point2D(x, y);
        }
        return null;
    }
}
