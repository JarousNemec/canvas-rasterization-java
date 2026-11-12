package fill.pattern;

//what the area is filled with - one color or one of the patterns
//independent of SeedFillStopMode, which says where the seed fill stops
public enum FillerMode {
    SOLID, CHECKERBOARD, STRIPES, DIAGONAL_STRIPES;

    //size of squares / stripes in px, patterns are not editable, so it is the same for all of them
    private static final int PATTERN_SIZE = 8;

    //secondary color is not used by SOLID
    public PatternFill createPattern(int primaryColor, int secondaryColor) {
        return switch (this) {
            case SOLID -> new SolidPattern(primaryColor);
            case CHECKERBOARD -> new CheckerboardPattern(PATTERN_SIZE, primaryColor, secondaryColor);
            case STRIPES -> new StripesPattern(PATTERN_SIZE, primaryColor, secondaryColor);
            case DIAGONAL_STRIPES -> new DiagonalStripesPattern(PATTERN_SIZE, primaryColor, secondaryColor);
        };
    }
}
