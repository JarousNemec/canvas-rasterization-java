package fill.pattern;

//common part of the patterns made of two alternating colors
//subclass only decides by its formula which of the two colors belongs to the pixel
public abstract class TwoColorPattern implements PatternFill {
    private final int primaryColor;
    private final int secondaryColor;

    protected TwoColorPattern(int primaryColor, int secondaryColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    @Override
    public int getPixelForPoint(int x, int y) {
        return isPrimary(x, y) ? primaryColor : secondaryColor;
    }

    protected abstract boolean isPrimary(int x, int y);

    //size of squares / stripes must be positive, otherwise the formula divides by zero
    protected static int checkSize(int size) {
        if (size <= 0)
            throw new IllegalArgumentException("Pattern size must be positive, found " + size);
        return size;
    }

    //index of the cell (square / stripe) the coordinate falls into is even
    //floorDiv and floorMod instead of / and %, so it works for negative coordinates too
    protected static boolean isEvenCell(int coordinate, int size) {
        return Math.floorMod(Math.floorDiv(coordinate, size), 2) == 0;
    }

    @Override
    public int getPrimaryColor() {
        return primaryColor;
    }

    @Override
    public int getSecondaryColor() {
        return secondaryColor;
    }
}
