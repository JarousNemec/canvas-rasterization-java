package controller.tool;

import clip.CohenSutherland;
import model.ClipWindow;
import model.Point2D;
import rasterize.RasterizationContext;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class EditorTool implements Tool {
    protected final ToolContext toolContext;

    protected boolean isBtn1Pressed = false;
    protected boolean isShiftPressed = false;
    protected Point mousePosition = null;

    public EditorTool(ToolContext toolContext) {
        this.toolContext = toolContext;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            isBtn1Pressed = true;
        }
        updateMousePosition(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            isBtn1Pressed = false;
        }
        updateMousePosition(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateMousePosition(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        updateShift(e.isShiftDown());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        updateShift(e.isShiftDown());
    }

    @Override
    public void drawPreview(RasterizationContext context) {}

    @Override
    public void updateByContext(){}

    //mouse can be dragged out of the canvas, so the points from the mouse are moved back to the canvas instead of being thrown away
    //point inside the canvas is returned as it is, so it can be called for every point

    //outside -> nearest point on the border of the canvas (x and y are clamped separately)
    //used where the point has no line it belongs to (polygon vertex, moved vertex)
    //it moves along the border while the mouse is moving outside, so the drawing stays smooth
    protected Point2D getPointInCanvas(int x, int y) {
        int clampedX = Math.clamp(x, 0, toolContext.getRasterWidth() - 1);
        int clampedY = Math.clamp(y, 0, toolContext.getRasterHeight() - 1);
        return new Point2D(clampedX, clampedY);
    }

    //outside -> point where the line from -> (x, y) crosses the border of the canvas, so the line keeps its direction
    //(clamping would bend it, e.g. the diagonal line drawn with shift would not be diagonal anymore)
    protected Point2D getPointInCanvas(Point2D from, int x, int y) {
        if (isPointInRaster(new Point(x, y)))
            return new Point2D(x, y);
        //the canvas is the clip window here, Cohen-Sutherland gives us the part of the line inside it
        ClipWindow canvas = new ClipWindow(new Point2D(toolContext.getRasterWidth() - 1, toolContext.getRasterHeight() - 1), new Point2D(0, 0));
        Point2D[] clipped = CohenSutherland.clip(from, new Point2D(x, y), canvas);
        //from is always inside (the line starts by a click to the canvas), this is just for safety
        if (clipped.length == 0)
            return getPointInCanvas(x, y);
        return clipped[1];
    }

    protected void updateMousePosition(MouseEvent e) {
        mousePosition = e.getPoint();
        isShiftPressed = e.isShiftDown();
    }

    protected boolean isPointInRaster(Point p) {
        return p.getX() >= 0 && p.getY() >= 0 && p.getX() < toolContext.getRasterWidth() && p.getY() < toolContext.getRasterHeight();
    }

    protected void updateShift(boolean shiftDown) {
        if (isShiftPressed == shiftDown)
            return;
        isShiftPressed = shiftDown;
    }
}
