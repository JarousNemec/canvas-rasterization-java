package select;

import model.ClipWindow;
import model.Point2D;
import model.Polygon;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SelectionUtilTest {

    @Test
    void findHit_polygonVertexOnWindowEdge_vertexWins() {
        //default clippers from ClipContext: pentagon vertex (200, 220) lies right on the left edge of the window
        //window edge is 1 px away, the vertex 1.41 px - the vertex must still win, otherwise it can't be dragged
        ClipWindow window = new ClipWindow(new Point2D(200, 150), new Point2D(600, 450));
        Polygon pentagon = polygonOf(400, 100, 600, 220, 560, 440, 240, 440, 200, 220);

        Hit hit = SelectionUtil.findHit(List.of(pentagon, window), 201, 221);

        assertNotNull(hit);
        assertSame(pentagon, hit.target());
        assertEquals(HitType.VERTEX, hit.hitType());
        assertEquals(4, hit.index());
    }

    @Test
    void findHit_sameDistance_laterCandidateWins() {
        //later in the list = drawn on top
        Polygon a = polygonOf(0, 0, 10, 0, 10, 10);
        Polygon b = polygonOf(0, 0, 10, 0, 10, 10);

        assertSame(b, SelectionUtil.findHit(List.of(a, b), 1, 1).target());
        assertSame(a, SelectionUtil.findHit(List.of(b, a), 1, 1).target());
    }

    @Test
    void findHit_noCandidates_isNull() {
        //ClipTool passes an empty list when the clippers are hidden
        assertNull(SelectionUtil.findHit(List.<Selectable>of(), 0, 0));
    }

    private static Polygon polygonOf(int... coordinates) {
        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < coordinates.length; i += 2)
            points.add(new Point2D(coordinates[i], coordinates[i + 1]));
        return new Polygon(points);
    }
}
