package select;

import model.Point2D;
import utils.GeometryUtil;

import java.util.List;

public final class SelectionUtil {
    public static final int DEFAULT_TOLERANCE = 5;

    private SelectionUtil() {
    }

    public static Hit findHit(List<? extends Selectable> candidates, int x, int y) {
        return findHit(candidates, x, y, DEFAULT_TOLERANCE);
    }

    public static Hit findHit(List<? extends Selectable> candidates, int x, int y, int tolerance) {
        Hit bestHit = null;

        //.reversed() is equivalent to for(int i = array.length - 1; i >= 0; i--){var item = array[i]}
        for (Selectable selectable : candidates.reversed()) {
            Hit hit = selectable.hitTest(x, y, tolerance);
            if (hit != null && isBetter(hit, bestHit)) {
                bestHit = hit;
            }
        }
        return bestHit;
    }

    public static Hit findHit(Selectable candidate, int x, int y) {
        return findHit(candidate, x, y, DEFAULT_TOLERANCE);
    }

    public static Hit findHit(Selectable candidate, int x, int y, int tolerance) {
        return candidate.hitTest(x, y, tolerance);
    }

    //hit test of a closed polygon given by its points, target = object that owns the points
    public static Hit hitTestPolygon(Selectable target, List<Point2D> points, int x, int y, int tolerance) {
        double toleranceSq = tolerance * tolerance;
        double bestDistanceSq = toleranceSq;
        int bestIndex = -1;

        //find the closest point of the polygon
        for (int i = 0; i < points.size(); i++) {
            var p = points.get(i);
            double distanceSq = GeometryUtil.distanceSquared(x, y, p.getX(), p.getY());
            if (distanceSq <= bestDistanceSq) {
                bestDistanceSq = distanceSq;
                bestIndex = i;
            }
        }

        //if found then return the point as the closest to given point because we have vertex hit priority
        if (bestIndex != -1) {
            return new Hit(target, HitType.VERTEX, bestIndex, bestDistanceSq, points.get(bestIndex));
        }

        //if vertex not found then we try to find the closest segment/edge
        int n = points.size();
        for (int i = 0; i < points.size(); i++) {
            var p1 = points.get(i);
            var p2 = points.get((i + 1) % n);
            double distanceSq = GeometryUtil.distanceSquaredToSegment(x, y, p1.getX(), p1.getY(), p2.getX(), p2.getY());
            if (distanceSq <= bestDistanceSq) {
                bestDistanceSq = distanceSq;
                bestIndex = i;
            }
        }
        if (bestIndex != -1) {
            var p1 = points.get(bestIndex);
            var p2 = points.get((bestIndex + 1) % n);
            Point2D closest = GeometryUtil.closestPointOnSegment(x, y, p1.getX(), p1.getY(), p2.getX(), p2.getY());

            return new Hit(target, HitType.EDGE, bestIndex, bestDistanceSq, closest);
        }
        return null;
    }

    private static boolean isBetter(Hit candidate, Hit best) {
        if (best == null) return true;
        if (candidate.hitType() != best.hitType())
            return candidate.hitType() == HitType.VERTEX; //vertex beats edge in selection
        return candidate.distanceSq() < best.distanceSq();
    }
}
