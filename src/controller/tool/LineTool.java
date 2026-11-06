package controller.tool;

import model.Line;
import model.Point2D;
import rasterize.RasterizationContext;
import rasterize.RasterizationMode;
import select.Hit;
import select.HitType;
import select.SelectionService;
import utils.GeometryUtil;
import utils.Logger;
import java.awt.event.MouseEvent;

public class LineTool extends EditorTool {

    private final SelectionService selectionService;
    private Line currentLine = new Line();

    public LineTool(ToolContext toolContext) {
        super(toolContext);
        selectionService = new SelectionService(toolContext.getCanvasModel());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (e.getButton() == MouseEvent.BUTTON1 && isPointInRaster(e.getPoint())) {
            handleNewLine(e);
        } else if (e.getButton() == MouseEvent.BUTTON3 && isPointInRaster(e.getPoint())) {
            handleLineRemove(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (isPointInRaster(e.getPoint())) {
                handleLineAdd(e);
            }
        }
    }

    @Override
    public void drawPreview(RasterizationContext context) {
        super.drawPreview(context);
        if (isBtn1Pressed)
            if (currentLine != null) {
                int x = (int) this.mousePosition.getX();
                int y = (int) this.mousePosition.getY();

                Point2D lineStart = currentLine.getStart();
                Point2D lineEnd = getLineEnd(x, y);
                context.getLineRasterizer().rasterize(lineStart, lineEnd.getX(), lineEnd.getY(), RasterizationMode.PREVIEW);
            }
    }

    private void handleNewLine(MouseEvent e) {
        currentLine.setStart(new Point2D(e.getX(), e.getY()));
        currentLine.setEnd(new Point2D(e.getX(), e.getY()));
    }

    private void handleLineAdd(MouseEvent e) {
        Point2D lineEnd = getLineEnd(e.getX(), e.getY());
        currentLine.setEnd(lineEnd);
        var canvasModel = toolContext.getCanvasModel();
        if (!canvasModel.contains(currentLine)) {
            currentLine.setColor(toolContext.getCurrentLineColor());
            canvasModel.addRasterizable(currentLine);
            currentLine = new Line();
        }
        Logger.info("New line added: " + currentLine.toString());
    }

    private void handleLineRemove(MouseEvent e) {
        Hit hit = EditorToolUtil.handleHitTest(selectionService, e.getPoint());
        if (hit != null && hit.hitType() == HitType.EDGE && hit.target() instanceof Line line) {
            toolContext.getCanvasModel().removeRasterizable(line);
            Logger.info("Line removed: " + line);
        }
    }

    @Override
    public void updateByContext() {
        super.updateByContext();
        if(currentLine != null){
            currentLine.setColor(toolContext.getCurrentLineColor());
        }
    }

    private Point2D getLineEnd(int x, int y) {
        Point2D start = currentLine.getStart();
        if (isShiftPressed)
            return GeometryUtil.snapToAxisOrDiagonal(start.getX(), start.getY(), x, y);
        return new Point2D(x, y);
    }
}
