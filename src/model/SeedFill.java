package model;

import fill.SeedFillStopMode;
import fill.SeedFillerStackImpl;
import fill.pattern.PatternFill;
import rasterize.Rasterizable;
import rasterize.RasterizationContext;
import rasterize.RasterizationMode;

public class SeedFill implements Rasterizable {
    private Point2D seed;
    private PatternFill fillPattern;

    //every seed fill keeps its own mode and border color,
    //the filler is shared and the scene is redrawn all the time, so it must be set again before every fill
    private SeedFillStopMode mode;
    private int borderColor;

    public SeedFill(Point2D seed, PatternFill fillPattern, SeedFillStopMode mode, int borderColor) {
        this.seed = seed;
        this.fillPattern = fillPattern;
        this.mode = mode;
        this.borderColor = borderColor;
    }

    public Point2D getSeed() {
        return seed;
    }

    public void setSeed(Point2D seed) {
        this.seed = seed;
    }

    public PatternFill getFillPattern() {
        return fillPattern;
    }

    public void setFillPattern(PatternFill fillPattern) {
        this.fillPattern = fillPattern;
    }

    public SeedFillStopMode getMode() {
        return mode;
    }

    public void setMode(SeedFillStopMode mode) {
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
        filler.setPattern(this.fillPattern);
        filler.setMode(this.mode);
        filler.setBorderColor(this.borderColor);
        filler.fill();
    }

    @Override
    public void rasterize(RasterizationContext context, RasterizationMode mode) {
        rasterize(context);
    }

    //seed fill has no edges, so its color is the primary color of the pattern
    //changing it keeps the type of the pattern and its second color
    @Override
    public void setColor(int color) {
        this.fillPattern = fillPattern.getFillerMode().createPattern(color, fillPattern.getSecondaryColor());
    }

    @Override
    public int getColor() {
        return fillPattern.getPrimaryColor();
    }
}
