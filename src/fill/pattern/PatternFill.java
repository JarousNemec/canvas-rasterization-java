package fill.pattern;

//rule that gives the color of every pixel of the filled area
//fillers only ask for the color of the pixel, they don't know which pattern they fill with
public interface PatternFill {
    int getPixelForPoint(int x, int y);

    //getters below are for reading the pattern back - to the UI when an object is selected and to the scene file
    FillerMode getFillerMode();

    int getPrimaryColor();

    int getSecondaryColor();
}
