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

    public int getRasterizablesCount() {
        return rasterizables.size();
    }

    public Rasterizable getRasterizable(int index) {
        return rasterizables.get(index);
    }

    public boolean contains(Rasterizable rasterizable) {
        return rasterizables.contains(rasterizable);
    }

    public void addRasterizable(Rasterizable rasterizable) {
        rasterizables.add(rasterizable);
    }
    public void removeAllRasterizables() {
        rasterizables.clear();
    }
}
