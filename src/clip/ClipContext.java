package clip;

import model.Polygon;
import rasterize.Rasterizable;

public class ClipContext {
    private ClipWindow clipWindow = null;
    private Polygon clipPolygon = null;

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
}
