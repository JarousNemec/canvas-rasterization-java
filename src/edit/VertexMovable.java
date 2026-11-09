package edit;

import model.Point2D;

import java.util.List;

public interface VertexMovable {
    void setNewPointPosition(int index, Point2D newPosition);

    //vertices in the same order as the indexes used by setNewPointPosition
    List<Point2D> getVertices();
}
