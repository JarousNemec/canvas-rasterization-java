package view.component;

import utils.ColorUtil;

import javax.swing.*;
import java.awt.*;

public class ColorIcon implements Icon {
    private final int size;
    private Color color = Color.LIGHT_GRAY;
    public ColorIcon(int size) {
        this.size = size;
    }

    public void setColor(int color) {
        this.color = new Color(color);
    }

    public Color getColor() {
        return this.color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x, y, size, size);
        g.setColor(Color.DARK_GRAY);        //outline so dark/light colors stay visible
        g.drawRect(x, y, size - 1, size - 1);
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
