package utils;

import model.Point2D;

public class GeometryUtil {
    private GeometryUtil() {
    }

    public static double distanceSqToSegment(int px, int py, int ax, int ay, int bx, int by) {
        //parameter to line equation
        double t = closestPointOnSegmentParameter(px, py, ax, ay, bx, by);

        //calculate the point on the segment that is closest to the observed point
        //parametrized line equation
        double closestX = lineralInterpolation(ax, bx, t);
        double closestY = lineralInterpolation(ay, by, t);

        //calculate the length of the vector from the closest point to the observed point
        return squaredPointsDistance(closestX, closestY, px, py);
    }

    public static Point2D closestPointOnSegmentToPoint(int px, int py, int ax, int ay, int bx, int by) {
        //parameter to line equation
        double t = closestPointOnSegmentParameter(px, py, ax, ay, bx, by);

        //calculate the point on the segment that is closest to the observed point
        //parametrized line equation
        double closestX = lineralInterpolation(ax, bx, t);
        double closestY = lineralInterpolation(ay, by, t);

        return new Point2D((int) Math.round(closestX), (int) Math.round(closestY));
    }

    private static double lineralInterpolation(double a, double b, double t){
        return a + t * (b-a);
    }

    private static double closestPointOnSegmentParameter(int px, int py, int ax, int ay, int bx, int by) {
        //vector of the segment
        double vx = bx - ax;
        double vy = by - ay;

        //vector from A to Point
        double ux = px - ax;
        double uy = py - ay;

        //squared length of the segment
        double vLengthSq = vectorLengthSq(vx, vy);

        //squared length of the segment must not be zero to avoid division by zero
        if (vLengthSq <= 0) return 0;

        //dot product of the segment vector and the observed point vector
        double dot = dotProduct(vx, vy, ux, uy);

        //scalar product divided by the squared length gives us the position of the closest point as a portion of the segment
        double t = dot / vLengthSq;

        //handle edge cases where the observed point lies before the start or past the end of the segment, so the closest point is one of the segment's endpoints
        if (t < 0) t = 0;
        else if (t > 1) t = 1;

        return t;
    }

    public static double squaredPointsDistance(double x1, double y1, double x2, double y2) {
        //calculate squared distance between two point
        double diffX = x2 - x1;
        double diffY = y2 - y1;
        return diffX * diffX + diffY * diffY;
    }

    public static double dotProduct(double vx, double vy, double ux, double uy) {
        return vx * ux + vy * uy;
    }

    public static double vectorLengthSq(double x, double y) {
        return x * x + y * y;
    }

    public static double vectorLength(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    //tan(22.5°) - boundary between axis and diagonal direction (halfway between 0° and 45°)
    //in the condition it stands for steps on the other axis at the current position on the base axis
    private static final double ATTACHMENT_THRESHOLD = Math.tan(Math.toRadians(22.5));

    public static Point2D attachPointToAxis(int startX, int startY, int endX, int endY) {
        int dx = endX - startX;
        int dy = endY - startY;
        int adx = Math.abs(dx);
        int ady = Math.abs(dy);

        //attach to Y axis
        if (ady <= adx * ATTACHMENT_THRESHOLD)
            return new Point2D(endX, startY);

        //attach to X axis
        if (adx <= ady * ATTACHMENT_THRESHOLD)
            return new Point2D(startX, endY);

        //attach to diagonal
        int longerCoordinate = Math.max(adx, ady);
        return new Point2D(startX + Integer.signum(dx) * longerCoordinate, startY + Integer.signum(dy) * longerCoordinate);
    }
}
