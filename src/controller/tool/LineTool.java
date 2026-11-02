package controller.tool;

import model.Line;
import model.Point2D;
import rasterize.RasterizationContext;
import rasterize.RasterizationMode;
import utils.GeometryUtil;
import utils.Logger;
import java.awt.event.MouseEvent;

public class LineTool extends EditorTool {

    private Line currentLine;

    public LineTool(ToolContext toolContext) {
        super(toolContext);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (e.getButton() == MouseEvent.BUTTON1 && isPointInRaster(e.getPoint())) {
            handleNewLine(e);
            isBtn1Pressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (isPointInRaster(e.getPoint())) {
                handleLineAdd(e);
            }
            isBtn1Pressed = false;
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
        currentLine = new Line();
        currentLine.setStart(new Point2D(e.getX(), e.getY()));
        currentLine.setEnd(new Point2D(e.getX(), e.getY()));
    }

    private void handleLineAdd(MouseEvent e) {
        Point2D lineEnd = getLineEnd(e.getX(), e.getY());
        currentLine.setEnd(lineEnd);
        var canvasModel = toolContext.getCanvasModel();
        if (!canvasModel.contains(currentLine))
            canvasModel.addRasterizable(currentLine);
        Logger.info("New line added: " + currentLine.toString());
    }

    private Point2D getLineEnd(int x, int y) {
        Point2D start = currentLine.getStart();
        if (isShiftPressed)
            return GeometryUtil.attachPointToAxis(start.getX(), start.getY(), x, y);
        return new Point2D(x, y);
    }
}
