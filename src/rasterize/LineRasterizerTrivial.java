package rasterize;

import raster.Raster;

import java.awt.*;

public class LineRasterizerTrivial extends LineRasterizer {

    public LineRasterizerTrivial(Raster raster) {
        super(raster);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        //vzdalenosti bodu = delky odvesen
        float dx = x2 - x1;
        float dy = y2 - y1;

        //tangens trojuhelniku uhel u pocatku žejo = tedy protilehla / prilehle = dy / dx
        if (Math.abs(dx) >= Math.abs(dy)) {
            if (x1 > x2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
            float k = dy / dx;
            float q = y1 - k * x1; //y = kx + q -> q = y - kx
            for (int x = x1; x <= x2; x++) {
                int y = (int) (k * x + q);
                raster.setPixel(x, y, color);
            }

        } else {
            if (y1 > y2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
            float k = dx / dy;
            float q = x1 - k * y1;
            for (int y = y1; y <= y2; y++) {
                int x = (int) (k * y + q);
                raster.setPixel(x, y, color);
            }
        }
    }
}
