package model;

import clip.ClipContext;
import clip.Clippable;
import clip.CohenSutherland;
import rasterize.LineRasterizer;
import rasterize.Rasterizable;
import rasterize.RasterizationContext;
import rasterize.RasterizationMode;
import select.Hit;
import select.HitType;
import select.Selectable;
import edit.VertexMovable;
import utils.GeometryUtil;

import java.awt.*;

public class Line implements Rasterizable, Selectable, VertexMovable, Clippable {
    private final Point2D[] points = new Point2D[2];
    private int color = Color.WHITE.getRGB();

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
        rasterize(context, RasterizationMode.NORMAL);
    }

    @Override
    public void rasterize(RasterizationContext context, RasterizationMode mode) {
        LineRasterizer rasterizer = context.getLineRasterizer();
        rasterizer.setColor(this.color);
        rasterizer.rasterize(points[0], points[1], mode);
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
    public Hit hitTest(int x, int y, int tolerance) {
        double toleranceSq = tolerance * tolerance;
        double bestDistanceSq = toleranceSq;
        int bestIndex = -1;
        for (int i = 0; i < points.length; i++) {
            var p = points[i];
            double distanceSq = GeometryUtil.distanceSquared(x, y, p.getX(), p.getY());
            if (distanceSq <= bestDistanceSq) {
                bestDistanceSq = distanceSq;
                bestIndex = i;
            }
        }
        if (bestIndex != -1) {
            return new Hit(this, HitType.VERTEX, bestIndex, bestDistanceSq, points[bestIndex]);
        }

        double distanceSq = GeometryUtil.distanceSquaredToSegment(x, y, points[0].getX(), points[0].getY(), points[1].getX(), points[1].getY());
        if (distanceSq <= toleranceSq) {
            bestIndex = 0;
            var p1 = points[0];
            var p2 = points[1];
            Point2D closest = GeometryUtil.closestPointOnSegment(x, y, p1.getX(), p1.getY(), p2.getX(), p2.getY());
            return new Hit(this, HitType.EDGE, bestIndex, distanceSq, closest);
        }
        return null;
    }

    @Override
    public void setNewPointPosition(int index, Point2D newPosition) {
        if(newPosition != null && index < points.length && index >= 0 )
            points[index] = newPosition;
    }

    @Override
    public Rasterizable clip(ClipContext context) {
        var clipWindow = context.getClipWindow();
        if (!context.isLineClippingEnabled() || clipWindow == null)
            return this;
        var clippedPoints = CohenSutherland.clip(points[0], points[1], clipWindow);
        if (clippedPoints.length == 0)
            return null;
        var clippedLine = new Line(clippedPoints[0], clippedPoints[1]);
        clippedLine.setColor(this.color);
        return clippedLine;
    }
}
