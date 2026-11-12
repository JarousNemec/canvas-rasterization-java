package controller.tool;

import model.CanvasModel;
import model.Point2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EditorToolTest {

    //canvas 800x600 -> x 0..799, y 0..599
    //EditorTool is abstract, so an empty subclass is enough for testing its helper methods
    private final EditorTool tool = new EditorTool(new ToolContext(new CanvasModel(), 600, 800)) {
    };

    // clamp

    @Test
    void pointInCanvas_inside_isNotChanged() {
        Point2D point = tool.getPointInCanvas(100, 200);

        assertEquals(100, point.getX());
        assertEquals(200, point.getY());
    }

    @Test
    void pointInCanvas_outside_isMovedToNearestBorder() {
        Point2D point = tool.getPointInCanvas(-50, 1000);

        assertEquals(0, point.getX());
        assertEquals(599, point.getY());
    }

    // along the line

    @Test
    void pointInCanvasFromStart_outsideRight_endsOnRightBorderInSameDirection() {
        //horizontal line to the right out of the canvas
        Point2D point = tool.getPointInCanvas(new Point2D(700, 300), 900, 300);

        assertEquals(799, point.getX());
        assertEquals(300, point.getY());
    }

    @Test
    void pointInCanvasFromStart_diagonal_staysDiagonal() {
        //45° line from (500, 500) down-right, leaves the canvas at the bottom border
        Point2D point = tool.getPointInCanvas(new Point2D(500, 500), 700, 700);

        assertEquals(599, point.getY());
        assertEquals(599, point.getX()); //x - 500 == y - 500
    }

    @Test
    void pointInCanvasFromStart_inside_isNotChanged() {
        Point2D point = tool.getPointInCanvas(new Point2D(10, 10), 20, 30);

        assertEquals(20, point.getX());
        assertEquals(30, point.getY());
    }
}
