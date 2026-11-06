package clip;

import model.Point2D;
import model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class ClipContext {
    //default clippers placed in the middle of the 800x600 canvas
    private ClipWindow clipWindow = new ClipWindow(new Point2D(200, 150), new Point2D(600, 450));
    private Polygon clipPolygon = new Polygon(new ArrayList<>(List.of(
            new Point2D(400, 100),
            new Point2D(600, 220),
            new Point2D(560, 440),
            new Point2D(240, 440),
            new Point2D(200, 220)
    )));

    private boolean lineClippingEnabled = false;
    private boolean polygonClippingEnabled = false;
    private boolean clipperVisualizationEnabled = false;

    public ClipWindow getClipWindow() {
        return clipWindow;
    }

    public void setClipWindow(ClipWindow clipWindow) {
        this.clipWindow = clipWindow;
    }

    public Polygon getClipPolygon() {
        return clipPolygon;
    }

    public void setClipPolygon(Polygon clipPolygon) {
        this.clipPolygon = clipPolygon;
    }

    public boolean isLineClippingEnabled() {
        return lineClippingEnabled;
    }

    public void setLineClippingEnabled(boolean lineClippingEnabled) {
        this.lineClippingEnabled = lineClippingEnabled;
    }

    public boolean isPolygonClippingEnabled() {
        return polygonClippingEnabled;
    }

    public void setPolygonClippingEnabled(boolean polygonClippingEnabled) {
        this.polygonClippingEnabled = polygonClippingEnabled;
    }

    public boolean isClipperVisualizationEnabled() {
        return clipperVisualizationEnabled;
    }

    public void setClipperVisualizationEnabled(boolean clipperVisualizationEnabled) {
        this.clipperVisualizationEnabled = clipperVisualizationEnabled;
    }
}
