package model;

import rasterize.Rasterizable;
import select.Selectable;

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

    // only the rasterizables that can be selected, in the same order (last = on top)
    public List<Selectable> getSelectables() {
        List<Selectable> selectables = new ArrayList<>();
        for (Rasterizable rasterizable : rasterizables) {
            if (rasterizable instanceof Selectable selectable)
                selectables.add(selectable);
        }
        return selectables;
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
    public void removeRasterizable(Rasterizable rasterizable) {
        rasterizables.remove(rasterizable);
    }
}
