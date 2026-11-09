package controller.tool;

import clip.ClipContext;
import edit.VertexListEditable;
import edit.VertexMovable;
import model.Point2D;
import model.Polygon;
import rasterize.RasterizationContext;
import select.Hit;
import select.HitType;
import select.Selectable;
import utils.GeometryUtil;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ClipTool extends EditorTool {
    private Hit currentHit = null;
    private final ClipContext clipContext;
    private static final int MIN_CLIP_POLYGON_VERTICES_COUNT = 5;


    public ClipTool(ToolContext toolContext, ClipContext clipContext) {
        super(toolContext);
        this.clipContext = clipContext;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (e.getButton() == MouseEvent.BUTTON1 && isPointInRaster(e.getPoint())) {
            currentHit = EditorToolUtil.handleHitTest(getCandidates(), e.getPoint());
            if (currentHit != null) {
                if (currentHit.hitType() == HitType.EDGE && currentHit.target() instanceof VertexListEditable vertexList) {
                    int pIndex = (currentHit.index() + 1) % vertexList.getSize();
                    vertexList.addPoint(pIndex, currentHit.closestPoint());
                    currentHit = new Hit(currentHit.target(), HitType.VERTEX, pIndex, currentHit.distanceSq(), currentHit.closestPoint());
                }
            }
        } else if (e.getButton() == MouseEvent.BUTTON3 && isPointInRaster(e.getPoint())) {
            currentHit = EditorToolUtil.handleHitTest(getCandidates(), e.getPoint());
            if (currentHit != null) {
                if (currentHit.hitType() == HitType.VERTEX && currentHit.target() instanceof VertexListEditable vertexList) {
                    if (vertexList.canRemovePoint() && vertexList.getSize() > MIN_CLIP_POLYGON_VERTICES_COUNT) {
                        vertexList.removePoint(currentHit.index());
                        currentHit = null;
                    }
                }
            }
        }
    }

    private List<Selectable> getCandidates() {
        List<Selectable> candidates = new ArrayList<>();
        if (!clipContext.isClipperVisualizationEnabled())
            return candidates;

        if (clipContext.isPolygonClippingEnabled() && clipContext.getClipPolygon() != null)
            candidates.add(clipContext.getClipPolygon());
        if (clipContext.isLineClippingEnabled() && clipContext.getClipWindow() != null)
            candidates.add(clipContext.getClipWindow());
        return candidates;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if (isPointInRaster(e.getPoint()) && isBtn1Pressed)
            handlePointMovement(e);
    }

    private void handlePointMovement(MouseEvent e) {
        if (currentHit == null || currentHit.hitType() != HitType.VERTEX) return;
        if (!(currentHit.target() instanceof VertexMovable movable)) return;

        int index = currentHit.index();
        Point2D newPosition = new Point2D(e.getX(), e.getY());

        if (movable instanceof Polygon polygon) {
            Point2D oldPosition = polygon.getPoint(index);
            polygon.setNewPointPosition(index, newPosition);

            //clip polygon must stay convex, otherwise revert
            if (!GeometryUtil.isPolygonConvex(polygon.getPointsReadOnly()))
                polygon.setNewPointPosition(index, oldPosition);
        } else
            movable.setNewPointPosition(index, newPosition);
    }

    @Override
    public void drawPreview(RasterizationContext context) {
        super.drawPreview(context);
    }

    @Override
    public void updateByContext() {
        super.updateByContext();
    }
}
