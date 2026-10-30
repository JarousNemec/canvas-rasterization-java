package view;

import raster.Raster;
import raster.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;

public class CanvasPanel extends JPanel {

    private final Raster raster;


    public CanvasPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));

        raster = new RasterBufferedImage(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //TODO: if the scene becomes mostly static (frequent repaints without raster changes), cache it in a VolatileImage on the GPU and re-upload only when the raster changes
        g.drawImage(((RasterBufferedImage) raster).getImage(), 0, 0, null);
    }

    public Raster getRaster() {
        return raster;
    }
}
