package model;

import fill.SeedFillMode;
import fill.SeedFillerStackImpl;
import rasterize.Rasterizable;
import rasterize.RasterizationContext;
import rasterize.RasterizationMode;

public class SeedFill implements Rasterizable {
    private Point2D seed;
    private int color;

    //every seed fill keeps its own mode and border color,
    //the filler is shared and the scene is redrawn all the time, so it must be set again before every fill
    private SeedFillMode mode;
    private int borderColor;

    public SeedFill(Point2D seed, int color, SeedFillMode mode, int borderColor) {
        this.seed = seed;
        this.color = color;
        this.mode = mode;
        this.borderColor = borderColor;
    }

    public Point2D getSeed() {
        return seed;
    }

    public void setSeed(Point2D seed) {
        this.seed = seed;
    }

    public SeedFillMode getMode() {
        return mode;
    }

    public void setMode(SeedFillMode mode) {
        this.mode = mode;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    @Override
    public void rasterize(RasterizationContext context) {
        SeedFillerStackImpl filler = context.getSeedFiller();
        filler.setSeed(seed);
        filler.setFillColor(this.color);
        filler.setMode(this.mode);
        filler.setBorderColor(this.borderColor);
        filler.fill();
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
