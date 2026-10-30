package fill;

import model.Point2D;
import raster.Raster;

import java.awt.*;

public class SeedFillerRecursionImpl implements SeedFiller {

    private final Raster raster;
    private int fillColor = Color.GREEN.getRGB();
    private int backgroundColor = Color.BLACK.getRGB();
    private Point2D seed = null;

    public SeedFillerRecursionImpl(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void fill() {
        if (seed == null || !raster.contains(seed.getX(), seed.getY()))
            return;
        backgroundColor = raster.getPixel(seed.getX(), seed.getY());
        if (backgroundColor == fillColor)
            return;
        seedFillRecursion(seed.getX(), seed.getY());
    }

    //useless because it consumes whole stack and than overflows in large areas, better use Stack or Queue implementation
    private void seedFillRecursion(int x, int y) {
        if (!raster.contains(x, y))
            return;
        int pixelColor = raster.getPixel(x, y);
        if (backgroundColor != pixelColor)
            return;
        raster.setPixel(x, y, fillColor);
        seedFillRecursion(x - 1, y);
        seedFillRecursion(x + 1, y);
        seedFillRecursion(x, y - 1);
        seedFillRecursion(x, y + 1);
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
}
