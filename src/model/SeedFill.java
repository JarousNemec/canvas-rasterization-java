package model;

import rasterize.Rasterizable;
import rasterize.RasterizationContext;
import rasterize.RasterizationMode;

public class SeedFill implements Rasterizable {
    private Point2D seed;
    private int color;

    public SeedFill(Point2D seed, int color) {
        this.seed = seed;
        this.color = color;
    }

    public Point2D getSeed() {
        return seed;
    }

    public void setSeed(Point2D seed) {
        this.seed = seed;
    }

    @Override
    public void rasterize(RasterizationContext context) {
        context.getSeedFiller().setSeed(seed);
        context.getSeedFiller().setFillColor(this.color);
        context.getSeedFiller().fill();
    }

    @Override
    public void rasterize(RasterizationContext context, RasterizationMode mode) {
        rasterize(context);
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int getColor() {
        return this.color;
    }


}
