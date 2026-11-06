package clip;

import model.Point2D;

public class ClipWindow {

    //top left corner
    private Point2D cornerA;

    //bottom right corner
    private Point2D cornerB;

    public ClipWindow(Point2D cornerB, Point2D cornerA) {
        setCornerB(cornerB);
        setCornerA(cornerA);
    }

    public int getXMin() { return Math.min(cornerA.getX(), cornerB.getX()); }
    public int getXMax() { return Math.max(cornerA.getX(), cornerB.getX()); }
    public int getYMin() { return Math.min(cornerA.getY(), cornerB.getY()); }
    public int getYMax() { return Math.max(cornerA.getY(), cornerB.getY()); }

    public int getWidth()  { return getXMax() - getXMin(); }
    public int getHeight() { return getYMax() - getYMin(); }

    public void setCornerB(Point2D cornerB) {
        this.cornerB = cornerB;
    }

    public Point2D getCornerA() {
        return cornerA;
    }

    public void setCornerA(Point2D cornerA) {
        this.cornerA = cornerA;
    }
}
