package controller.tool;

import edit.VertexListEditable;
import model.Point2D;
import select.Hit;
import select.HitType;
import edit.VertexMovable;

import java.awt.event.MouseEvent;

public class VertexTool extends EditorTool {
    private Hit currentHit;

    public VertexTool(ToolContext toolContext) {
        super(toolContext);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (e.getButton() == MouseEvent.BUTTON1 && isPointInRaster(e.getPoint())) {
            currentHit = EditorToolUtil.handleHitTest(toolContext.getCanvasModel().getSelectables(), e.getPoint());
            if (currentHit != null) {
                if (currentHit.hitType() == HitType.EDGE && currentHit.target() instanceof VertexListEditable vertexList) {
                    int pIndex = (currentHit.index() + 1) % vertexList.getSize();
                    vertexList.addPoint(pIndex, currentHit.closestPoint());
                    currentHit = new Hit(currentHit.target(), HitType.VERTEX, pIndex, currentHit.distanceSq(), currentHit.closestPoint());
                }
            }
        } else if (e.getButton() == MouseEvent.BUTTON3 && isPointInRaster(e.getPoint())) {
            currentHit = EditorToolUtil.handleHitTest(toolContext.getCanvasModel().getSelectables(), e.getPoint());
            if (currentHit != null) {
                if (currentHit.hitType() == HitType.VERTEX && currentHit.target() instanceof VertexListEditable vertexList) {
                    if (vertexList.canRemovePoint()) {
                        vertexList.removePoint(currentHit.index());
                        currentHit = null;
                    }
                }
            }
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
            if (currentHit.target() instanceof VertexMovable && currentHit.hitType() == HitType.VERTEX) {
                ((VertexMovable) currentHit.target()).setNewPointPosition(currentHit.index(), new Point2D(e.getX(), e.getY()));
            }
        }
    }
}
