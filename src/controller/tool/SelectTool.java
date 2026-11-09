package controller.tool;

import fill.Fillable;
import rasterize.Rasterizable;
import rasterize.RasterizationContext;
import rasterize.RasterizationMode;
import select.Hit;
import utils.ColorUtil;

import java.awt.event.MouseEvent;

public class SelectTool extends EditorTool {

    private Hit currentHit = null;

    public SelectTool(ToolContext toolContext) {
        super(toolContext);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (e.getButton() == MouseEvent.BUTTON1 && isPointInRaster(e.getPoint())) {
            currentHit = EditorToolUtil.handleHitTest(toolContext.getCanvasModel().getSelectables(), e.getPoint());
            if (currentHit != null) {
                if (currentHit.target() instanceof Rasterizable)
                    toolContext.setLineColorFromTool(((Rasterizable) currentHit.target()).getColor());
                if (currentHit.target() instanceof Fillable)
                    toolContext.setFillColorFromTool(((Fillable) currentHit.target()).getFillColor());
            }
        }
    }

    @Override
    public void drawPreview(RasterizationContext context) {
        super.drawPreview(context);
        if (currentHit != null) {
            if (currentHit.target() instanceof Rasterizable) {
                ((Rasterizable) currentHit.target()).rasterize(context, RasterizationMode.GLOWING);
            }
        }
    }

    @Override
    public void updateByContext() {
        super.updateByContext();
        if (currentHit != null) {
            if (currentHit.target() instanceof Rasterizable)
                ((Rasterizable) currentHit.target()).setColor(toolContext.getCurrentLineColor());
            if (currentHit.target() instanceof Fillable) {
                Fillable targetedFillable = ((Fillable) currentHit.target());
                targetedFillable.setFillColor(toolContext.getCurrentFillColor());
                if (toolContext.getCurrentFillColor() != ColorUtil.DEFAULT_FILL_COLOR) {
                    targetedFillable.enableFill();
                } else {
                    targetedFillable.disableFill();
                }
            }
        }
    }
}
