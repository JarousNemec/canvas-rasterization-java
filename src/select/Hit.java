package select;

import model.Point2D;

public record Hit(Selectable target, HitType hitType, int index, double distanceSq, Point2D closestPoint) {

    //e.g. "Hit{EDGE 3 of Polygon with 4 points., distance=4.00 px, closestX = 3 closestY = 4}"
    @Override
    public String toString() {
        return String.format("Hit{%s %d of %s, distance=%.2f px, closestX = %d closestY = %d}", hitType, index, target, Math.sqrt(distanceSq), closestPoint.getX(), closestPoint.getY());
    }
}
