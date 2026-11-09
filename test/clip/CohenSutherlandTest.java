package clip;

import model.ClipWindow;
import model.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertEquals;

//clip must always finish: a broken clipping step can loop forever, which has to fail the test instead of hanging the run
//the separate thread is needed because a busy loop does not react to the interruption of the test thread
@Timeout(value = 1, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
class CohenSutherlandTest {

    //y grows downwards, so TOP is y < 10 and BOTTOM is y > 30
    private static final ClipWindow WINDOW = new ClipWindow(p(10, 10), p(30, 30));

    // trivial accept - nothing is clipped

    @Test
    void clip_lineInside_staysWhole() {
        assertSegment(15, 15, 25, 20, CohenSutherland.clip(p(15, 15), p(25, 20), WINDOW));
    }

    @Test
    void clip_lineAlongTopEdge_staysWhole() {
        //points on the top edge are inside, the same as on every other edge
        assertSegment(10, 10, 30, 10, CohenSutherland.clip(p(10, 10), p(30, 10), WINDOW));
    }

    @Test
    void clip_lineAlongBottomEdge_staysWhole() {
        assertSegment(10, 30, 30, 30, CohenSutherland.clip(p(10, 30), p(30, 30), WINDOW));
    }

    @Test
    void clip_lineAlongLeftEdge_staysWhole() {
        assertSegment(10, 10, 10, 30, CohenSutherland.clip(p(10, 10), p(10, 30), WINDOW));
    }

    @Test
    void clip_lineAlongRightEdge_staysWhole() {
        assertSegment(30, 10, 30, 30, CohenSutherland.clip(p(30, 10), p(30, 30), WINDOW));
    }

    @Test
    void clip_zeroLengthLineInside_staysWhole() {
        assertSegment(20, 20, 20, 20, CohenSutherland.clip(p(20, 20), p(20, 20), WINDOW));
    }

    // trivial reject - both points are outside on the same side

    @Test
    void clip_lineLeftOfWindow_isRejected() {
        //the line spans over TOP and BOTTOM, but both points share the LEFT bit
        assertRejected(CohenSutherland.clip(p(0, 5), p(5, 35), WINDOW));
    }

    @Test
    void clip_zeroLengthLineOutside_isRejected() {
        assertRejected(CohenSutherland.clip(p(5, 5), p(5, 5), WINDOW));
    }

    // reject after clipping - the codes share no bit at first

    @Test
    void clip_lineMissingCorner_isRejected() {
        //LEFT and TOP at first, at x = 10 the line is at y = 5, so the point gains the TOP bit
        assertRejected(CohenSutherland.clip(p(0, 15), p(15, 0), WINDOW));
    }

    @Test
    void clip_lineTouchingCorner_collapsesToCorner() {
        //the line has exactly one common point with the window
        assertSegment(10, 10, 10, 10, CohenSutherland.clip(p(0, 20), p(20, 0), WINDOW));
    }

    // one point outside

    @Test
    void clip_pointLeftOfWindow_isMovedToLeftEdge() {
        assertSegment(10, 20, 20, 20, CohenSutherland.clip(p(0, 20), p(20, 20), WINDOW));
    }

    @Test
    void clip_pointRightOfWindow_isMovedToRightEdge() {
        assertSegment(30, 20, 20, 20, CohenSutherland.clip(p(40, 20), p(20, 20), WINDOW));
    }

    @Test
    void clip_pointAboveWindow_isMovedToTopEdge() {
        assertSegment(20, 10, 20, 20, CohenSutherland.clip(p(20, 0), p(20, 20), WINDOW));
    }

    @Test
    void clip_pointBelowWindow_isMovedToBottomEdge() {
        assertSegment(20, 30, 20, 20, CohenSutherland.clip(p(20, 40), p(20, 20), WINDOW));
    }

    @Test
    void clip_secondPointOutside_keepsPointOrder() {
        assertSegment(20, 20, 30, 20, CohenSutherland.clip(p(20, 20), p(40, 20), WINDOW));
    }

    // both points outside

    @Test
    void clip_horizontalLineThroughWindow_isClippedOnBothSides() {
        assertSegment(10, 20, 30, 20, CohenSutherland.clip(p(0, 20), p(40, 20), WINDOW));
    }

    @Test
    void clip_diagonalThroughCorners_isClippedToCorners() {
        assertSegment(10, 10, 30, 30, CohenSutherland.clip(p(0, 0), p(40, 40), WINDOW));
    }

    @Test
    void clip_pointWithTwoBits_isClippedInTwoSteps() {
        //y = x / 2: A has code 1001, at x = 10 it becomes (10, 5) with code 0001 and at y = 10 it ends up at (20, 10)
        assertSegment(20, 10, 30, 15, CohenSutherland.clip(p(0, 0), p(40, 20), WINDOW));
    }

    @Test
    void clip_offGridIntersection_isRoundedNotTruncated() {
        //y = 10 + 8x / 30: at x = 10 the line is at y = 12.67, which is closer to 13 than to 12
        assertSegment(10, 13, 30, 18, CohenSutherland.clip(p(0, 10), p(30, 18), WINDOW));
    }

    // clip window shape

    @Test
    void clip_windowWithSwappedCorners_givesSameResult() {
        ClipWindow window = new ClipWindow(p(30, 30), p(10, 10));

        assertSegment(10, 20, 30, 20, CohenSutherland.clip(p(0, 20), p(40, 20), window));
    }

    @Test
    void clip_windowCreatedFromOtherDiagonal_givesSameResult() {
        ClipWindow window = new ClipWindow(p(30, 10), p(10, 30));

        assertSegment(10, 20, 30, 20, CohenSutherland.clip(p(0, 20), p(40, 20), window));
    }

    @Test
    void clip_windowWithZeroHeight_clipsToItsSegment() {
        //the left and right edges have zero length, they must still work as lines
        ClipWindow window = new ClipWindow(p(10, 20), p(30, 20));

        assertSegment(10, 20, 30, 20, CohenSutherland.clip(p(0, 20), p(40, 20), window));
    }

    private static Point2D p(int x, int y) {
        return new Point2D(x, y);
    }

    private static void assertSegment(int expectedAx, int expectedAy, int expectedBx, int expectedBy, Point2D[] actual) {
        assertEquals(2, actual.length, "number of points");
        assertEquals(expectedAx, actual[0].getX(), "a.x");
        assertEquals(expectedAy, actual[0].getY(), "a.y");
        assertEquals(expectedBx, actual[1].getX(), "b.x");
        assertEquals(expectedBy, actual[1].getY(), "b.y");
    }

    private static void assertRejected(Point2D[] actual) {
        assertEquals(0, actual.length, "number of points");
    }
}
