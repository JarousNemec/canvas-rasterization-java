package fill;

import fill.pattern.PatternFill;
import fill.pattern.SolidPattern;
import model.Point2D;
import raster.Raster;
import utils.ColorUtil;

import java.util.Arrays;

//not used in the app, kept only for comparison with SeedFillerStackImpl - recursion overflows the call stack in large areas
public class SeedFillerRecursionImpl implements SeedFiller {

    private final Raster raster;
    private PatternFill pattern = new SolidPattern(ColorUtil.DEFAULT_FILL_COLOR);
    private int backgroundColor = ColorUtil.DEFAULT_BACKGROUND_COLOR;
    private int borderColor = ColorUtil.DEFAULT_LINE_COLOR;
    private Point2D seed = null;
    private SeedFillStopMode mode = SeedFillStopMode.BACKGROUND;

    //already filled pixels, index = y * width + x (same reason as in SeedFillerStackImpl - pattern has more colors)
    private final boolean[] visited;

    public SeedFillerRecursionImpl(Raster raster) {
        this.raster = raster;
        visited = new boolean[raster.getWidth() * raster.getHeight()];
    }

    @Override
    public void fill() {
        if (seed == null || !raster.contains(seed.getX(), seed.getY()))
            return;

        Arrays.fill(visited, false);
        int seedColor = raster.getPixel(seed.getX(), seed.getY());
        switch (mode) {
            case BACKGROUND -> {
                backgroundColor = seedColor;
                seedFillRecursionBackground(seed.getX(), seed.getY());
            }
            case BORDER -> {
                if (seedColor == borderColor)
                    return;
                seedFillRecursionBorder(seed.getX(), seed.getY());
            }
        }

    }

    //useless because it consumes the whole stack and then overflows in large areas, better use Stack or Queue implementation
    private void seedFillRecursionBackground(int x, int y) {
        if (!raster.contains(x, y) || isVisited(x, y))
            return;
        int pixelColor = raster.getPixel(x, y);
        if (backgroundColor != pixelColor)
            return;
        fillPixel(x, y);
        seedFillRecursionBackground(x - 1, y);
        seedFillRecursionBackground(x + 1, y);
        seedFillRecursionBackground(x, y - 1);
        seedFillRecursionBackground(x, y + 1);
    }

    private void seedFillRecursionBorder(int x, int y) {
        if (!raster.contains(x, y) || isVisited(x, y))
            return;
        int pixelColor = raster.getPixel(x, y);

        //this condition is the diff between the background approach
        if (borderColor == pixelColor)
            return;
        fillPixel(x, y);
        seedFillRecursionBorder(x - 1, y);
        seedFillRecursionBorder(x + 1, y);
        seedFillRecursionBorder(x, y - 1);
        seedFillRecursionBorder(x, y + 1);
    }

    private void fillPixel(int x, int y) {
        visited[y * raster.getWidth() + x] = true;
        raster.setPixel(x, y, pattern.getPixelForPoint(x, y));
    }

    private boolean isVisited(int x, int y) {
        return visited[y * raster.getWidth() + x];
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public PatternFill getPattern() {
        return pattern;
    }

    @Override
    public void setPattern(PatternFill pattern) {
        this.pattern = pattern;
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

    public SeedFillStopMode getMode() {
        return mode;
    }

    public void setMode(SeedFillStopMode mode) {
        this.mode = mode;
    }
}
