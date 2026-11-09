package model;

import edit.VertexMovable;
import rasterize.RasterizationContext;
import select.Hit;
import select.Selectable;
import select.SelectionUtil;
import utils.ColorUtil;

import java.util.List;

public class ClipWindow implements Selectable, VertexMovable {
    private static final int MIN_SIZE = 1; //min width and height

    //top left corner
    private Point2D cornerA;

    //bottom right corner
    private Point2D cornerB;

    public ClipWindow(Point2D cornerB, Point2D cornerA) {
        setCornerB(cornerB);
        setCornerA(cornerA);
    }

    public int getXMin() { return Math.min(cornerA.getX(), cornerB.getX()); }
    public int getXMax() { return Math.max(cornerA.getX(), cornerB.getX()); }
    public int getYMin() { return Math.min(cornerA.getY(), cornerB.getY()); }
    public int getYMax() { return Math.max(cornerA.getY(), cornerB.getY()); }

    //corners CCW on screen: 0 = top-left, 1 = bottom-left, 2 = bottom-right, 3 = top-right
    @Override
    public List<Point2D> getVertices() {
        int xMin = getXMin();
        int xMax = getXMax();
        int yMin = getYMin();
        int yMax = getYMax();
        return List.of(
                new Point2D(xMin, yMin),
                new Point2D(xMin, yMax),
                new Point2D(xMax, yMax),
                new Point2D(xMax, yMin)
        );
    }

    public int getWidth()  { return getXMax() - getXMin(); }
    public int getHeight() { return getYMax() - getYMin(); }

    public void setCornerB(Point2D cornerB) {
        this.cornerB = cornerB;
    }

    public Point2D getCornerA() {
        return cornerA;
    }

    public void setCornerA(Point2D cornerA) {
        this.cornerA = cornerA;
    }

    //moves corner by index (as in getVertices), opposite corner stays
    //clamped so the corners can't swap (index in Hit would point to another corner)
    @Override
    public void setNewPointPosition(int index, Point2D newPosition) {
        if (newPosition == null)
            return;

        int xMin = getXMin();
        int xMax = getXMax();
        int yMin = getYMin();
        int yMax = getYMax();
        int x = newPosition.getX();
        int y = newPosition.getY();

        switch (index) {
            case 0 -> { // top-left
                xMin = Math.min(x, xMax - MIN_SIZE);
                yMin = Math.min(y, yMax - MIN_SIZE);
            }
            case 1 -> { // bottom-left
                xMin = Math.min(x, xMax - MIN_SIZE);
                yMax = Math.max(y, yMin + MIN_SIZE);
            }
            case 2 -> { // bottom-right
                xMax = Math.max(x, xMin + MIN_SIZE);
                yMax = Math.max(y, yMin + MIN_SIZE);
            }
            case 3 -> { // top-right
                xMax = Math.max(x, xMin + MIN_SIZE);
                yMin = Math.min(y, yMax - MIN_SIZE);
            }
            default -> {
                return;
            }
        }

        cornerA = new Point2D(xMin, yMin);
        cornerB = new Point2D(xMax, yMax);
    }

    public void visualize(RasterizationContext context) {
        int xMin = getXMin();
        int xMax = getXMax();
        int yMin = getYMin();
        int yMax = getYMax();
        var lineRasterizer = context.getLineRasterizer();
        lineRasterizer.setColor(ColorUtil.CLIP_WINDOW_COLOR);
        lineRasterizer.rasterize(xMin, yMin, xMax, yMin);
        lineRasterizer.rasterize(xMax, yMin, xMax, yMax);
        lineRasterizer.rasterize(xMax, yMax, xMin, yMax);
        lineRasterizer.rasterize(xMin, yMax, xMin, yMin);
    }

    @Override
    public Hit hitTest(int x, int y, int tolerance) {
        return SelectionUtil.hitTestPolygon(this, getVertices(), x, y, tolerance);
    }

    @Override
    public String toString() {
        return "ClipWindow [" + getXMin() + ", " + getYMin() + "] - [" + getXMax() + ", " + getYMax() + "]";
    }
}
