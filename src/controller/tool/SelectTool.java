package controller.tool;

import rasterize.Rasterizable;
import rasterize.RasterizationContext;
import rasterize.RasterizationMode;
import select.Hit;
import select.SelectionService;
import java.awt.event.MouseEvent;

public class SelectTool extends EditorTool {

    private final SelectionService selectionService;
    private Hit currentHit = null;

    public SelectTool(ToolContext toolContext) {
        super(toolContext);
        selectionService = new SelectionService(toolContext.getCanvasModel());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (e.getButton() == MouseEvent.BUTTON1 && isPointInRaster(e.getPoint())) {
            currentHit = EditorToolUtil.handleHitTest(selectionService, e.getPoint());
            isBtn1Pressed = true;
        }
    }

    @Override
    public void drawPreview(RasterizationContext context) {
        super.drawPreview(context);
        if (isBtn1Pressed)
            if (currentHit != null) {
                if (currentHit.target() instanceof Rasterizable) {
                    ((Rasterizable) currentHit.target()).rasterize(context, RasterizationMode.GLOWING);
                }
            }
    }
}
