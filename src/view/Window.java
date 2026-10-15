package view;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private final Panel panel;

    public Window(int width, int height, String title) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(title);
        setVisible(true);

        panel = new Panel(width, height);
        add(panel);
        pack();

        panel.setFocusable(true);
        panel.grabFocus();
    }

    public Panel getPanel() {
        return panel;
    }
}
