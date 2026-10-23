package utils;

public class GeometryUtil {
    private GeometryUtil() {}
    public static double distanceSqToSegment(int px, int py, int sx1, int sy1, int sx2, int sy2) {

        //vector of the segment
        double dx = sx2 - sx1;
        double dy = sy2 - sy1;

        //squared length of the segment
        double lengthSq = dx * dx + dy * dy;

        //vector from the first segment point to the observed point
        double toPointVectorX = px - sx1;
        double toPointVectorY = py - sy1;

        //position parameter on the segment
        double t = 0;

        //squared length of the segment must not be zero to avoid division by zero
        if (lengthSq != 0) {

            //scalar product of the segment vector and the observed point vector
            double dot = dx * toPointVectorX + dy * toPointVectorY;

            //scalar product divided by the squared length gives us the position of the closest point as a portion of the segment
            t = dot / lengthSq;

            //handle edge cases where the observed point lies before the start or past the end of the segment, so the closest point is one of the segment's endpoints
            if (t < 0) t = 0;
            if (t > 1) t = 1;
        }

        //calculate the point on the segment that is closest to the observed point
        double closestX = sx1 + t * dx;
        double closestY = sy1 + t * dy;

        //calculate the vector from the closest point to the observed point
        double diffX = px - closestX;
        double diffY = py - closestY;

        //calculate the squared length of the difference vector
        return diffX * diffX + diffY * diffY;
    }

    public static double distanceSqToPoint(int x1, int y1, int x2, int y2) {
        double diffX = x2 - x1;
        double diffY = y2 - y1;
        return diffX * diffX + diffY * diffY;
    }
}
