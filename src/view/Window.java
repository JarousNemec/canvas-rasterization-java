package view;

import controller.UserMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.function.Consumer;

public class Window extends JFrame {

    private CanvasPanel canvasPanel;
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final String TITLE = "Canvas Rasterization";

    private Consumer<UserMode> onModeChangedListener;

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
        modeComboBox.setFocusable(false);
        modeComboBox.addItem("Line");
        modeComboBox.addItem("Polygon");
        modeComboBox.addItem("Point");
        modeComboBox.addItem("Select");
        modeComboBox.addItem("Fill");
        modeComboBox.setMaximumSize(modeComboBox.getPreferredSize());
        modeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selected = (String) e.getItem();

                if (onModeChangedListener != null) {
                    switch (selected) {
                        case "Line":
                            onModeChangedListener.accept(UserMode.LINE);
                            break;
                        case "Polygon":
                            onModeChangedListener.accept(UserMode.POLYGON);
                            break;
                        case "Fill":
                            onModeChangedListener.accept(UserMode.FILLER);
                            break;
                        case "Point":
                            onModeChangedListener.accept(UserMode.POINT);
                            break;
                        case "Select":
                            onModeChangedListener.accept(UserMode.SELECT);
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

    public void setOnModeChanged(Consumer<UserMode> listener) {
        this.onModeChangedListener = listener;
    }

    public CanvasPanel getPanel() {
        return canvasPanel;
    }
}
