package controller.tool;

import fill.Fillable;
import rasterize.Rasterizable;
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

    //Mouse event carries the exact shift state at the moment of the event, independent of keyboard focus
    protected void updateMousePosition(MouseEvent e) {
        mousePosition = e.getPoint();
        isShiftPressed = e.isShiftDown();
    }

    protected boolean isPointInRaster(Point p) {
        return p.getX() >= 0 && p.getY() >= 0 && p.getX() < toolContext.getRasterWidth() && p.getY() < toolContext.getRasterHeight();
    }

    //Shift changed on keyboard - redraw the preview immediately, even when the mouse is not moving
    protected void updateShift(boolean shiftDown) {
        if (isShiftPressed == shiftDown)
            return;
        isShiftPressed = shiftDown;
    }
}
