package model;

import edit.VertexListEditable;
import fill.Fillable;
import fill.ScanLineFiller;
import rasterize.*;
import select.Hit;
import select.HitType;
import select.Selectable;
import edit.VertexMovable;
import utils.ColorUtil;
import utils.GeometryUtil;

import java.util.ArrayList;
import java.util.List;

public class Polygon implements Rasterizable, Fillable, Selectable, VertexListEditable {
    private final List<Point2D> points;
    private int color = ColorUtil.DEFAULT_LINE_COLOR;
    private int fillColor = ColorUtil.DEFAULT_FILL_COLOR;
    private boolean isFilled = false;


    public Polygon() {
        points = new ArrayList<>();
    }

    public void addPoint(Point2D point) {
        points.add(point);
    }

    @Override
    public Point2D getPoint(int i) {
        return points.get(i);
    }

    @Override
    public int getSize() {
        return points.size();
    }

    @Override
    public boolean canRemovePoint() {
        return points.size() > 3;
    }

    @Override
    public void removePoint(int i) {
        points.remove(i);
    }

    public Point2D getFirst() {
        return points.getFirst();
    }

    public Point2D getLast() {
        return points.getLast();
    }

    @Override
    public void addPoint(int index, Point2D point) {
        points.add(index, point);
    }

    public void clear() {
        points.clear();
    }

    @Override
    public void rasterize(RasterizationContext context) {
        rasterize(context, RasterizationMode.NORMAL);
    }

    @Override
    public void rasterize(RasterizationContext context, RasterizationMode mode) {
        if (this.isFilled) {
            ScanLineFiller filler = context.getScanLineFiller();
            filler.setPolygon(this);
            filler.setFillColor(this.fillColor);
            filler.fill();
        }
        PolygonRasterizer rasterizer = context.getPolygonRasterizer();
        rasterizer.setColor(this.color);
        rasterizer.rasterize(this, mode);

    }


    @Override
    public Hit hitTest(int x, int y, int tolerance) {
        double toleranceSq = tolerance * tolerance;
        double bestDistanceSq = toleranceSq;
        int bestIndex = -1;

        //find the closest point of the polygon
        for (int i = 0; i < points.size(); i++) {
            var p = points.get(i);
            double distanceSq = GeometryUtil.squaredPointsDistance(x, y, p.getX(), p.getY());
            if (distanceSq <= bestDistanceSq) {
                bestDistanceSq = distanceSq;
                bestIndex = i;
            }
        }

        //if found then return the point as the closest to given point because we have vertex hit priority
        if (bestIndex != -1) {
            return new Hit(this, HitType.VERTEX, bestIndex, bestDistanceSq, points.get(bestIndex));
        }

        //if vertex not found then we try to find the closest segment/edge
        int n = points.size();
        for (int i = 0; i < points.size(); i++) {
            var p1 = points.get(i);
            var p2 = points.get((i + 1) % n);
            double distanceSq = GeometryUtil.distanceSqToSegment(x, y, p1.getX(), p1.getY(), p2.getX(), p2.getY());
            if (distanceSq <= bestDistanceSq) {
                bestDistanceSq = distanceSq;
                bestIndex = i;
            }
        }
        if (bestIndex != -1) {
            var p1 = points.get(bestIndex);
            var p2 = points.get((bestIndex + 1) % n);
            Point2D closest = GeometryUtil.closestPointOnSegmentToPoint(x, y, p1.getX(), p1.getY(), p2.getX(), p2.getY());

            return new Hit(this, HitType.EDGE, bestIndex, bestDistanceSq, closest);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Polygon with " + points.size() + " points.";
    }

    @Override
    public void setNewPointPosition(int index, Point2D newPosition) {
        if (newPosition != null && index < points.size() && index >= 0)
            points.set(index, newPosition);
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public void setFillColor(int color) {
        this.fillColor = color;
    }

    @Override
    public int getFillColor() {
        return this.fillColor;
    }

    @Override
    public void enableFill() {
        isFilled = true;
    }

    @Override
    public void disableFill() {
        isFilled = false;
    }

    @Override
    public boolean isFilled() {
        return isFilled;
    }
}
