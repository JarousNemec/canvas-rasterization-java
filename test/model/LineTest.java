package model;

import org.junit.jupiter.api.Test;
import select.Hit;
import select.HitType;

import static org.junit.jupiter.api.Assertions.*;

class LineTest {

    private static final int TOLERANCE = 5;
    //allowed difference when comparing doubles - floating point math is not exact,
    //so assertEquals(expected, actual, DELTA) passes if |expected - actual| <= DELTA
    private static final double DELTA = 1e-9;

    //horizontal line (0, 0) -> (100, 0)
    private final Line line = new Line(new Point2D(0, 0), new Point2D(100, 0));

    // vertices

    @Test
    void hitTest_nearStart_returnsVertexZero() {
        Hit hit = line.hitTest(2, 1, TOLERANCE);

        assertNotNull(hit);
        assertSame(line, hit.target());
        assertEquals(HitType.VERTEX, hit.hitType());
        assertEquals(0, hit.index());
        assertEquals(5, hit.distanceSq(), DELTA); //2² + 1²
    }

    @Test
    void hitTest_nearEnd_returnsVertexOne() {
        Hit hit = line.hitTest(99, 3, TOLERANCE);

        assertNotNull(hit);
        assertEquals(HitType.VERTEX, hit.hitType());
        assertEquals(1, hit.index());
        assertEquals(10, hit.distanceSq(), DELTA); //1² + 3²
    }

    @Test
    void hitTest_onEdgeNearVertex_vertexWins() {
        //edge distance is 0, but the vertex is within tolerance, so it must win
        Hit hit = line.hitTest(4, 0, TOLERANCE);

        assertNotNull(hit);
        assertEquals(HitType.VERTEX, hit.hitType());
        assertEquals(0, hit.index());
        assertEquals(16, hit.distanceSq(), DELTA);
    }

    @Test
    void hitTest_bothVerticesInTolerance_closerOneWins() {
        Line shortLine = new Line(new Point2D(0, 0), new Point2D(6, 0));

        //distance to start is 4, distance to end is 2
        Hit hit = shortLine.hitTest(4, 0, TOLERANCE);

        assertNotNull(hit);
        assertEquals(HitType.VERTEX, hit.hitType());
        assertEquals(1, hit.index());
        assertEquals(4, hit.distanceSq(), DELTA);
    }

    @Test
    void hitTest_exactlyOnToleranceBorder_isHit() {
        Hit hit = line.hitTest(105, 0, TOLERANCE);

        assertNotNull(hit);
        assertEquals(HitType.VERTEX, hit.hitType());
        assertEquals(1, hit.index());
        assertEquals(25, hit.distanceSq(), DELTA);
    }

    // edge

    @Test
    void hitTest_nearMiddle_returnsEdgeWithRealDistance() {
        Hit hit = line.hitTest(50, 3, TOLERANCE);

        assertNotNull(hit);
        assertSame(line, hit.target());
        assertEquals(HitType.EDGE, hit.hitType());
        assertEquals(0, hit.index());
        assertEquals(9, hit.distanceSq(), DELTA); //real distance, not toleranceSq
    }

    @Test
    void hitTest_verticalLine_returnsEdge() {
        Line vertical = new Line(new Point2D(10, 0), new Point2D(10, 100));

        Hit hit = vertical.hitTest(12, 50, TOLERANCE);

        assertNotNull(hit);
        assertEquals(HitType.EDGE, hit.hitType());
        assertEquals(4, hit.distanceSq(), DELTA);
    }

    // miss

    @Test
    void hitTest_farFromLine_returnsNull() {
        assertNull(line.hitTest(50, 20, TOLERANCE));
    }

    @Test
    void hitTest_onLineExtensionOutsideTolerance_returnsNull() {
        //lies on the infinite line, but 10 px past the end
        assertNull(line.hitTest(110, 0, TOLERANCE));
    }

    @Test
    void hitTest_justOutsideTolerance_returnsNull() {
        //distance 6 > tolerance 5
        assertNull(line.hitTest(50, 6, TOLERANCE));
    }
}
