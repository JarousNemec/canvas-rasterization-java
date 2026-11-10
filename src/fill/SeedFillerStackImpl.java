package fill;

import model.Point2D;
import raster.Raster;
import utils.ColorUtil;

public class SeedFillerStackImpl implements SeedFiller {

    private final Raster raster;
    private int fillColor = ColorUtil.DEFAULT_FILL_COLOR;
    private int backgroundColor = ColorUtil.DEFAULT_BACKGROUND_COLOR;
    private int borderColor = ColorUtil.DEFAULT_LINE_COLOR;
    private Point2D seed = null;
    private SeedFillMode mode = SeedFillMode.BACKGROUND;

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

        int seedColor = raster.getPixel(seed.getX(), seed.getY());
        switch (mode) {
            case BACKGROUND -> {
                if (seedColor == fillColor)
                    return;
                backgroundColor = seedColor;
                seedFillStackBackground();
            }
            case BORDER -> {
                if (seedColor == borderColor || seedColor == fillColor)
                    return;
                seedFillStackBorder();
            }
            case PATTERN -> {
                seedFillStackPattern();
            }
        }

    }

    private void seedFillStackBackground() {
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

    private void seedFillStackBorder() {
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

                    //this condition is the diff between the background approach
                    if (fillColor != pixelColor && borderColor != pixelColor) {
                        raster.setPixel(nx, ny, fillColor);
                        stackX[top] = nx;
                        stackY[top] = ny;
                        top++;
                    }
                }
            }
        }
    }

    private void seedFillStackPattern() {
//        int top = 0;
//
//        stackX[top] = seed.getX();
//        stackY[top] = seed.getY();
//        top++;
//        raster.setPixel(seed.getX(), seed.getY(), fillColor);
//
//        while (top > 0) {
//            top--;
//            int x = stackX[top];
//            int y = stackY[top];
//
//            for (int i = 0; i < DX.length; i++) {
//                int nx = x + DX[i];
//                int ny = y + DY[i];
//                if (raster.contains(nx, ny)) {
//                    int pixelColor = raster.getPixel(nx, ny);
//                    if (backgroundColor == pixelColor) {
//                        raster.setPixel(nx, ny, fillColor);
//                        stackX[top] = nx;
//                        stackY[top] = ny;
//                        top++;
//                    }
//                }
//            }
//        }
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
