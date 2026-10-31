package fill;

public interface Fillable {
    void setFillColor(int color);
    int getFillColor();
    void enableFill();
    void disableFill();
    boolean isFilled();
}
