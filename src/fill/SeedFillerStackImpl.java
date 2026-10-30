package fill;

import model.Point2D;
import raster.Raster;

import java.awt.*;

public class SeedFillerStackImpl implements SeedFiller {

    private final Raster raster;
    private int fillColor = Color.GREEN.getRGB();
    private int backgroundColor = Color.BLACK.getRGB();
    private Point2D seed = null;

    //Custom stack implementation
    private final int[] stackX;
    private final int[] stackY;

    //directions x, y
    private static final int[] DX = {-1, 1, 0, 0};
    private static final int[] DY = {0, 0, -1, 1};

    public SeedFillerStackImpl(Raster raster) {
        this.raster = raster;

        //Init one custom stack same for every call
        int width = raster.getWidth();
        int height = raster.getHeight();
        int maxSize = width * height;
        stackX = new int[maxSize];
        stackY = new int[maxSize];
    }

    @Override
    public void fill() {
        if (seed == null || !raster.contains(seed.getX(), seed.getY()))
            return;
        backgroundColor = raster.getPixel(seed.getX(), seed.getY());
        if (backgroundColor == fillColor)
            return;
        seedFillStack();
    }

    private void seedFillStack() {
        int top = 0;

        stackX[top] = seed.getX();
        stackY[top] = seed.getY();
        top++;
        raster.setPixel(seed.getX(), seed.getY(), fillColor);

        while (top > 0) {
            top--;
            int x = stackX[top];
            int y = stackY[top];

            for (int i = 0; i < DX.length; i++) {
                int nx = x + DX[i];
                int ny = y + DY[i];
                if (raster.contains(nx, ny)) {
                    int pixelColor = raster.getPixel(nx, ny);
                    if (backgroundColor == pixelColor) {
                        raster.setPixel(nx, ny, fillColor);
                        stackX[top] = nx;
                        stackY[top] = ny;
                        top++;
                    }
                }
            }
        }
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
