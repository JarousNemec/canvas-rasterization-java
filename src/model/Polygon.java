package model;

import rasterize.Rasterizable;
import rasterize.RasterizationContext;
import rasterize.RasterizationMode;
import select.Hit;
import select.HitType;
import select.Selectable;
import transform.Transformable;
import utils.GeometryUtil;

import java.util.ArrayList;
import java.util.List;

public class Polygon implements Rasterizable, Selectable, Transformable {
    private final List<Point2D> points;

    public Polygon() {
        points = new ArrayList<>();
    }
    public void addPoint(Point2D point) {
        points.add(point);
    }

    public Point2D getPoint(int i) {
        return points.get(i);
    }

    public int getSize() {
        return points.size();
    }

    public Point2D getFirst(){
        return points.getFirst();
    }
    public Point2D getLast(){
        return points.getLast();
    }

    public void clear() {
        points.clear();
    }

    @Override
    public void rasterize(RasterizationContext context) {
        context.getPolygonRasterizer().rasterize(this);
    }

    @Override
    public void rasterize(RasterizationContext context, RasterizationMode mode) {
        context.getPolygonRasterizer().rasterize(this, mode);
    }

    @Override
    public Hit hitTest(int x, int y, int tolerance) {
        double toleranceSq = tolerance * tolerance;
        double bestDistanceSq = toleranceSq;
        int bestIndex = -1;
        for (int i = 0; i < points.size(); i++) {
            var p = points.get(i);
            double distanceSq = GeometryUtil.distanceSqToPoint(x, y, p.getX(), p.getY());
            if (distanceSq <= bestDistanceSq) {
                bestDistanceSq = distanceSq;
                bestIndex = i;
            }
        }
        if (bestIndex != -1) {
            return new Hit(this, HitType.VERTEX,  bestIndex, bestDistanceSq);
        }
        int n = points.size();
        for (int i = 0; i < points.size(); i++) {
            int indexP1 = i;
            int indexP2 = (i + 1) % n;
            var p1 = points.get(i);
            var p2 = points.get(indexP2);
            double distanceSq = GeometryUtil.distanceSqToSegment(x, y, p1.getX(), p1.getY(), p2.getX(), p2.getY());
            if (distanceSq <= bestDistanceSq) {
                bestDistanceSq = distanceSq;
                bestIndex = i;
            }
        }
        if (bestIndex != -1) {
            return new Hit(this, HitType.EDGE,  bestIndex, bestDistanceSq);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Polygon with " + points.size() + " points.";
    }

    @Override
    public void setNewPointPosition(int index, Point2D newPosition) {
        if(newPosition != null && index < points.size() && index >= 0 )
            points.set(index, newPosition);
    }
}
