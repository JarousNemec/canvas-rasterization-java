package model;

import rasterize.Rasterizable;
import rasterize.RasterizationContext;

public class Line implements Rasterizable {
    private Point2D start;
    private Point2D end;

    public Line(Point2D p1, Point2D p2) {
        this.start = p1;
        this.end = p2;
    }

    public Line() {
        this.start = new Point2D(0, 0);
        this.end = new Point2D(0, 0);
    }

    public Point2D getStart() {
        return start;
    }

    public void setStart(Point2D start) {
        this.start = start;
    }

    public void setStart(int x, int y) {
        this.start = new Point2D(x, y);
    }

    public Point2D getEnd() {
        return end;
    }

    public void setEnd(Point2D end) {
        this.end = end;
    }

    public void setEnd(int x, int y) {
        this.end = new Point2D(x, y);
    }

    @Override
    public String toString() {
        return "Line{" + "start= X:" + start.getX() + " ,Y:" + start.getY() + ", end= X:" + end.getX() + " ,Y:" + end.getY() + '}';
    }

    @Override
    public void rasterize(RasterizationContext context) {
        context.getLineRasterizer().rasterize(start, end);
    }
}
