package controller.tool;

import model.Point2D;
import select.Hit;
import select.HitType;
import select.SelectionService;
import transform.Transformable;
import java.awt.event.MouseEvent;

public class VertexTool extends EditorTool {
    private final SelectionService selectionService;
    private Hit currentHit;
    public VertexTool(ToolContext toolContext) {
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
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if (isPointInRaster(e.getPoint()) && isBtn1Pressed)
            handlePointMovement(e);

    }

    private void handlePointMovement(MouseEvent e) {
        if (currentHit != null) {
            if (currentHit.target() instanceof Transformable && currentHit.hitType() == HitType.VERTEX) {
                ((Transformable) currentHit.target()).setNewPointPosition(currentHit.index(), new Point2D(e.getX(), e.getY()));
            }
        }
    }
}
