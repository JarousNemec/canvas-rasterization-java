package fill;

import model.Point2D;
import raster.Raster;
import utils.ColorUtils;

import java.awt.*;

public class SeedFiller implements Filler {

    private final Raster raster;
    private int fillColor = ColorUtils.toRgbInt(Color.GREEN);
    private int backgroundColor = ColorUtils.toRgbInt(Color.BLACK);
    private Point2D seed = null;

    public SeedFiller(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void fill() {
        if (seed == null)
            return;
        backgroundColor = raster.getPixel(seed.getX(), seed.getY());
        seedFill(seed.getX(), seed.getY());
    }

    private void seedFill(int x, int y) {
        int pixelColor = raster.getPixel(x, y);
        if (pixelColor == -1 || pixelColor != backgroundColor)
            return;
        raster.setPixel(x, y, fillColor);
        seedFill(x-1,y);
        seedFill(x+1,y);
        seedFill(x,y-1);
        seedFill(x,y+1);
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
