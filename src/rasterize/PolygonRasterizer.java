package rasterize;

import model.Point2D;
import model.Polygon;

public class PolygonRasterizer {
    private final LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon) {
        rasterize(polygon, RasterizationMode.NORMAL);
    }

    public void rasterize(Polygon polygon, RasterizationMode mode) {
        int n = polygon.getSize();
        if (n < 2)
            return;

        for (int i = 0; i < n; i++) {
            int indexP1 = i;
            int indexP2 = (i + 1) % n;
            Point2D p1 = polygon.getPoint(indexP1);
            Point2D p2 = polygon.getPoint(indexP2);

            lineRasterizer.rasterize(p1, p2, mode);
        }
    }

    public void rasterizePointPreview(int x, int y, Polygon polygon) {
        if (polygon.getSize() >= 2 && lineRasterizer.isPointInRaster(x, y)) {
            lineRasterizer.rasterize(x, y, polygon.getFirst(), RasterizationMode.PREVIEW);
            lineRasterizer.rasterize(x, y, polygon.getLast(), RasterizationMode.PREVIEW);
            lineRasterizer.rasterize(polygon.getFirst(), polygon.getLast(), RasterizationMode.ALERT);
        }
    }
}
