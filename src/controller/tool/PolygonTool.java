package controller.tool;

import model.Point2D;
import model.Polygon;
import rasterize.RasterizationContext;
import select.Hit;
import select.HitType;
import utils.ColorUtil;
import utils.Logger;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class PolygonTool extends EditorTool {

    private Polygon currentPolygon = new Polygon();

    public PolygonTool(ToolContext toolContext) {
        super(toolContext);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (e.getButton() == MouseEvent.BUTTON3 && isPointInRaster(e.getPoint())) {
            handlePolygonRemove(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (isPointInRaster(e.getPoint())) {
                handlePolygonAddPoint(e);
            }
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
                Logger.info("Added new polygon.");
            }
        }
    }

    @Override
    public void drawPreview(RasterizationContext context) {
        super.drawPreview(context);
        if (isBtn1Pressed)
            if (currentPolygon != null) {
                int x = (int) this.mousePosition.getX();
                int y = (int) this.mousePosition.getY();
                context.getPolygonRasterizer().rasterizePointPreview(x, y, currentPolygon);
            }
    }

    @Override
    public void updateByContext() {
        super.updateByContext();
        if (currentPolygon != null) {
            currentPolygon.setColor(toolContext.getCurrentLineColor());
            currentPolygon.setFillColor(toolContext.getCurrentFillColor());
            if (toolContext.getCurrentFillColor() != ColorUtil.DEFAULT_FILL_COLOR) {
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
        currentPolygon.addPoint(new Point2D(e.getX(), e.getY()));
        var canvasModel = toolContext.getCanvasModel();
        if (!canvasModel.contains(currentPolygon))
            canvasModel.addRasterizable(currentPolygon);
        Logger.info("New point: " + e.getX() + ", " + e.getY() + " has been added to polygon.");
    }
}
