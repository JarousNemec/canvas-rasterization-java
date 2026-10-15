package rasterize;

import model.Point2D;
import model.Polygon;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon) {
        // TODO: kontrola, jestli je min počet vrcholů

        for(int i = 0; i < polygon.getSize(); i++) {
            int indexA = i;
            // Pozor, abych nesahal mimo seznam a zráoveˇmn chci spojit poslední point s nultým
            int indexB = i + 1;
            Point2D a = polygon.getPoint(indexA);
            Point2D b = polygon.getPoint(indexB);

            // TODO: rasterizovat úsečku od a do b
        }
    }
}
