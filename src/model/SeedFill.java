package model;

import rasterize.Rasterizable;
import rasterize.RasterizationContext;
import rasterize.RasterizationMode;

import java.awt.*;

public class SeedFill implements Rasterizable {
    private Point2D seed;
    private int fillColor;

    public SeedFill(Point2D seed, int fillColor) {
        this.seed = seed;
        this.fillColor = fillColor;
    }

    public Point2D getSeed() {
        return seed;
    }

    public void setSeed(Point2D seed) {
        this.seed = seed;
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    @Override
    public void rasterize(RasterizationContext context) {
        context.getSeedFiller().setSeed(seed);
        context.getSeedFiller().setFillColor(fillColor);
        context.getSeedFiller().fill();
    }

    @Override
    public void rasterize(RasterizationContext context, RasterizationMode mode) {
        rasterize(context);
    }


}
