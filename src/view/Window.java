package view;

import constants.DrawingMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.function.Consumer;

public class Window extends JFrame {

    private Panel panel;
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final String TITLE = "Canvas Rasterization";

    private Consumer<DrawingMode> onModeChangedListener;

    public Window() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(TITLE);
        setVisible(true);
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        //init toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        //init combobox for mode selection
        var modeComboBox = new JComboBox<String>();
        modeComboBox.addItem("Line");
        modeComboBox.addItem("Polygon");
        modeComboBox.setMaximumSize(modeComboBox.getPreferredSize());
        modeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selected = (String) e.getItem();

                //todo: do the parsing more general
                DrawingMode newMode = "Polygon".equals(selected) ? DrawingMode.POLYGON : DrawingMode.LINE;

                if (onModeChangedListener != null) {
                    onModeChangedListener.accept(newMode);
                }
            }
        });

        //init mode selector label
        toolBar.add(new JLabel("Mode: "));

        //init main drawing panel
        panel = new Panel(PANEL_WIDTH, PANEL_HEIGHT);

        //add components to the window container
        toolBar.add(modeComboBox);
        add(toolBar, BorderLayout.NORTH);
        add(panel);

        //set ideal window size
        pack();

        //set focus on the drawing panel
        panel.setFocusable(true);
        panel.grabFocus();
    }
    public void setOnModeChanged(Consumer<DrawingMode> listener) {
        this.onModeChangedListener = listener;
    }

    public Panel getPanel() {
        return panel;
    }
}
