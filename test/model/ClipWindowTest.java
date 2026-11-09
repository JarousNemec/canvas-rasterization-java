package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import select.Hit;
import select.HitType;
import utils.GeometryUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClipWindowTest {

    private static final int TOLERANCE = 5;

    //window (10, 10) - (30, 30)
    private ClipWindow window;

    @BeforeEach
    void setUp() {
        window = new ClipWindow(p(10, 10), p(30, 30));
    }

    // getVertices - its indices are used by hitTest and setNewPointPosition

    @Test
    void getVertices_startsTopLeftAndIsCCWOnScreen() {
        List<Point2D> vertices = window.getVertices();

        assertCorners(vertices, 10, 10, 10, 30, 30, 30, 30, 10);
        assertTrue(GeometryUtil.signedArea2(vertices) < 0);
    }

    @Test
    void getVertices_cornersGivenByOtherDiagonal_sameOrder() {
        assertCorners(new ClipWindow(p(30, 10), p(10, 30)).getVertices(), 10, 10, 10, 30, 30, 30, 30, 10);
    }

    // setNewPointPosition

    @Test
    void setNewPointPosition_movesCornerAndKeepsOppositeOne() {
        //1 = bottom-left: changes xMin and yMax, top-right corner stays
        window.setNewPointPosition(1, p(5, 40));

        assertBounds(5, 10, 30, 40);
    }

    @Test
    void setNewPointPosition_pastOppositeCorner_isClamped() {
        //the window can't turn inside out, it stays at least 1 px big
        window.setNewPointPosition(0, p(50, 50));

        assertBounds(29, 29, 30, 30);
    }

    @Test
    void setNewPointPosition_afterClamp_sameIndexIsStillSameCorner() {
        //ClipTool keeps the index from the Hit for the whole drag
        window.setNewPointPosition(0, p(50, 50));
        window.setNewPointPosition(0, p(0, 0));

        assertBounds(0, 0, 30, 30);
    }

    @Test
    void setNewPointPosition_invalidIndexOrNull_doesNothing() {
        window.setNewPointPosition(4, p(0, 0));
        window.setNewPointPosition(-1, p(0, 0));
        window.setNewPointPosition(0, null);

        assertBounds(10, 10, 30, 30);
    }

    // hitTest

    @Test
    void hitTest_nearCorner_indexMovesThatCorner() {
        Hit hit = window.hitTest(31, 32, TOLERANCE);

        assertNotNull(hit);
        assertSame(window, hit.target());
        assertEquals(HitType.VERTEX, hit.hitType());
        assertEquals(2, hit.index());

        window.setNewPointPosition(hit.index(), p(40, 45));
        assertBounds(10, 10, 40, 45);
    }

    @Test
    void hitTest_insideWindow_isNull() {
        //only the outline is selectable
        assertNull(window.hitTest(20, 20, TOLERANCE));
    }

    private void assertBounds(int xMin, int yMin, int xMax, int yMax) {
        assertEquals(xMin, window.getXMin(), "xMin");
        assertEquals(yMin, window.getYMin(), "yMin");
        assertEquals(xMax, window.getXMax(), "xMax");
        assertEquals(yMax, window.getYMax(), "yMax");
    }

    private static void assertCorners(List<Point2D> actual, int... expected) {
        assertEquals(expected.length / 2, actual.size(), "size");
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected[2 * i], actual.get(i).getX(), "x of " + i);
            assertEquals(expected[2 * i + 1], actual.get(i).getY(), "y of " + i);
        }
    }

    private static Point2D p(int x, int y) {
        return new Point2D(x, y);
    }
}
