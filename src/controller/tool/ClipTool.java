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
            //an EDGE hit is kept, the new vertex is inserted on the first drag (see handleVertexInsert)
            currentHit = EditorToolUtil.handleHitTest(getCandidates(), e.getPoint());
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

    //the vertex is inserted at the mouse position, not at the closest point on the edge - that one is rounded to
    //whole pixels and on a slanted edge it can end up slightly inside and make the clip polygon concave
    //same as a move: insert, check convexity, revert - so the clip polygon can never get into a non-convex state
    private void handleVertexInsert(Polygon polygon, Point2D position) {
        int index = currentHit.index() + 1;
        polygon.addPoint(index, position);
        if (!GeometryUtil.isPolygonConvex(polygon.getPointsReadOnly())) {
            polygon.removePoint(index); //try again on the next drag event
            return;
        }
        currentHit = new Hit(polygon, HitType.VERTEX, index, 0, position);
    }

    private void handlePointMovement(MouseEvent e) {
        if (currentHit == null) return;
        if (currentHit.hitType() == HitType.EDGE && currentHit.target() instanceof Polygon polygon) {
            handleVertexInsert(polygon, new Point2D(e.getX(), e.getY()));
            return;
        }
        if (currentHit.hitType() != HitType.VERTEX) return;
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
        //only the visible clippers can be edited, so mark only their vertices
        for (Selectable candidate : getCandidates())
            if (candidate instanceof VertexMovable movable)
                context.getVertexMarkerRasterizer().rasterize(movable);
    }

    @Override
    public void updateByContext() {
        super.updateByContext();
    }
}
