package fill;

import fill.pattern.CheckerboardPattern;
import fill.pattern.DiagonalStripesPattern;
import fill.pattern.FillerMode;
import fill.pattern.PatternFill;
import fill.pattern.SolidPattern;
import fill.pattern.StripesPattern;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatternTest {

    private static final int A = 0xFFFFFFFF; //primary color
    private static final int B = 0xFF000000; //secondary color

    // solid

    @Test
    void solid_isSameColorEverywhere() {
        PatternFill pattern = new SolidPattern(A);

        assertEquals(A, pattern.getPixelForPoint(0, 0));
        assertEquals(A, pattern.getPixelForPoint(123, 45));
        assertEquals(A, pattern.getSecondaryColor());
    }

    // filler mode

    @Test
    void fillerMode_createPattern_returnsPatternOfThatMode() {
        for (FillerMode mode : FillerMode.values()) {
            PatternFill pattern = mode.createPattern(A, B);

            assertEquals(mode, pattern.getFillerMode());
            assertEquals(A, pattern.getPrimaryColor());
        }
    }

    // checkerboard

    @Test
    void checkerboard_firstSquares_alternateInRowAndColumn() {
        PatternFill pattern = new CheckerboardPattern(10, A, B);

        assertEquals(A, pattern.getPixelForPoint(0, 0));
        assertEquals(A, pattern.getPixelForPoint(9, 9)); //last pixel of the first square
        assertEquals(B, pattern.getPixelForPoint(10, 0)); //next square in the row
        assertEquals(B, pattern.getPixelForPoint(0, 10)); //next square in the column
        assertEquals(A, pattern.getPixelForPoint(10, 10)); //diagonal square has the same color
    }

    // vertical stripes

    @Test
    void stripes_dependOnlyOnX() {
        PatternFill pattern = new StripesPattern(4, A, B);

        assertEquals(A, pattern.getPixelForPoint(3, 0));
        assertEquals(B, pattern.getPixelForPoint(4, 0));
        assertEquals(A, pattern.getPixelForPoint(8, 0));
        //same column, any row -> same color
        assertEquals(B, pattern.getPixelForPoint(4, 123));
    }

    // diagonal stripes

    @Test
    void diagonalStripes_areShiftedByOnePixelEveryRow() {
        PatternFill pattern = new DiagonalStripesPattern(4, A, B);

        assertEquals(B, pattern.getPixelForPoint(4, 0));
        //one row down the stripe border moves one pixel to the left
        assertEquals(B, pattern.getPixelForPoint(3, 1));
        assertEquals(A, pattern.getPixelForPoint(2, 1));
    }

    // common

    @Test
    void negativeCoordinates_continueThePattern() {
        PatternFill pattern = new StripesPattern(4, A, B);

        //stripe [-4, -1] is right before stripe [0, 3], so it has the other color
        assertEquals(B, pattern.getPixelForPoint(-1, 0));
        assertEquals(B, pattern.getPixelForPoint(-4, 0));
        assertEquals(A, pattern.getPixelForPoint(-5, 0));
    }

    @Test
    void sizeNotPositive_throws() {
        assertThrows(IllegalArgumentException.class, () -> new CheckerboardPattern(0, A, B));
        assertThrows(IllegalArgumentException.class, () -> new StripesPattern(-1, A, B));
        assertThrows(IllegalArgumentException.class, () -> new DiagonalStripesPattern(0, A, B));
    }
}
