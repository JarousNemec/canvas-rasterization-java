package model;

import clip.ClipContext;
import clip.Clippable;
import clip.SutherlandHogman;
import edit.VertexListEditable;
import fill.Fillable;
import fill.ScanLineFiller;
import rasterize.*;
import select.Hit;
import select.SelectionUtil;
import select.Selectable;
import utils.ColorUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Polygon implements Rasterizable, Fillable, Selectable, VertexListEditable, Clippable {
    private final List<Point2D> points;
    private int color = ColorUtil.DEFAULT_LINE_COLOR;
    private int fillColor = ColorUtil.DEFAULT_FILL_COLOR;
    private boolean isFilled = false;


    public Polygon() {
        this.points = new ArrayList<>();
    }
    public Polygon(List<Point2D> points) {
        this.points = points;
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
        return SelectionUtil.hitTestPolygon(this, points, x, y, tolerance);
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

    @Override
    public Rasterizable clip(ClipContext context) {
        var clipPolygon = context.getClipPolygon();
        if (!context.isPolygonClippingEnabled() || clipPolygon == null)
            return this;
        var clippedPoints = SutherlandHogman.clip(this.points, clipPolygon.points);
        if (clippedPoints.isEmpty())
            return null;
        var clippedPolygon = new Polygon(clippedPoints);
        clippedPolygon.color = this.color;
        clippedPolygon.fillColor = this.fillColor;
        clippedPolygon.isFilled = this.isFilled;
        return clippedPolygon;
    }

    public List<Point2D> getPointsReadOnly() { return Collections.unmodifiableList(points); }
}
