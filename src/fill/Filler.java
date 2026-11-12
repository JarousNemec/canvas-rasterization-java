package fill;

import fill.pattern.PatternFill;

public interface Filler {
    void fill();
    //every filled pixel gets its color from the pattern, one color is SolidPattern
    void setPattern(PatternFill pattern);
    PatternFill getPattern();
}
