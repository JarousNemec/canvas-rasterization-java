package controller.tool;

import model.Point2D;
import model.SeedFill;

import java.awt.event.MouseEvent;

public class FillerTool extends EditorTool {

    public FillerTool(ToolContext toolContext) {
        super(toolContext);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (isPointInRaster(e.getPoint())) {
                handleSeedFillerAdd(e);
            }
        }
    }

    private void handleSeedFillerAdd(MouseEvent e) {
        //border color = current line color, used only in BORDER mode
        var seedFill = new SeedFill(
                new Point2D(e.getX(), e.getY()),
                toolContext.getCurrentFillColor(),
                toolContext.getCurrentSeedFillMode(),
                toolContext.getCurrentLineColor());
        toolContext.getCanvasModel().addRasterizable(seedFill);
    }
}
