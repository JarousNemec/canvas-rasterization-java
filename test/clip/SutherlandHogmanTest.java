package clip;

import model.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class SutherlandHogmanTest {

    //y grows downwards, so this order is clockwise on the screen
    private static final int[] SQUARE = {10, 10, 30, 10, 30, 30, 10, 30};
    //the same square in the opposite direction
    private static final int[] SQUARE_REVERSED = {10, 30, 30, 30, 30, 10, 10, 10};

    // subject and clipper do not cross

    @Test
    void clip_subjectInside_staysWhole() {
        List<Point2D> clipped = SutherlandHogman.clip(polygon(15, 15, 25, 15, 25, 25), polygon(SQUARE));

        assertPolygon(clipped, 15, 15, 25, 15, 25, 25);
    }

    @Test
    void clip_subjectOutside_isEmpty() {
        assertEmpty(SutherlandHogman.clip(polygon(40, 40, 50, 40, 50, 50), polygon(SQUARE)));
    }

    @Test
    void clip_subjectOutsideNearCorner_isEmpty() {
        //the bounding boxes overlap, but the whole subject lies on x + y <= 15 and the corner of the clipper has x + y = 20
        assertEmpty(SutherlandHogman.clip(polygon(0, 0, 15, 0, 0, 15), polygon(SQUARE)));
    }

    @Test
    void clip_subjectAroundClipper_becomesClipper() {
        List<Point2D> clipped = SutherlandHogman.clip(polygon(0, 0, 40, 0, 40, 40, 0, 40), polygon(SQUARE));

        assertPolygon(clipped, 10, 10, 30, 10, 30, 30, 10, 30);
    }

    @Test
    void clip_subjectEqualToClipper_staysWhole() {
        assertPolygon(SutherlandHogman.clip(polygon(SQUARE), polygon(SQUARE)), SQUARE);
    }

    @Test
    void clip_emptySubject_isEmpty() {
        assertEmpty(SutherlandHogman.clip(new ArrayList<>(), polygon(SQUARE)));
    }

    // subject touches the clipper, the common part has no area

    @Test
    void clip_subjectTouchingClipperCorner_hasNoArea() {
        assertArea(0, SutherlandHogman.clip(polygon(0, 0, 10, 10, 0, 10), polygon(SQUARE)));
    }

    @Test
    void clip_subjectSharingClipperEdgeFromOutside_hasNoArea() {
        assertArea(0, SutherlandHogman.clip(polygon(30, 10, 50, 10, 50, 30, 30, 30), polygon(SQUARE)));
    }

    // one side of the clipper cuts the subject

    @Test
    void clip_vertexLeftOfClipper_isCutByLeftEdge() {
        List<Point2D> clipped = SutherlandHogman.clip(polygon(0, 20, 20, 12, 20, 28), polygon(SQUARE));

        assertPolygon(clipped, 10, 16, 20, 12, 20, 28, 10, 24);
    }

    @Test
    void clip_twoVerticesOutside_areReplacedByTwoIntersections() {
        List<Point2D> clipped = SutherlandHogman.clip(polygon(0, 15, 20, 15, 20, 25, 0, 25), polygon(SQUARE));

        assertPolygon(clipped, 10, 15, 20, 15, 20, 25, 10, 25);
    }

    @Test
    void clip_outsideVertexIsFirstInList_closingEdgeIsCut() {
        //the same triangle starting from every vertex, the edge from the last point to the first one counts too
        assertPolygon(SutherlandHogman.clip(polygon(20, 12, 20, 28, 0, 20), polygon(SQUARE)), 10, 16, 20, 12, 20, 28, 10, 24);
        assertPolygon(SutherlandHogman.clip(polygon(20, 28, 0, 20, 20, 12), polygon(SQUARE)), 10, 16, 20, 12, 20, 28, 10, 24);
    }

    // more sides of the clipper cut the subject

    @Test
    void clip_barThroughClipper_isCutOnBothSides() {
        List<Point2D> clipped = SutherlandHogman.clip(polygon(0, 15, 40, 15, 40, 25, 0, 25), polygon(SQUARE));

        assertPolygon(clipped, 10, 15, 30, 15, 30, 25, 10, 25);
    }

    @Test
    void clip_subjectOverCorner_getsClipperCornerAsVertex() {
        List<Point2D> clipped = SutherlandHogman.clip(polygon(20, 20, 40, 20, 40, 40, 20, 40), polygon(SQUARE));

        assertPolygon(clipped, 20, 20, 30, 20, 30, 30, 20, 30);
    }

    @Test
    void clip_triangleCuttingAcrossClipper_keepsOnlyCommonPart() {
        //the hypotenuse x + y = 30 crosses the clipper at (20, 10) and (10, 20)
        List<Point2D> clipped = SutherlandHogman.clip(polygon(0, 0, 30, 0, 0, 30), polygon(SQUARE));

        assertPolygon(clipped, 10, 10, 20, 10, 10, 20);
    }

    @Test
    void clip_offGridIntersection_isRoundedNotTruncated() {
        //y = 10 + 8x / 30: at x = 10 the edge is at y = 12.67, which is closer to 13 than to 12
        List<Point2D> clipped = SutherlandHogman.clip(polygon(0, 10, 30, 18, 30, 10), polygon(SQUARE));

        assertPolygon(clipped, 10, 13, 30, 18, 30, 10, 10, 10);
    }

    @Test
    void clip_keepsDirectionOfSubject() {
        //the same subject as in clip_vertexLeftOfClipper_isCutByLeftEdge in the opposite direction
        List<Point2D> clipped = SutherlandHogman.clip(polygon(20, 28, 20, 12, 0, 20), polygon(SQUARE));

        assertPolygon(clipped, 10, 24, 20, 28, 20, 12, 10, 16);
    }

    // non-convex subject

    @Test
    void clip_concaveSubject_staysConcave() {
        List<Point2D> clipped = SutherlandHogman.clip(polygon(15, 15, 40, 15, 40, 22, 22, 22, 22, 40, 15, 40), polygon(SQUARE));

        assertPolygon(clipped, 15, 15, 30, 15, 30, 22, 22, 22, 22, 30, 15, 30);
    }

    @Test
    void clip_concaveSubjectSplitIntoTwoParts_keepsAreaOfBothParts() {
        //an upside-down U: only two legs 4 wide and 10 high remain inside the clipper
        List<Point2D> clipped = SutherlandHogman.clip(polygon(12, 0, 28, 0, 28, 20, 24, 20, 24, 5, 16, 5, 16, 20, 12, 20), polygon(SQUARE));

        assertArea(80, clipped);
        assertInsideSquare(clipped);
    }

    // self-intersecting subject

    @Test
    void clip_selfIntersectingSubject_crossingInsideClipper_keepsCrossing() {
        //the edges cross at (20, 20) and meet the top edge at x = 24 and x = 16
        List<Point2D> clipped = SutherlandHogman.clip(polygon(12, 0, 28, 0, 12, 40, 28, 40), polygon(SQUARE));

        assertPolygon(clipped, 24, 10, 16, 30, 24, 30, 16, 10);
    }

    @Test
    void clip_selfIntersectingSubject_crossingOutsideClipper_losesCrossing() {
        //the edges cross at (5, 20), every edge reaches the left edge of the clipper
        List<Point2D> clipped = SutherlandHogman.clip(polygon(0, 22, 20, 14, 0, 18, 20, 26), polygon(SQUARE));

        assertPolygon(clipped, 10, 18, 20, 14, 10, 16, 10, 22, 20, 26, 10, 24);
    }

    // clipper orientation and shape

    @Test
    void clip_reversedClipper_givesSameResult() {
        //without orientating the clipper everything would be outside
        List<Point2D> clipped = SutherlandHogman.clip(polygon(20, 20, 40, 20, 40, 40, 20, 40), polygon(SQUARE_REVERSED));

        assertPolygon(clipped, 20, 20, 30, 20, 30, 30, 20, 30);
    }

    @Test
    void clip_clipperStartingFromOtherVertex_givesSameResult() {
        List<Point2D> clipped = SutherlandHogman.clip(polygon(20, 20, 40, 20, 40, 40, 20, 40), polygon(30, 30, 10, 30, 10, 10, 30, 10));

        assertPolygon(clipped, 20, 20, 30, 20, 30, 30, 20, 30);
    }

    @Test
    void clip_triangleClipper_cutsByDiagonalEdge() {
        //the hypotenuse x + y = 40 crosses the subject at (30, 10) and (10, 30)
        List<Point2D> clipped = SutherlandHogman.clip(polygon(10, 10, 40, 10, 40, 40, 10, 40), polygon(0, 0, 40, 0, 0, 40));

        assertPolygon(clipped, 10, 10, 30, 10, 10, 30);
    }

    @Test
    void clip_reversedTriangleClipper_cutsByDiagonalEdge() {
        List<Point2D> clipped = SutherlandHogman.clip(polygon(10, 10, 40, 10, 40, 40, 10, 40), polygon(0, 40, 40, 0, 0, 0));

        assertPolygon(clipped, 10, 10, 30, 10, 10, 30);
    }

    @Test
    void clip_clipperWithCollinearFirstVertices_givesSameResult() {
        //(20, 10) lies on the top edge, so the first three points of the clipper form a straight line
        List<Point2D> clipper = polygon(10, 10, 20, 10, 30, 10, 30, 30, 10, 30);

        assertPolygon(SutherlandHogman.clip(polygon(20, 20, 40, 20, 40, 40, 20, 40), clipper), 20, 20, 30, 20, 30, 30, 20, 30);
        assertEmpty(SutherlandHogman.clip(polygon(40, 40, 50, 40, 50, 50), clipper));
    }

    // input lists

    @Test
    void clip_doesNotChangeInputLists() {
        List<Point2D> subject = polygon(0, 15, 40, 15, 40, 25, 0, 25);
        List<Point2D> clipper = polygon(SQUARE);
        List<Point2D> originalSubject = new ArrayList<>(subject);
        List<Point2D> originalClipper = new ArrayList<>(clipper);

        SutherlandHogman.clip(subject, clipper);

        assertEquals(originalSubject, subject);
        assertEquals(originalClipper, clipper);
    }

    @Test
    void clip_subjectInside_returnsNewList() {
        //Polygon keeps the returned list, editing the clipped polygon must not reach the original one
        List<Point2D> subject = polygon(15, 15, 25, 15, 25, 25);

        List<Point2D> clipped = SutherlandHogman.clip(subject, polygon(SQUARE));

        assertNotNull(clipped);
        assertNotSame(subject, clipped);
    }

    //takes any number of ints as pairs x, y: polygon(x0, y0, x1, y1, ...)
    private static List<Point2D> polygon(int... coordinates) {
        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < coordinates.length; i += 2) {
            points.add(new Point2D(coordinates[i], coordinates[i + 1]));
        }
        return points;
    }

    //the same polygon can start at any vertex and a vertex repeated right after itself does not change its shape
    private static void assertPolygon(List<Point2D> actual, int... expected) {
        assertNotNull(actual);
        List<Point2D> expectedPoints = polygon(expected);
        List<Point2D> actualPoints = withoutRepeatedPoints(actual);

        if (actualPoints.size() == expectedPoints.size()) {
            for (int start = 0; start < actualPoints.size(); start++) {
                if (matchesFrom(start, actualPoints, expectedPoints))
                    return;
            }
        }
        fail("expected " + format(expectedPoints) + " but was " + format(actual));
    }

    private static boolean matchesFrom(int start, List<Point2D> actual, List<Point2D> expected) {
        for (int i = 0; i < expected.size(); i++) {
            if (!samePosition(actual.get((start + i) % actual.size()), expected.get(i)))
                return false;
        }
        return true;
    }

    private static List<Point2D> withoutRepeatedPoints(List<Point2D> points) {
        List<Point2D> result = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Point2D previous = points.get((i + points.size() - 1) % points.size());
            if (!samePosition(points.get(i), previous))
                result.add(points.get(i));
        }
        return result;
    }

    private static boolean samePosition(Point2D a, Point2D b) {
        return a.getX() == b.getX() && a.getY() == b.getY();
    }

    private static void assertEmpty(List<Point2D> actual) {
        assertNotNull(actual);
        assertTrue(actual.isEmpty(), "expected no points but was " + format(actual));
    }

    //area from the shoelace formula
    private static void assertArea(int expected, List<Point2D> actual) {
        assertNotNull(actual);
        int doubledArea = 0;
        for (int i = 0; i < actual.size(); i++) {
            Point2D p = actual.get(i);
            Point2D next = actual.get((i + 1) % actual.size());
            doubledArea += p.getX() * next.getY() - next.getX() * p.getY();
        }
        assertEquals(expected, Math.abs(doubledArea) / 2.0, format(actual));
    }

    private static void assertInsideSquare(List<Point2D> actual) {
        for (Point2D p : actual) {
            assertTrue(p.getX() >= 10 && p.getX() <= 30 && p.getY() >= 10 && p.getY() <= 30, format(actual));
        }
    }

    private static String format(List<Point2D> points) {
        StringBuilder text = new StringBuilder();
        for (Point2D p : points) {
            text.append('(').append(p.getX()).append(", ").append(p.getY()).append(") ");
        }
        return text.toString();
    }
}
