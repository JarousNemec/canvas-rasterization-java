package controller.tool;

import model.Line;
import model.Point2D;
import rasterize.RasterizationContext;
import rasterize.RasterizationMode;
import select.Hit;
import select.HitType;
import utils.GeometryUtil;
import utils.Logger;
import java.awt.event.MouseEvent;

public class LineTool extends EditorTool {

    private Line currentLine = new Line();
    private boolean isLineStarted = false;

    public LineTool(ToolContext toolContext) {
        super(toolContext);
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
        //released out of the canvas is fine too, the end is moved to the border (see getLineEnd)
        //but the line must have been started by a press in the canvas
        if (e.getButton() == MouseEvent.BUTTON1 && isLineStarted) {
            handleLineAdd(e);
            isLineStarted = false;
        }
    }

    @Override
    public void drawPreview(RasterizationContext context) {
        super.drawPreview(context);
        if (isBtn1Pressed && isLineStarted)
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
        isLineStarted = true;
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
        Hit hit = EditorToolUtil.handleHitTest(toolContext.getCanvasModel().getSelectables(), e.getPoint());
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

    //snapped first and then moved to the canvas border along the line, so the snapped direction is kept
    private Point2D getLineEnd(int x, int y) {
        Point2D start = currentLine.getStart();
        Point2D end = new Point2D(x, y);
        if (isShiftPressed)
            end = GeometryUtil.snapToAxisOrDiagonal(start.getX(), start.getY(), x, y);
        return getPointInCanvas(start, end.getX(), end.getY());
    }
}
