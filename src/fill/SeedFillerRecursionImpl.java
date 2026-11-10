package fill;

import model.Point2D;
import raster.Raster;
import utils.ColorUtil;

public class SeedFillerRecursionImpl implements SeedFiller {

    private final Raster raster;
    private int fillColor = ColorUtil.DEFAULT_FILL_COLOR;
    private int backgroundColor = ColorUtil.DEFAULT_BACKGROUND_COLOR;
    private int borderColor = ColorUtil.DEFAULT_LINE_COLOR;
    private Point2D seed = null;
    private SeedFillMode mode = SeedFillMode.BACKGROUND;

    public SeedFillerRecursionImpl(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void fill() {
        if (seed == null || !raster.contains(seed.getX(), seed.getY()))
            return;

        int seedColor = raster.getPixel(seed.getX(), seed.getY());
        switch (mode) {
            case BACKGROUND -> {
                if (seedColor == fillColor)
                    return;
                backgroundColor = seedColor;
                seedFillRecursionBackground(seed.getX(), seed.getY());
            }
            case BORDER -> {
                if (seedColor == borderColor || seedColor == fillColor)
                    return;
                seedFillRecursionBorder(seed.getX(), seed.getY());
            }
            case PATTERN -> {
                seedFillRecursionPattern(seed.getX(), seed.getY());
            }
        }

    }

    //useless because it consumes the whole stack and then overflows in large areas, better use Stack or Queue implementation
    private void seedFillRecursionBackground(int x, int y) {
        if (!raster.contains(x, y))
            return;
        int pixelColor = raster.getPixel(x, y);
        if (backgroundColor != pixelColor)
            return;
        raster.setPixel(x, y, fillColor);
        seedFillRecursionBackground(x - 1, y);
        seedFillRecursionBackground(x + 1, y);
        seedFillRecursionBackground(x, y - 1);
        seedFillRecursionBackground(x, y + 1);
    }

    private void seedFillRecursionBorder(int x, int y) {
        if (!raster.contains(x, y))
            return;
        int pixelColor = raster.getPixel(x, y);

        //this condition is the diff between the background approach
        if (fillColor == pixelColor || borderColor == pixelColor)
            return;
        raster.setPixel(x, y, fillColor);
        seedFillRecursionBorder(x - 1, y);
        seedFillRecursionBorder(x + 1, y);
        seedFillRecursionBorder(x, y - 1);
        seedFillRecursionBorder(x, y + 1);
    }

    private void seedFillRecursionPattern(int x, int y) {
//        if (!raster.contains(x, y))
//            return;
//        int pixelColor = raster.getPixel(x, y);
//        if (backgroundColor != pixelColor)
//            return;
//        raster.setPixel(x, y, fillColor);
//        seedFillRecursionPattern(x - 1, y);
//        seedFillRecursionPattern(x + 1, y);
//        seedFillRecursionPattern(x, y - 1);
//        seedFillRecursionPattern(x, y + 1);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public Point2D getSeed() {
        return seed;
    }

    public void setSeed(Point2D seed) {
        this.seed = seed;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public SeedFillMode getMode() {
        return mode;
    }

    public void setMode(SeedFillMode mode) {
        this.mode = mode;
    }
}
