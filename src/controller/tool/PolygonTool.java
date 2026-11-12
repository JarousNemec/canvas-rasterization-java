package controller.tool;

import model.Point2D;
import model.Polygon;
import rasterize.RasterizationContext;
import select.Hit;
import select.HitType;
import utils.Logger;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class PolygonTool extends EditorTool {

    private Polygon currentPolygon = new Polygon();
    private boolean isPointStarted = false;

    public PolygonTool(ToolContext toolContext) {
        super(toolContext);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (e.getButton() == MouseEvent.BUTTON1 && isPointInRaster(e.getPoint())) {
            isPointStarted = true;
        } else if (e.getButton() == MouseEvent.BUTTON3 && isPointInRaster(e.getPoint())) {
            handlePolygonRemove(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        //released out of the canvas is fine too, the point is moved to the border
        //but the point must have been started by a press in the canvas
        if (e.getButton() == MouseEvent.BUTTON1 && isPointStarted) {
            handlePolygonAddPoint(e);
            isPointStarted = false;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        if (e.getKeyChar() == 'a' || e.getKeyChar() == 'A') {
            if (currentPolygon.getSize() > 0) {
                var canvasModel = toolContext.getCanvasModel();
                if (!canvasModel.contains(currentPolygon))
                    canvasModel.addRasterizable(currentPolygon);
                currentPolygon = new Polygon();
                //next polygon gets the current colors and pattern too, not the defaults
                updateByContext();
                Logger.info("Added new polygon.");
            }
        }
    }

    @Override
    public void drawPreview(RasterizationContext context) {
        super.drawPreview(context);
        if (isBtn1Pressed && isPointStarted)
            if (currentPolygon != null) {
                Point2D point = getPointInCanvas((int) this.mousePosition.getX(), (int) this.mousePosition.getY());
                context.getPolygonRasterizer().rasterizePointPreview(point.getX(), point.getY(), currentPolygon);
            }
    }

    @Override
    public void updateByContext() {
        super.updateByContext();
        if (currentPolygon != null) {
            currentPolygon.setColor(toolContext.getCurrentLineColor());
            currentPolygon.setFillPattern(toolContext.createFillPattern());
            if (toolContext.isFillWanted()) {
                currentPolygon.enableFill();
            }else{
                currentPolygon.disableFill();
            }
        }
    }

    private void handlePolygonRemove(MouseEvent e) {
        Hit hit = EditorToolUtil.handleHitTest(toolContext.getCanvasModel().getSelectables(), e.getPoint());
        if (hit != null && hit.hitType() == HitType.EDGE && hit.target() instanceof Polygon polygon) {
            toolContext.getCanvasModel().removeRasterizable(polygon);
            //the polygon in progress must not come back with the next added point
            if (polygon == currentPolygon) {
                currentPolygon = new Polygon();
                updateByContext();
            }
            Logger.info("Polygon removed: " + polygon);
        }
    }

    private void handlePolygonAddPoint(MouseEvent e) {
        Point2D point = getPointInCanvas(e.getX(), e.getY());
        currentPolygon.addPoint(point);
        var canvasModel = toolContext.getCanvasModel();
        if (!canvasModel.contains(currentPolygon))
            canvasModel.addRasterizable(currentPolygon);
        Logger.info("New point: " + point.getX() + ", " + point.getY() + " has been added to polygon.");
    }
}
