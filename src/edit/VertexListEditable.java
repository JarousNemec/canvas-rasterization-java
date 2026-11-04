package edit;

import model.Point2D;

public interface VertexListEditable extends VertexMovable {
    void addPoint(int index, Point2D newPoint);
    Point2D getPoint(int index);
    void removePoint(int index);
    int getSize();
    boolean canRemovePoint();
}
