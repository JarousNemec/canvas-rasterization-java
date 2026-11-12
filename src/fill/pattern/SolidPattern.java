package fill.pattern;

//one color everywhere - the classic fill, so fillers don't need a special branch for it
public class SolidPattern implements PatternFill {
    private final int color;

    public SolidPattern(int color) {
        this.color = color;
    }

    @Override
    public int getPixelForPoint(int x, int y) {
        return color;
    }

    @Override
    public FillerMode getFillerMode() {
        return FillerMode.SOLID;
    }

    @Override
    public int getPrimaryColor() {
        return color;
    }

    @Override
    public int getSecondaryColor() {
        return color;
    }
}
