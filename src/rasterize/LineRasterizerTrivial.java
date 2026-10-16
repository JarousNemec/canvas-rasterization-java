package rasterize;

import mode.RasterizationMode;
import raster.Raster;

//Implementation of line rasterizer that uses the Trivial (Naive) algorithm
public class LineRasterizerTrivial extends LineRasterizer {

    public LineRasterizerTrivial(Raster raster) {
        super(raster);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2, RasterizationMode mode) {

        //Set color by mode
        switch (mode) {
            case ALERT -> setColor(ALERT_COLOR);
            case PREVIEW -> setColor(PREVIEW_COLOR);
            default -> setColor(defaultColor);
        }

        //Distances between points = lengths of triangle arms
        float dx = x2 - x1;
        float dy = y2 - y1;

        //Check for dividing by zero
        if (dx == 0 && dy == 0) {
            raster.setPixel(x1, y1, color);
            return;
        }

        //Tangent is the opposite side over the hypotenuse. For example dy / dx depending on used quadrant.
        if (Math.abs(dx) >= Math.abs(dy)) {

            //Swap points if one is bigger than the other
            if (x1 > x2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }

            //Calculate the slopes for the slope-intercept form of the equation of a line segment
            float k = dy / dx;
            float q = y1 - k * x1; //y = kx + q -> q = y - kx

            //Iterate though every point and draw it
            for (int x = x1; x <= x2; x++) {
                int y = Math.round(k * x + q);
                raster.setPixel(x, y, color);
            }

        } else {
            //Swap points if one is bigger than the other
            if (y1 > y2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }

            //Calculate the slopes for the slope-intercept form of the equation of a line segment
            float k = dx / dy;
            float q = x1 - k * y1;

            //Iterate though every point and draw it
            for (int y = y1; y <= y2; y++) {
                int x = Math.round(k * y + q);
                raster.setPixel(x, y, color);
            }
        }
        resetColor();
    }
}
