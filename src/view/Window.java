package view;

import constants.DrawingMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.function.Consumer;

public class Window extends JFrame {

    private CanvasPanel canvasPanel;
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
        modeComboBox.addItem("Filler");
        modeComboBox.setMaximumSize(modeComboBox.getPreferredSize());
        modeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selected = (String) e.getItem();

                if (onModeChangedListener != null) {
                    switch (selected){
                        case "Line":
                            onModeChangedListener.accept(DrawingMode.LINE);
                            break;
                        case "Polygon":
                            onModeChangedListener.accept(DrawingMode.POLYGON);
                            break;
                        case "Filler":
                            onModeChangedListener.accept(DrawingMode.FILLER);
                            break;
                    }
                }
            }
        });

        //init mode selector label
        toolBar.add(new JLabel("Mode: "));

        //init main drawing panel
        canvasPanel = new CanvasPanel(PANEL_WIDTH, PANEL_HEIGHT);

        //add components to the window container
        toolBar.add(modeComboBox);
        add(toolBar, BorderLayout.NORTH);
        add(canvasPanel);

        //set ideal window size
        pack();

        //set focus on the drawing panel
        canvasPanel.setFocusable(true);
        canvasPanel.grabFocus();
    }
    public void setOnModeChanged(Consumer<DrawingMode> listener) {
        this.onModeChangedListener = listener;
    }

    public CanvasPanel getPanel() {
        return canvasPanel;
    }
}
