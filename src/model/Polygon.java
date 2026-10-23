package model;

import rasterize.Rasterizable;
import rasterize.RasterizationContext;

import java.util.ArrayList;
import java.util.List;

public class Polygon implements Rasterizable {
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
}
