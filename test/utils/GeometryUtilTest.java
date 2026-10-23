package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeometryUtilTest {

    //allowed difference when comparing doubles - floating point math is not exact,
    //so assertEquals(expected, actual, DELTA) passes if |expected - actual| <= DELTA
    private static final double DELTA = 1e-9;

    // distanceSqToPoint

    @Test
    void distanceSqToPoint_samePoint_isZero() {
        assertEquals(0, GeometryUtil.distanceSqToPoint(5, 5, 5, 5), DELTA);
    }

    @Test
    void distanceSqToPoint_rightTriangle3_4_5() {
        //3² + 4² = 25
        assertEquals(25, GeometryUtil.distanceSqToPoint(0, 0, 3, 4), DELTA);
    }

    @Test
    void distanceSqToPoint_isSymmetric() {
        assertEquals(GeometryUtil.distanceSqToPoint(1, 2, 7, 9), GeometryUtil.distanceSqToPoint(7, 9, 1, 2), DELTA);
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
}
