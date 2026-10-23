package model;

import rasterize.Rasterizable;

import java.util.ArrayList;
import java.util.List;

public class CanvasModel {
    private final List<Rasterizable> rasterizables;
    public CanvasModel() {
        this.rasterizables = new ArrayList<>();
    }
    public List<Rasterizable> getRasterizables() {
        return rasterizables;
    }
    public void addRasterizable(Rasterizable rasterizable) {
        rasterizables.add(rasterizable);
    }
    public void removeAllRasterizables() {
        rasterizables.clear();
    }
}
