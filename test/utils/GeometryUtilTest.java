package utils;

import model.Point2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GeometryUtilTest {

    //allowed difference when comparing doubles - floating point math is not exact,
    //so assertEquals(expected, actual, DELTA) passes if |expected - actual| <= DELTA
    private static final double DELTA = 1e-9;

    // squaredPointsDistance

    @Test
    void squaredPointsDistance_isZero() {
        assertEquals(0, GeometryUtil.squaredPointsDistance(5, 5, 5, 5), DELTA);
    }

    @Test
    void squaredPointsDistance_rightTriangle3_4_5() {
        //3² + 4² = 25
        assertEquals(25, GeometryUtil.squaredPointsDistance(0, 0, 3, 4), DELTA);
    }

    @Test
    void squaredPointsDistance_isSymmetric() {
        assertEquals(GeometryUtil.squaredPointsDistance(1, 2, 7, 9), GeometryUtil.squaredPointsDistance(7, 9, 1, 2), DELTA);
    }

    // distanceSqToSegment - horizontal segment (0, 0) -> (10, 0)

    @Test
    void distanceSqToSegment_pointAboveSegment_usesPerpendicular() {
        //closest point is (4, 0), distance is 3
        assertEquals(9, GeometryUtil.distanceSqToSegment(4, 3, 0, 0, 10, 0), DELTA);
    }

    @Test
    void distanceSqToSegment_pointOnSegment_isZero() {
        assertEquals(0, GeometryUtil.distanceSqToSegment(6, 0, 0, 0, 10, 0), DELTA);
    }

    @Test
    void distanceSqToSegment_pointBeforeStart_closestIsStart() {
        //t < 0 -> clamped to start (0, 0): 4² + 3² = 25
        assertEquals(25, GeometryUtil.distanceSqToSegment(-4, 3, 0, 0, 10, 0), DELTA);
    }

    @Test
    void distanceSqToSegment_pointPastEnd_closestIsEnd() {
        //t > 1 -> clamped to end (10, 0): 4² + 3² = 25
        assertEquals(25, GeometryUtil.distanceSqToSegment(14, 3, 0, 0, 10, 0), DELTA);
    }

    @Test
    void distanceSqToSegment_pointOnLineButOutsideSegment_isNotZero() {
        //lies on the infinite line, but the segment ends at (10, 0)
        assertEquals(25, GeometryUtil.distanceSqToSegment(15, 0, 0, 0, 10, 0), DELTA);
    }

    // distanceSqToSegment - other orientations

    @Test
    void distanceSqToSegment_verticalSegment() {
        //segment (0, 0) -> (0, 10), closest point is (0, 5)
        assertEquals(9, GeometryUtil.distanceSqToSegment(3, 5, 0, 0, 0, 10), DELTA);
    }

    @Test
    void distanceSqToSegment_diagonalSegment() {
        //segment (0, 0) -> (10, 10), closest point to (0, 10) is (5, 5): 5² + 5² = 50
        assertEquals(50, GeometryUtil.distanceSqToSegment(0, 10, 0, 0, 10, 10), DELTA);
    }

    @Test
    void distanceSqToSegment_swappedEndpoints_sameResult() {
        double forward = GeometryUtil.distanceSqToSegment(4, 3, 0, 0, 10, 0);
        double backward = GeometryUtil.distanceSqToSegment(4, 3, 10, 0, 0, 0);
        assertEquals(forward, backward, DELTA);
    }

    @Test
    void distanceSqToSegment_zeroLengthSegment_actsAsPoint() {
        //start == end, must not divide by zero
        assertEquals(25, GeometryUtil.distanceSqToSegment(3, 4, 0, 0, 0, 0), DELTA);
    }

    @Test
    void distanceSqToSegment_closestPointOffGrid_isExact() {
        //segment (0, 0) -> (2, 1), closest point to (0, 1) is (0.4, 0.2) - not a whole pixel
        //exact: 0.4² + 0.8² = 0.8; computing from the rounded point (0, 0) would give 1
        assertEquals(0.8, GeometryUtil.distanceSqToSegment(0, 1, 0, 0, 2, 1), DELTA);
    }

    // closestPointOnSegmentToPoint - horizontal segment (0, 0) -> (10, 0)

    @Test
    void closestPointOnSegmentToPoint_pointAboveSegment_isPerpendicularFoot() {
        assertPoint(4, 0, GeometryUtil.closestPointOnSegmentToPoint(4, 3, 0, 0, 10, 0));
    }

    @Test
    void closestPointOnSegmentToPoint_pointOnSegment_isPointItself() {
        assertPoint(6, 0, GeometryUtil.closestPointOnSegmentToPoint(6, 0, 0, 0, 10, 0));
    }

    @Test
    void closestPointOnSegmentToPoint_pointBeforeStart_isStart() {
        //t < 0 -> clamped to start
        assertPoint(0, 0, GeometryUtil.closestPointOnSegmentToPoint(-4, 3, 0, 0, 10, 0));
    }

    @Test
    void closestPointOnSegmentToPoint_pointPastEnd_isEnd() {
        //t > 1 -> clamped to end
        assertPoint(10, 0, GeometryUtil.closestPointOnSegmentToPoint(14, 3, 0, 0, 10, 0));
    }

    // closestPointOnSegmentToPoint - other orientations

    @Test
    void closestPointOnSegmentToPoint_verticalSegment() {
        assertPoint(0, 5, GeometryUtil.closestPointOnSegmentToPoint(3, 5, 0, 0, 0, 10));
    }

    @Test
    void closestPointOnSegmentToPoint_diagonalSegment() {
        assertPoint(5, 5, GeometryUtil.closestPointOnSegmentToPoint(0, 10, 0, 0, 10, 10));
    }

    @Test
    void closestPointOnSegmentToPoint_swappedEndpoints_samePoint() {
        assertPoint(4, 0, GeometryUtil.closestPointOnSegmentToPoint(4, 3, 10, 0, 0, 0));
    }

    @Test
    void closestPointOnSegmentToPoint_offGrid_isRoundedToPixel() {
        //segment (0, 0) -> (10, 5), exact closest point to (3, 5) is (4.4, 2.2) -> rounded to (4, 2)
        assertPoint(4, 2, GeometryUtil.closestPointOnSegmentToPoint(3, 5, 0, 0, 10, 5));
    }

    @Test
    void closestPointOnSegmentToPoint_zeroLengthSegment_isThatPoint() {
        //start == end, must not divide by zero and must not return null
        assertPoint(2, 2, GeometryUtil.closestPointOnSegmentToPoint(7, 9, 2, 2, 2, 2));
    }

    // dotProduct

    @Test
    void dotProduct_perpendicularVectors_isZero() {
        assertEquals(0, GeometryUtil.dotProduct(3, 0, 0, 5), DELTA);
    }

    @Test
    void dotProduct_generalVectors() {
        //1*3 + 2*4 = 11
        assertEquals(11, GeometryUtil.dotProduct(1, 2, 3, 4), DELTA);
    }

    @Test
    void dotProduct_oppositeVectors_isNegative() {
        assertEquals(-4, GeometryUtil.dotProduct(2, 0, -2, 0), DELTA);
    }

    // vectorLengthSq, vectorLength

    @Test
    void vectorLengthSq_rightTriangle3_4_5() {
        assertEquals(25, GeometryUtil.vectorLengthSq(3, 4), DELTA);
    }

    @Test
    void vectorLengthSq_negativeComponents_isPositive() {
        assertEquals(25, GeometryUtil.vectorLengthSq(-3, -4), DELTA);
    }

    @Test
    void vectorLength_rightTriangle3_4_5() {
        assertEquals(5, GeometryUtil.vectorLength(3, 4), DELTA);
    }

    @Test
    void vectorLength_zeroVector_isZero() {
        assertEquals(0, GeometryUtil.vectorLength(0, 0), DELTA);
    }

    private static void assertPoint(int expectedX, int expectedY, Point2D actual) {
        assertNotNull(actual);
        assertEquals(expectedX, actual.getX(), "x");
        assertEquals(expectedY, actual.getY(), "y");
    }
}
