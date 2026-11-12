package fill;

import fill.pattern.PatternFill;
import fill.pattern.SolidPattern;
import model.Edge;
import model.Point2D;
import model.Polygon;
import raster.Raster;
import sort.InsertionSort;
import utils.ColorUtil;

import java.util.ArrayList;

public class ScanLineFiller implements Filler {

    private final Raster raster;
    private PatternFill pattern = new SolidPattern(ColorUtil.DEFAULT_FILL_COLOR);
    private Polygon polygon;

    public ScanLineFiller(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void fill() {
        if (polygon == null)
            return;
        if (polygon.getSize() < 3)
            return;

        int yMin = polygon.getFirst().getY();
        int yMax = polygon.getFirst().getY();

        //find min and max Y
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
            if (edge.isHorizontal())
                continue;
            edge.orientate();
            edges.add(edge);
        }

        ArrayList<Integer> intersectionsX = new ArrayList<>();
        for (int y = yMin; y <= yMax; y++) {
            intersectionsX.clear();
            for (var edge : edges) {
                if (edge.isIntersection(y)) {
                    int intersectionX = edge.getIntersectionX(y);
                    intersectionsX.add(intersectionX);
                }
            }

            InsertionSort.sort(intersectionsX);

            //fill between pairs: 1st-2nd, 3rd-4th, ...
            for (int i = 0; i < intersectionsX.size() - 1; i = i + 2) {
                for (int x = intersectionsX.get(i); x < intersectionsX.get(i + 1); x++) {
                    raster.setPixel(x, y, pattern.getPixelForPoint(x, y));
                }
            }
        }
    }

    @Override
    public PatternFill getPattern() {
        return pattern;
    }

    @Override
    public void setPattern(PatternFill pattern) {
        this.pattern = pattern;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
