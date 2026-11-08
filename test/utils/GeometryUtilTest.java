package utils;

import model.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeometryUtilTest {

    //allowed difference when comparing doubles - floating point math is not exact,
    //so assertEquals(expected, actual, DELTA) passes if |expected - actual| <= DELTA
    private static final double DELTA = 1e-9;

    // distanceSquared

    @Test
    void distanceSquared_isZero() {
        assertEquals(0, GeometryUtil.distanceSquared(5, 5, 5, 5), DELTA);
    }

    @Test
    void distanceSquared_rightTriangle3_4_5() {
        //3² + 4² = 25
        assertEquals(25, GeometryUtil.distanceSquared(0, 0, 3, 4), DELTA);
    }

    @Test
    void distanceSquared_isSymmetric() {
        assertEquals(GeometryUtil.distanceSquared(1, 2, 7, 9), GeometryUtil.distanceSquared(7, 9, 1, 2), DELTA);
    }

    // distanceSquaredToSegment - horizontal segment (0, 0) -> (10, 0)

    @Test
    void distanceSquaredToSegment_pointAboveSegment_usesPerpendicular() {
        //closest point is (4, 0), distance is 3
        assertEquals(9, GeometryUtil.distanceSquaredToSegment(4, 3, 0, 0, 10, 0), DELTA);
    }

    @Test
    void distanceSquaredToSegment_pointOnSegment_isZero() {
        assertEquals(0, GeometryUtil.distanceSquaredToSegment(6, 0, 0, 0, 10, 0), DELTA);
    }

    @Test
    void distanceSquaredToSegment_pointBeforeStart_closestIsStart() {
        //t < 0 -> clamped to start (0, 0): 4² + 3² = 25
        assertEquals(25, GeometryUtil.distanceSquaredToSegment(-4, 3, 0, 0, 10, 0), DELTA);
    }

    @Test
    void distanceSquaredToSegment_pointPastEnd_closestIsEnd() {
        //t > 1 -> clamped to end (10, 0): 4² + 3² = 25
        assertEquals(25, GeometryUtil.distanceSquaredToSegment(14, 3, 0, 0, 10, 0), DELTA);
    }

    @Test
    void distanceSquaredToSegment_pointOnLineButOutsideSegment_isNotZero() {
        //lies on the infinite line, but the segment ends at (10, 0)
        assertEquals(25, GeometryUtil.distanceSquaredToSegment(15, 0, 0, 0, 10, 0), DELTA);
    }

    // distanceSquaredToSegment - other orientations

    @Test
    void distanceSquaredToSegment_verticalSegment() {
        //segment (0, 0) -> (0, 10), closest point is (0, 5)
        assertEquals(9, GeometryUtil.distanceSquaredToSegment(3, 5, 0, 0, 0, 10), DELTA);
    }

    @Test
    void distanceSquaredToSegment_diagonalSegment() {
        //segment (0, 0) -> (10, 10), closest point to (0, 10) is (5, 5): 5² + 5² = 50
        assertEquals(50, GeometryUtil.distanceSquaredToSegment(0, 10, 0, 0, 10, 10), DELTA);
    }

    @Test
    void distanceSquaredToSegment_swappedEndpoints_sameResult() {
        double forward = GeometryUtil.distanceSquaredToSegment(4, 3, 0, 0, 10, 0);
        double backward = GeometryUtil.distanceSquaredToSegment(4, 3, 10, 0, 0, 0);
        assertEquals(forward, backward, DELTA);
    }

    @Test
    void distanceSquaredToSegment_zeroLengthSegment_actsAsPoint() {
        //start == end, must not divide by zero
        assertEquals(25, GeometryUtil.distanceSquaredToSegment(3, 4, 0, 0, 0, 0), DELTA);
    }

    @Test
    void distanceSquaredToSegment_closestPointOffGrid_isExact() {
        //segment (0, 0) -> (2, 1), closest point to (0, 1) is (0.4, 0.2) - not a whole pixel
        //exact: 0.4² + 0.8² = 0.8; computing from the rounded point (0, 0) would give 1
        assertEquals(0.8, GeometryUtil.distanceSquaredToSegment(0, 1, 0, 0, 2, 1), DELTA);
    }

    // closestPointOnSegment - horizontal segment (0, 0) -> (10, 0)

    @Test
    void closestPointOnSegment_pointAboveSegment_isPerpendicularFoot() {
        assertPoint(4, 0, GeometryUtil.closestPointOnSegment(4, 3, 0, 0, 10, 0));
    }

    @Test
    void closestPointOnSegment_pointOnSegment_isPointItself() {
        assertPoint(6, 0, GeometryUtil.closestPointOnSegment(6, 0, 0, 0, 10, 0));
    }

    @Test
    void closestPointOnSegment_pointBeforeStart_isStart() {
        //t < 0 -> clamped to start
        assertPoint(0, 0, GeometryUtil.closestPointOnSegment(-4, 3, 0, 0, 10, 0));
    }

    @Test
    void closestPointOnSegment_pointPastEnd_isEnd() {
        //t > 1 -> clamped to end
        assertPoint(10, 0, GeometryUtil.closestPointOnSegment(14, 3, 0, 0, 10, 0));
    }

    // closestPointOnSegment - other orientations

    @Test
    void closestPointOnSegment_verticalSegment() {
        assertPoint(0, 5, GeometryUtil.closestPointOnSegment(3, 5, 0, 0, 0, 10));
    }

    @Test
    void closestPointOnSegment_diagonalSegment() {
        assertPoint(5, 5, GeometryUtil.closestPointOnSegment(0, 10, 0, 0, 10, 10));
    }

    @Test
    void closestPointOnSegment_swappedEndpoints_samePoint() {
        assertPoint(4, 0, GeometryUtil.closestPointOnSegment(4, 3, 10, 0, 0, 0));
    }

    @Test
    void closestPointOnSegment_offGrid_isRoundedToPixel() {
        //segment (0, 0) -> (10, 5), exact closest point to (9, 1) is (7.6, 3.8) -> rounded to (8, 4), truncation would give (7, 3)
        assertPoint(8, 4, GeometryUtil.closestPointOnSegment(9, 1, 0, 0, 10, 5));
    }

    @Test
    void closestPointOnSegment_zeroLengthSegment_isThatPoint() {
        //start == end, must not divide by zero and must not return null
        assertPoint(2, 2, GeometryUtil.closestPointOnSegment(7, 9, 2, 2, 2, 2));
    }

    // dot

    @Test
    void dot_perpendicularVectors_isZero() {
        assertEquals(0, GeometryUtil.dot(3, 0, 0, 5), DELTA);
    }

    @Test
    void dot_generalVectors() {
        //1*3 + 2*4 = 11
        assertEquals(11, GeometryUtil.dot(1, 2, 3, 4), DELTA);
    }

    @Test
    void dot_oppositeVectors_isNegative() {
        assertEquals(-4, GeometryUtil.dot(2, 0, -2, 0), DELTA);
    }

    // lengthSquared, length

    @Test
    void lengthSquared_rightTriangle3_4_5() {
        assertEquals(25, GeometryUtil.lengthSquared(3, 4), DELTA);
    }

    @Test
    void lengthSquared_negativeComponents_isPositive() {
        assertEquals(25, GeometryUtil.lengthSquared(-3, -4), DELTA);
    }

    @Test
    void length_rightTriangle3_4_5() {
        assertEquals(5, GeometryUtil.length(3, 4), DELTA);
    }

    @Test
    void length_zeroVector_isZero() {
        assertEquals(0, GeometryUtil.length(0, 0), DELTA);
    }

    // lineIntersection

    @Test
    void lineIntersection_generalLines() {
        //AB (-2, 5) -> (6, 1) crosses the vertical line x = 0 at t = 0.25 -> (0, 4)
        assertPoint(0, 4, GeometryUtil.lineIntersection(p(-2, 5), p(6, 1), p(0, 0), p(0, 10)));
    }

    @Test
    void lineIntersection_horizontalAndVertical() {
        assertPoint(3, 5, GeometryUtil.lineIntersection(p(0, 5), p(10, 5), p(3, 0), p(3, 10)));
    }

    @Test
    void lineIntersection_twoDiagonals() {
        assertPoint(5, 5, GeometryUtil.lineIntersection(p(0, 0), p(10, 10), p(0, 10), p(10, 0)));
    }

    @Test
    void lineIntersection_swappedEdgeDirection_samePoint() {
        //direction of CD must not matter
        assertPoint(0, 4, GeometryUtil.lineIntersection(p(-2, 5), p(6, 1), p(0, 10), p(0, 0)));
    }

    @Test
    void lineIntersection_outsideSegment_usesInfiniteLines() {
        //AB ends at x = 2, but the infinite line still crosses x = 10 (t = 5)
        assertPoint(10, 0, GeometryUtil.lineIntersection(p(0, 0), p(2, 0), p(10, -5), p(10, 5)));
    }

    @Test
    void lineIntersection_offGrid_isRoundedNotTruncated() {
        //exact intersection is (6, 1.8) -> rounded to (6, 2), truncation would give (6, 1)
        assertPoint(6, 2, GeometryUtil.lineIntersection(p(0, 0), p(10, 3), p(6, 0), p(6, 10)));
    }

    @Test
    void lineIntersection_parallelLines_isNull() {
        assertNull(GeometryUtil.lineIntersection(p(0, 0), p(10, 0), p(0, 5), p(10, 5)));
    }

    @Test
    void lineIntersection_collinearLines_isNull() {
        //same line - infinitely many common points, no single intersection
        assertNull(GeometryUtil.lineIntersection(p(0, 0), p(10, 0), p(20, 0), p(30, 0)));
    }

    @Test
    void lineIntersection_zeroLengthEdge_isNull() {
        //C == D does not define a line, must not divide by zero
        assertNull(GeometryUtil.lineIntersection(p(0, 0), p(10, 0), p(5, 5), p(5, 5)));
    }

    // Vec2 versions - no rounding until the very end

    @Test
    void lineIntersection_vec2_isNotRounded() {
        //AB from (0, 0) to (3, 1) crosses x = 1 at y = 1/3
        Vec2 actual = GeometryUtil.lineIntersection(v(0, 0), v(3, 1), v(1, 0), v(1, 10));
        assertEquals(1, actual.x(), DELTA);
        assertEquals(1.0 / 3, actual.y(), DELTA);
    }

    @Test
    void lineIntersection_vec2_parallel_isNull() {
        assertNull(GeometryUtil.lineIntersection(v(0, 0), v(10, 0), v(0, 5), v(10, 5)));
    }

    @Test
    void lineIntersection_point2D_isRoundedVec2Result() {
        //same lines as above, 1/3 is rounded to 0
        assertPoint(1, 0, GeometryUtil.lineIntersection(p(0, 0), p(3, 1), p(1, 0), p(1, 10)));
    }

    @Test
    void pointSide_vec2_sameSignAsIntVersion() {
        //A -> B goes right, P above on screen = left = positive
        assertTrue(GeometryUtil.pointSide(v(0, 0), v(10, 0), v(5, -5)) > 0);
        assertTrue(GeometryUtil.pointSide(v(0, 0), v(10, 0), v(5, 5)) < 0);
        assertEquals(GeometryUtil.pointSide(p(0, 0), p(10, 0), p(5, -5)), GeometryUtil.pointSide(v(0, 0), v(10, 0), v(5, -5)), DELTA);
    }

    @Test
    void isPointInsideCCWEdge_vec2_tinyNegativeCountsAsInside() {
        //point a tiny bit right of the edge because of floating point error is still inside
        assertTrue(GeometryUtil.isPointInsideCCWEdge(v(0, 0), v(10, 0), v(5, 1e-12)));
        assertFalse(GeometryUtil.isPointInsideCCWEdge(v(0, 0), v(10, 0), v(5, 0.01)));
    }

    @Test
    void vec2_roundTrip_roundsToNearestPixel() {
        assertPoint(3, 5, new Vec2(2.6, 4.5).toPoint2D());
        List<Point2D> points = Vec2.toPoint2D(Vec2.of(List.of(p(1, 2), p(3, 4))));
        assertPolygon(List.of(p(1, 2), p(3, 4)), points);
    }

    // signedArea2 - on the canvas Y points down, so counter-clockwise on screen gives a negative value

    @Test
    void signedArea2_squareCCW_isNegative() {
        //(0,0) -> down -> right -> up, counter-clockwise as seen on screen
        assertEquals(-200, GeometryUtil.signedArea2(squareCCW()));
    }

    @Test
    void signedArea2_squareCW_isPositive() {
        assertEquals(200, GeometryUtil.signedArea2(squareCW()));
    }

    @Test
    void signedArea2_rightTriangle_isDoubledArea() {
        //legs 10 and 10 -> area 50 -> doubled 100
        assertEquals(100, Math.abs(GeometryUtil.signedArea2(List.of(p(0, 0), p(10, 0), p(0, 10)))));
    }

    @Test
    void signedArea2_concaveLShape_isDoubledArea() {
        //10x10 square without its 5x5 corner -> area 75 -> doubled 150, clockwise on screen
        assertEquals(150, GeometryUtil.signedArea2(lShapeCW()));
    }

    @Test
    void signedArea2_differentStartVertex_sameResult() {
        assertEquals(-200, GeometryUtil.signedArea2(List.of(p(10, 10), p(10, 0), p(0, 0), p(0, 10))));
    }

    @Test
    void signedArea2_squareAwayFromOrigin_sameResult() {
        //the triangles from the origin cancel out, so position of the polygon does not matter
        assertEquals(-200, GeometryUtil.signedArea2(List.of(p(300, 200), p(300, 210), p(310, 210), p(310, 200))));
    }

    @Test
    void signedArea2_allPointsCollinear_isZero() {
        assertEquals(0, GeometryUtil.signedArea2(List.of(p(0, 0), p(5, 5), p(10, 10))));
    }

    // orientatePolygonCCW

    @Test
    void orientatePolygonCCW_alreadyCCW_keepsOrder() {
        assertPolygon(squareCCW(), GeometryUtil.orientatePolygonCCW(squareCCW()));
    }

    @Test
    void orientatePolygonCCW_clockwise_reversesOrder() {
        List<Point2D> expected = List.of(p(0, 10), p(10, 10), p(10, 0), p(0, 0));
        assertPolygon(expected, GeometryUtil.orientatePolygonCCW(squareCW()));
    }

    @Test
    void orientatePolygonCCW_concaveClockwise_becomesCCW() {
        assertTrue(GeometryUtil.signedArea2(GeometryUtil.orientatePolygonCCW(lShapeCW())) < 0);
    }

    @Test
    void orientatePolygonCCW_doesNotModifyInput() {
        List<Point2D> input = new ArrayList<>(squareCW());
        List<Point2D> result = GeometryUtil.orientatePolygonCCW(input);
        assertNotSame(input, result);
        assertPolygon(squareCW(), input);
    }

    @Test
    void orientatePolygonCCW_allPointsCollinear_keepsOrder() {
        List<Point2D> line = List.of(p(0, 0), p(5, 5), p(10, 10));
        assertPolygon(line, GeometryUtil.orientatePolygonCCW(line));
    }

    @Test
    void orientatePolygonCCW_thenInsideTest_worksForClockwiseInput() {
        //orientation and isPointInsideCCWEdge must use the same convention
        List<Point2D> clipper = GeometryUtil.orientatePolygonCCW(squareCW());
        assertTrue(GeometryUtil.isPointInsideConvexCCWPolygon(clipper, p(5, 5)));
        assertFalse(GeometryUtil.isPointInsideConvexCCWPolygon(clipper, p(15, 5)));
    }

    private static List<Point2D> squareCCW() {
        return List.of(p(0, 0), p(0, 10), p(10, 10), p(10, 0));
    }

    private static List<Point2D> squareCW() {
        return List.of(p(0, 0), p(10, 0), p(10, 10), p(0, 10));
    }

    private static List<Point2D> lShapeCW() {
        return List.of(p(0, 0), p(10, 0), p(10, 5), p(5, 5), p(5, 10), p(0, 10));
    }

    private static void assertPolygon(List<Point2D> expected, List<Point2D> actual) {
        assertEquals(expected.size(), actual.size(), "size");
        for (int i = 0; i < expected.size(); i++)
            assertPoint(expected.get(i).getX(), expected.get(i).getY(), actual.get(i));
    }

    private static Vec2 v(double x, double y) {
        return new Vec2(x, y);
    }

    private static Point2D p(int x, int y) {
        return new Point2D(x, y);
    }

    private static void assertPoint(int expectedX, int expectedY, Point2D actual) {
        assertNotNull(actual);
        assertEquals(expectedX, actual.getX(), "x");
        assertEquals(expectedY, actual.getY(), "y");
    }
}
