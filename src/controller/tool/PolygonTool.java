package controller.tool;

import model.Point2D;
import model.Polygon;
import rasterize.RasterizationContext;
import utils.Logger;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class PolygonTool extends EditorTool {

    private Polygon currentPolygon = new Polygon();

    public PolygonTool(ToolContext toolContext) {
        super(toolContext);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (isPointInRaster(e.getPoint())) {
                handlePolygonAddPoint(e);
            }
            isBtn1Pressed = false;
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

    private void handlePolygonAddPoint(MouseEvent e) {
        currentPolygon.addPoint(new Point2D(e.getX(), e.getY()));
        var canvasModel = toolContext.getCanvasModel();
        if (!canvasModel.contains(currentPolygon))
            canvasModel.addRasterizable(currentPolygon);
        Logger.info("New point: " + e.getX() + ", " + e.getY() + " has been added to polygon.");
    }
}
