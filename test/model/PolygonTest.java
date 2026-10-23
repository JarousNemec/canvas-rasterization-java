package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import select.Hit;
import select.HitType;

import static org.junit.jupiter.api.Assertions.*;

class PolygonTest {

    private static final int TOLERANCE = 5;

    //allowed difference when comparing doubles - floating point math is not exact,
    //so assertEquals(expected, actual, DELTA) passes if |expected - actual| <= DELTA
    private static final double DELTA = 1e-9;

    //square (0, 0) -> (100, 0) -> (100, 100) -> (0, 100)
    //edges: 0 = top, 1 = right, 2 = bottom, 3 = left (closing edge from the last point to the first)
    private Polygon square;

    @BeforeEach
    void setUp() {
        square = polygonOf(0, 0, 100, 0, 100, 100, 0, 100);
    }

    //helper for creating a polygon in one line
    //takes any number of ints (varargs) as pairs x, y: polygonOf(x0, y0, x1, y1, ...)
    //so polygonOf(0, 0, 100, 0) creates a polygon with points (0, 0) and (100, 0)
    private static Polygon polygonOf(int... coordinates) {
        Polygon polygon = new Polygon();
        //step by 2 - coordinates[i] is x and coordinates[i + 1] is y of the same point
        for (int i = 0; i < coordinates.length; i += 2) {
            polygon.addPoint(new Point2D(coordinates[i], coordinates[i + 1]));
        }
        return polygon;
    }

    // vertices

    @Test
    void hitTest_nearVertex_returnsVertexWithIndex() {
        Hit hit = square.hitTest(98, 97, TOLERANCE);

        assertNotNull(hit);
        assertSame(square, hit.target());
        assertEquals(HitType.VERTEX, hit.hitType());
        assertEquals(2, hit.index());
        assertEquals(13, hit.distanceSq(), DELTA); //2² + 3²
    }

    @Test
    void hitTest_onEdgeNearVertex_vertexWins() {
        //edge distance is 0, but vertex 0 is within tolerance
        Hit hit = square.hitTest(3, 0, TOLERANCE);

        assertNotNull(hit);
        assertEquals(HitType.VERTEX, hit.hitType());
        assertEquals(0, hit.index());
        assertEquals(9, hit.distanceSq(), DELTA);
    }

    // edges

    @Test
    void hitTest_nearTopEdge_returnsEdgeZero() {
        Hit hit = square.hitTest(50, -3, TOLERANCE);

        assertNotNull(hit);
        assertEquals(HitType.EDGE, hit.hitType());
        assertEquals(0, hit.index());
        assertEquals(9, hit.distanceSq(), DELTA);
    }

    @Test
    void hitTest_nearRightEdge_returnsEdgeOne() {
        Hit hit = square.hitTest(102, 50, TOLERANCE);

        assertNotNull(hit);
        assertEquals(HitType.EDGE, hit.hitType());
        assertEquals(1, hit.index());
        assertEquals(4, hit.distanceSq(), DELTA);
    }

    @Test
    void hitTest_nearClosingEdge_returnsLastEdge() {
        //left edge goes from the last point (0, 100) back to the first point (0, 0)
        Hit hit = square.hitTest(-4, 50, TOLERANCE);

        assertNotNull(hit);
        assertEquals(HitType.EDGE, hit.hitType());
        assertEquals(3, hit.index());
        assertEquals(16, hit.distanceSq(), DELTA);
    }

    @Test
    void hitTest_nearCornerOutsideVertexTolerance_closerEdgeWins() {
        //vertex 0 is too far (5² + 3² = 34), top edge is 3 px away, left edge 5 px away
        Hit hit = square.hitTest(5, 3, TOLERANCE);

        assertNotNull(hit);
        assertEquals(HitType.EDGE, hit.hitType());
        assertEquals(0, hit.index());
        assertEquals(9, hit.distanceSq(), DELTA);
    }

    @Test
    void hitTest_nearCornerOutsideVertexTolerance_otherEdgeWins() {
        //mirrored case: left edge is 3 px away, top edge 5 px away
        Hit hit = square.hitTest(3, 5, TOLERANCE);

        assertNotNull(hit);
        assertEquals(HitType.EDGE, hit.hitType());
        assertEquals(3, hit.index());
        assertEquals(9, hit.distanceSq(), DELTA);
    }

    // miss

    @Test
    void hitTest_insidePolygon_returnsNull() {
        //polygon is only an outline, the inside is not selectable
        assertNull(square.hitTest(50, 50, TOLERANCE));
    }

    @Test
    void hitTest_farOutside_returnsNull() {
        assertNull(square.hitTest(200, 200, TOLERANCE));
    }

    // degenerate polygons

    @Test
    void hitTest_emptyPolygon_returnsNull() {
        assertNull(new Polygon().hitTest(0, 0, TOLERANCE));
    }

    @Test
    void hitTest_singlePoint_nearIsVertex() {
        Polygon single = polygonOf(10, 10);

        Hit hit = single.hitTest(12, 10, TOLERANCE);

        assertNotNull(hit);
        assertEquals(HitType.VERTEX, hit.hitType());
        assertEquals(0, hit.index());
    }

    @Test
    void hitTest_singlePoint_farIsNull() {
        assertNull(polygonOf(10, 10).hitTest(50, 50, TOLERANCE));
    }

    @Test
    void hitTest_twoPoints_middleIsEdge() {
        Polygon twoPoints = polygonOf(0, 0, 100, 0);

        Hit hit = twoPoints.hitTest(50, 2, TOLERANCE);

        assertNotNull(hit);
        assertEquals(HitType.EDGE, hit.hitType());
        assertEquals(4, hit.distanceSq(), DELTA);
    }
}
