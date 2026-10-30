package raster;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class RasterBufferedImage implements Raster {
    private final BufferedImage image;
    private final int[] pixels;
    private final int width;
    private final int height;

    public RasterBufferedImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        this.width = width;
        this.height = height;
    }

    @Override
    public void setPixel(int x, int y, int color) {
        if (contains(x, y))
            pixels[y * width + x] = color;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void clear() {
        Graphics g = image.getGraphics();
        g.clearRect(0, 0, width, height);
        g.dispose();
    }

    @Override
    public int getPixel(int x, int y) {
        return pixels[y * width + x];
    }

    @Override
    public boolean contains(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public BufferedImage getImage() {
        return image;
    }
}
