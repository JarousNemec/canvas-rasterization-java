package rasterize;

import edit.VertexMovable;
import model.Point2D;
import raster.Raster;
import select.SelectionUtil;
import utils.ColorUtil;

//draws a filled dot around vertices, so the user can see where a vertex can be grabbed
//the radius is the selection tolerance and the condition is the same as in the hit test (distanceSq <= radiusSq),
//so the dot covers exactly the area where the vertex is hit
public class VertexMarkerRasterizer implements Rasterizer {
    private final Raster raster;
    private int color = ColorUtil.VERTEX_MARKER_COLOR;
    private final int radius = SelectionUtil.DEFAULT_TOLERANCE;

    public VertexMarkerRasterizer(Raster raster) {
        this.raster = raster;
    }

    public void rasterize(VertexMovable movable) {
        for (Point2D vertex : movable.getVertices())
            rasterize(vertex);
    }

    public void rasterize(Point2D center) {
        int radiusSq = radius * radius;
        for (int dy = -radius; dy <= radius; dy++)
            for (int dx = -radius; dx <= radius; dx++)
                if (dx * dx + dy * dy <= radiusSq)
                    raster.setPixel(center.getX() + dx, center.getY() + dy, color);
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int getColor() {
        return color;
    }
}
