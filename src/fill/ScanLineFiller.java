package fill;

import model.Edge;
import model.Point2D;
import model.Polygon;
import raster.Raster;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;


public class ScanLineFiller implements Filler {

    private Raster raster;
    private int fillColor = Color.blue.getRGB();
    private Polygon polygon;

    public ScanLineFiller(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void fill() {
        if (polygon == null)
            return;
        if (polygon.getSize() < 2)
            return;

        int yMin = polygon.getFirst().getY();
        int yMax = polygon.getFirst().getY();
        for (int i = 0; i < polygon.getSize(); i++) {
            var y = polygon.getPoint(i).getY();
            if (y > yMax)
                yMax = y;
            if (y < yMin)
                yMin = y;
        }

        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < polygon.getSize(); i++) {
            Point2D p1 = polygon.getPoint(i);
            Point2D p2 = polygon.getPoint((i + 1) % polygon.getSize());

            Edge edge = new Edge(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            if (edge.isHorizontal()) {
                edge.orientate();
                edges.add(edge);
            }
        }

        ArrayList<Integer> intersectionsX = new ArrayList<>();
        for (int y = yMin; y <= yMax; y++) {
            intersectionsX.clear();
            for (var edge : edges) {
                if (edge.isIntersection(y)) {
                    intersectionsX.add(edge.getIntersectionX(y));
                }
            }

            Collections.sort(intersectionsX);

            for (int i = 0; i < intersectionsX.size(); i = i + 2) {
                for (int x = intersectionsX.get(i); x < intersectionsX.get(i + 1); x++) {
                    raster.setPixel(x, y, fillColor);
                }
            }
        }
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
