package rasterize;

import mode.RasterizationMode;
import model.Point2D;
import model.Polygon;
import utils.ColorUtils;

import java.awt.*;

public class PolygonRasterizer {
    private final LineRasterizer lineRasterizer;
    private static final int PREVIEW_COLOR = ColorUtils.toRgbInt(Color.ORANGE);
    private static final int ALERT_COLOR = ColorUtils.toRgbInt(Color.red);

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon) {
        if (polygon.getSize() < 2)
            return;
        if (polygon.getSize() > 2) {
            Point2D first = polygon.getFirst();
            Point2D last = polygon.getLast();
            lineRasterizer.rasterize(first, last);
        }
        for (int i = 0; i < polygon.getSize() - 1; i++) {
            int indexP1 = i;
            int indexP2 = i + 1;
            Point2D p1 = polygon.getPoint(indexP1);
            Point2D p2 = polygon.getPoint(indexP2);

            lineRasterizer.rasterize(p1, p2);
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
