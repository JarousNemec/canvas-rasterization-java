package utils;

import model.Point2D;

import java.util.ArrayList;
import java.util.List;

//double point for calculations, round to Point2D only at the end
public record Vec2(double x, double y) {

    public static Vec2 of(Point2D p) {
        return new Vec2(p.getX(), p.getY());
    }

    public static List<Vec2> of(List<Point2D> points) {
        List<Vec2> result = new ArrayList<>(points.size());
        for (Point2D p : points)
            result.add(of(p));
        return result;
    }

    public Point2D toPoint2D() {
        return new Point2D((int) Math.round(x), (int) Math.round(y));
    }

    public static List<Point2D> toPoint2D(List<Vec2> points) {
        List<Point2D> result = new ArrayList<>(points.size());
        for (Vec2 p : points)
            result.add(p.toPoint2D());
        return result;
    }
}
