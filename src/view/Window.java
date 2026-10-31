package view;

import controller.UserMode;
import view.component.ColorIcon;

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
    private Consumer<Integer> onLineColorChangedListener;
    private Consumer<Integer> onFillColorChangedListener;

    private final JToolBar toolBar = new JToolBar();
    private final JComboBox<String> modeComboBox = new JComboBox<String>();
    private final ColorIcon pickLineColorIcon = new ColorIcon(15);
    private final JButton pickLineColorButton = new JButton("Line color", pickLineColorIcon);
    private final ColorIcon pickFillColorIcon = new ColorIcon(15);
    private final JButton pickFillColorButton = new JButton("Fill color", pickFillColorIcon);

    public Window() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(TITLE);
        setVisible(true);
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        //init toolbar
        toolBar.setFloatable(false);

        initUserModeCombobox();

        initColorChoosers();

        //init mode selector label
        toolBar.add(new JLabel("Mode: "));

        //init main drawing panel
        canvasPanel = new CanvasPanel(PANEL_WIDTH, PANEL_HEIGHT);

        //add components to the window container
        toolBar.add(modeComboBox);
        toolBar.add(pickLineColorButton);
        toolBar.add(pickFillColorButton);
        add(toolBar, BorderLayout.NORTH);
        add(canvasPanel);

        //set ideal window size
        pack();

        //set focus on the drawing panel
        canvasPanel.setFocusable(true);
        canvasPanel.grabFocus();
    }

    private void initColorChoosers() {
        //init line color chooser
        pickLineColorButton.setFocusable(false);
        pickLineColorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(Window.this, "Pick line color", pickLineColorIcon.getColor(), false);
            if (color == null)
                return;
            int currentColor = color.getRGB();
            pickLineColorIcon.setColor(currentColor);
            pickLineColorButton.repaint();
            if (onLineColorChangedListener != null)
                onLineColorChangedListener.accept(currentColor);
        });

        //init fill color chooser
        pickFillColorButton.setFocusable(false);
        pickFillColorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(Window.this, "Pick fill color", pickFillColorIcon.getColor(), false);
            if (color == null)
                return;
            int currentColor = color.getRGB();
            pickFillColorIcon.setColor(currentColor);
            pickFillColorButton.repaint();
            if (onFillColorChangedListener != null)
                onFillColorChangedListener.accept(currentColor);
        });
    }

    private void initUserModeCombobox() {
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
    }

    public void setOnModeChanged(Consumer<UserMode> listener) {
        this.onModeChangedListener = listener;
    }

    public void setOnLineColorChanged(Consumer<Integer> listener) {
        this.onLineColorChangedListener = listener;
    }

    public void setOnFillColorChanged(Consumer<Integer> listener) {
        this.onFillColorChangedListener = listener;
    }

    public void setLineColor(int color) {
        this.pickLineColorIcon.setColor(color);
    }
    public void setFillColor(int color) {
        this.pickFillColorIcon.setColor(color);
    }
    public void setFillColorChooserEnabled(boolean enable) {
        this.pickFillColorButton.setEnabled(enable);
    }

    public CanvasPanel getPanel() {
        return canvasPanel;
    }
}
