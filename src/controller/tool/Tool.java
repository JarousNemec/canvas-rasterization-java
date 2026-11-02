package controller.tool;

import rasterize.RasterizationContext;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface Tool {
    void mousePressed(MouseEvent e);
    void mouseReleased(MouseEvent e);
    void mouseDragged(MouseEvent e);
    void keyPressed(KeyEvent e);
    void keyReleased(KeyEvent e);
    void drawPreview(RasterizationContext context);
    void updateByContext();
}
