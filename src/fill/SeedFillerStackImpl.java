package fill;

import fill.pattern.PatternFill;
import fill.pattern.SolidPattern;
import model.Point2D;
import raster.Raster;
import utils.ColorUtil;

import java.util.Arrays;

public class SeedFillerStackImpl implements SeedFiller {

    private final Raster raster;
    private PatternFill pattern = new SolidPattern(ColorUtil.DEFAULT_FILL_COLOR);
    private int backgroundColor = ColorUtil.DEFAULT_BACKGROUND_COLOR;
    private int borderColor = ColorUtil.DEFAULT_LINE_COLOR;
    private Point2D seed = null;
    private SeedFillStopMode mode = SeedFillStopMode.BACKGROUND;

    //Custom stack implementation
    private final int[] stackX;
    private final int[] stackY;

    //already filled pixels, index = y * width + x
    //filled pixel can't be recognized by its color, because pattern has more colors (and can contain the background color),
    //without it the fill would visit the same pixels again and again
    private final boolean[] visited;

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
        visited = new boolean[maxSize];
    }

    @Override
    public void fill() {
        if (seed == null || !raster.contains(seed.getX(), seed.getY()))
            return;

        int seedColor = raster.getPixel(seed.getX(), seed.getY());
        switch (mode) {
            case BACKGROUND -> {
                backgroundColor = seedColor;
                seedFillStack();
            }
            case BORDER -> {
                if (seedColor == borderColor)
                    return;
                seedFillStack();
            }
        }
    }

    //BACKGROUND and BORDER differ only in the condition, which pixels belong to the area (canFill)
    private void seedFillStack() {
        Arrays.fill(visited, false);
        int top = 0;

        //every pixel is filled and marked as visited when it is pushed, so it is pushed only once
        //and the stack can't be bigger than the raster
        fillPixel(seed.getX(), seed.getY());
        stackX[top] = seed.getX();
        stackY[top] = seed.getY();
        top++;

        while (top > 0) {
            top--;
            int x = stackX[top];
            int y = stackY[top];

            for (int i = 0; i < DX.length; i++) {
                int nx = x + DX[i];
                int ny = y + DY[i];
                if (raster.contains(nx, ny) && !isVisited(nx, ny) && canFill(raster.getPixel(nx, ny))) {
                    fillPixel(nx, ny);
                    stackX[top] = nx;
                    stackY[top] = ny;
                    top++;
                }
            }
        }
    }

    private boolean canFill(int pixelColor) {
        return switch (mode) {
            case BACKGROUND -> pixelColor == backgroundColor;
            case BORDER -> pixelColor != borderColor;
        };
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
