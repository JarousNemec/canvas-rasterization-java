package model;

import rasterize.Rasterizable;
import rasterize.RasterizationContext;
import select.Hit;
import select.HitType;
import select.Selectable;
import utils.GeometryUtil;

public class Line implements Rasterizable, Selectable {
    private final Point2D[] points = new Point2D[2];

    public Line(Point2D p1, Point2D p2) {
        this.points[0] = p1;
        this.points[1] = p2;
    }

    public Line() {
        this.points[0] = new Point2D(0, 0);
        this.points[1] = new Point2D(0, 0);
    }

    public Point2D getPoint(int index) {
        return points[index];
    }

    public Point2D getStart() {
        return points[0];
    }

    public void setStart(Point2D start) {
        this.points[0] = start;
    }

    public void setStart(int x, int y) {
        this.points[0] = new Point2D(x, y);
    }

    public Point2D getEnd() {
        return points[1];
    }

    public void setEnd(Point2D end) {
        this.points[1] = end;
    }

    public void setEnd(int x, int y) {
        this.points[1] = new Point2D(x, y);
    }

    @Override
    public String toString() {
        return "Line{" + "start= X:" + points[0].getX() + " ,Y:" + points[0].getY() + ", end= X:" + points[1].getX() + " ,Y:" + points[1].getY() + '}';
    }

    @Override
    public void rasterize(RasterizationContext context) {
        context.getLineRasterizer().rasterize(points[0], points[1]);
    }

    @Override
    public Hit hitTest(int x, int y, int tolerance) {
        double toleranceSq = tolerance * tolerance;
        double bestDistanceSq = toleranceSq;
        int bestIndex = -1;
        for (int i = 0; i < points.length; i++) {
            var p = points[i];
            double distanceSq = GeometryUtil.distanceSqToPoint(x, y, p.getX(), p.getY());
            if (distanceSq <= bestDistanceSq) {
                bestDistanceSq = distanceSq;
                bestIndex = i;
            }
        }
        if (bestIndex != -1) {
            return new Hit(this, HitType.VERTEX, bestIndex, bestDistanceSq);
        }

        double distanceSq = GeometryUtil.distanceSqToSegment(x, y, points[0].getX(), points[0].getY(), points[1].getX(), points[1].getY());
        if (distanceSq <= toleranceSq) {
            bestIndex = 0;
            return new Hit(this, HitType.EDGE, bestIndex, distanceSq);
        }
        return null;
    }
}
