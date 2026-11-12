package fill;

import fill.pattern.PatternFill;

public interface Fillable {
    void setFillPattern(PatternFill pattern);
    PatternFill getFillPattern();
    void enableFill();
    void disableFill();
    boolean isFilled();
}
