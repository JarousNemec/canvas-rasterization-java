package persistence;

import model.ClipWindow;
import model.Polygon;
import rasterize.Rasterizable;

import java.util.List;

//state of the scene in one moment - everything that is saved to the file and loaded back
//rasterizables are in the order they are drawn (seed fill depends on what is drawn under it)
public record SceneSnapshot(
        List<Rasterizable> rasterizables,
        ClipWindow clipWindow,
        Polygon clipPolygon,
        boolean lineClippingEnabled,
        boolean polygonClippingEnabled,
        boolean clipperVisualizationEnabled) {
}
