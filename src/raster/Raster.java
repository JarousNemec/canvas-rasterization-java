package raster;

public interface Raster {
    void setPixel(int x, int y, int color);
    int getWidth();
    int getHeight();
    void clear();
    int getPixel(int x, int y);
    boolean contains(int x, int y);
}
