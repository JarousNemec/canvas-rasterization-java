package rasterize;

import model.Point2D;
import raster.Raster;
import utils.ColorUtil;


public abstract class LineRasterizer implements Rasterizer {
    protected Raster raster;
    protected int defaultColor = ColorUtil.DEFAULT_LINE_COLOR;
    protected int color = defaultColor;


    public LineRasterizer(Raster raster) {
        this.raster = raster;
    }

    public LineRasterizer(Raster raster, int defaultColor) {
        this.raster = raster;
        this.defaultColor = defaultColor;
    }

    public int getColor() {
        return color;
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void resetColor() {
        this.color = defaultColor;
    }

    public void rasterize(int x1, int y1, int x2, int y2) {
        rasterize(x1, y1, x2, y2, RasterizationMode.NORMAL);
    }

    public void rasterize(Point2D p1, Point2D p2) {
        rasterize(p1.getX(), p1.getY(), p2.getX(), p2.getY(), RasterizationMode.NORMAL);
    }

    public void rasterize(int x1, int y1, Point2D p2) {
        rasterize(x1, y1, p2.getX(), p2.getY(), RasterizationMode.NORMAL);
    }

    public void rasterize(Point2D p1, int x2, int y2) {
        rasterize(p1.getX(), p1.getY(), x2, y2, RasterizationMode.NORMAL);
    }

    public void rasterize(Point2D p1, Point2D p2, RasterizationMode mode) {
        rasterize(p1.getX(), p1.getY(), p2.getX(), p2.getY(), mode);
    }

    public void rasterize(int x1, int y1, Point2D p2, RasterizationMode mode) {
        rasterize(x1, y1, p2.getX(), p2.getY(), mode);
    }

    public void rasterize(Point2D p1, int x2, int y2, RasterizationMode mode) {
        rasterize(p1.getX(), p1.getY(), x2, y2, mode);
    }

    // Every rasterize method overload uses this method in result
    public abstract void rasterize(int x1, int y1, int x2, int y2, RasterizationMode mode);

    public boolean isPointInRaster(int x, int y) {
        return (x >= 0 && y >= 0 && x < raster.getWidth() && y < raster.getHeight());
    }
}
