package fill;

import fill.pattern.CheckerboardPattern;
import fill.pattern.SolidPattern;
import model.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raster.RasterBufferedImage;
import utils.ColorUtil;

import static org.junit.jupiter.api.Assertions.*;

class SeedFillerStackImplTest {

    private static final int BORDER = 0xFFFFFFFF;
    private static final int BACKGROUND = ColorUtil.DEFAULT_BACKGROUND_COLOR;
    private static final int RED = 0xFFFF0000;

    private RasterBufferedImage raster;
    private SeedFillerStackImpl filler;

    //raster 20x20 with the border of the square (5, 5) - (14, 14), inside is the background
    @BeforeEach
    void setUp() {
        raster = new RasterBufferedImage(20, 20);
        raster.clear();
        for (int i = 5; i <= 14; i++) {
            raster.setPixel(i, 5, BORDER);
            raster.setPixel(i, 14, BORDER);
            raster.setPixel(5, i, BORDER);
            raster.setPixel(14, i, BORDER);
        }
        filler = new SeedFillerStackImpl(raster);
        filler.setSeed(new Point2D(10, 10));
        filler.setBorderColor(BORDER);
    }

    @Test
    void background_solid_fillsOnlyInside() {
        filler.setMode(SeedFillStopMode.BACKGROUND);
        filler.setPattern(new SolidPattern(RED));

        filler.fill();

        assertEquals(RED, raster.getPixel(6, 6));
        assertEquals(RED, raster.getPixel(13, 13));
        assertEquals(BORDER, raster.getPixel(5, 5));
        assertEquals(BACKGROUND, raster.getPixel(2, 2)); //outside
    }

    @Test
    void background_patternWithBackgroundColor_endsAndFillsOnlyInside() {
        //pattern contains the background color - filled pixels still look like background,
        //without visited pixels the fill would never end
        filler.setMode(SeedFillStopMode.BACKGROUND);
        filler.setPattern(new CheckerboardPattern(1, RED, BACKGROUND));

        filler.fill();

        assertEquals(RED, raster.getPixel(6, 6)); //6 + 6 even -> primary
        assertEquals(BACKGROUND, raster.getPixel(6, 7)); //odd -> secondary
        assertEquals(BACKGROUND, raster.getPixel(2, 2)); //outside is not touched
    }

    @Test
    void border_pattern_fillsWholeInsideWithPattern() {
        filler.setMode(SeedFillStopMode.BORDER);
        filler.setPattern(new CheckerboardPattern(1, RED, 0xFF0000FF));

        filler.fill();

        for (int y = 6; y <= 13; y++) {
            for (int x = 6; x <= 13; x++) {
                int expected = (x + y) % 2 == 0 ? RED : 0xFF0000FF;
                assertEquals(expected, raster.getPixel(x, y), "pixel " + x + ", " + y);
            }
        }
        assertEquals(BACKGROUND, raster.getPixel(2, 2));
    }

    @Test
    void border_seedOnBorder_fillsNothing() {
        filler.setMode(SeedFillStopMode.BORDER);
        filler.setPattern(new SolidPattern(RED));
        filler.setSeed(new Point2D(5, 10));

        filler.fill();

        assertEquals(BACKGROUND, raster.getPixel(10, 10));
    }
}
