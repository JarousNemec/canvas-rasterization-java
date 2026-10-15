package view;

import raster.Raster;
import raster.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {

    private final Raster raster;

    public Panel(int width, int height) {
        setPreferredSize(new Dimension(width, height));

        raster = new RasterBufferedImage(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(((RasterBufferedImage)raster).getImage(), 0, 0, null);
    }

    public Raster getRaster() {
        return raster;
    }
}
