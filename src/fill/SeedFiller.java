package fill;

import model.Point2D;

public interface SeedFiller extends Filler{
    Point2D getSeed();
    void setSeed(Point2D seed);
    int getBackgroundColor();
    void setBackgroundColor(int backgroundColor);
}
