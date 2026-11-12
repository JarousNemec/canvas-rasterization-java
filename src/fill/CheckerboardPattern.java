package fill;

//checkerboard of squares with the side squareSize px
//column + row of the square is even -> primary color, odd -> secondary color
public class CheckerboardPattern extends TwoColorPattern {
    private final int squareSize;

    public CheckerboardPattern(int squareSize, int primaryColor, int secondaryColor) {
        super(primaryColor, secondaryColor);
        this.squareSize = checkSize(squareSize);
    }

    @Override
    protected boolean isPrimary(int x, int y) {
        //same result as "column and row are both even or both odd"
        return isEvenCell(x, squareSize) == isEvenCell(y, squareSize);
    }

    public int getSquareSize() {
        return squareSize;
    }
}
