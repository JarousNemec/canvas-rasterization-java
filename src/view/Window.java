package view;

import controller.tool.ToolType;
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

    private Consumer<ToolType> onModeChangedListener;
    private Consumer<Integer> onLineColorChangedListener;
    private Consumer<Integer> onFillColorChangedListener;
    private Consumer<Boolean> onLineClippingChangedListener;
    private Consumer<Boolean> onPolygonClippingChangedListener;
    private Consumer<Boolean> onClipperVisualizationChangedListener;

    private final JToolBar toolBar = new JToolBar();
    private final JComboBox<String> modeComboBox = new JComboBox<>();
    private final ColorIcon pickLineColorIcon = new ColorIcon(15);
    private final JButton pickLineColorButton = new JButton("Line color", pickLineColorIcon);
    private final ColorIcon pickFillColorIcon = new ColorIcon(15);
    private final JButton pickFillColorButton = new JButton("Fill color", pickFillColorIcon);
    private final JCheckBox lineClippingCheckBox = new JCheckBox("Clip lines");
    private final JCheckBox polygonClippingCheckBox = new JCheckBox("Clip polygons");
    private final JCheckBox clipperVisualizationCheckBox = new JCheckBox("Show clippers");

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

        initClipCheckBoxes();

        //init mode selector label
        toolBar.add(new JLabel("Mode: "));

        //init main drawing panel
        canvasPanel = new CanvasPanel(PANEL_WIDTH, PANEL_HEIGHT);

        //add components to the window container
        toolBar.add(modeComboBox);
        toolBar.add(pickLineColorButton);
        toolBar.add(pickFillColorButton);
        toolBar.add(lineClippingCheckBox);
        toolBar.add(polygonClippingCheckBox);
        toolBar.add(clipperVisualizationCheckBox);
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
        pickLineColorButton.addActionListener(_ -> {
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
        pickFillColorButton.addActionListener(_ -> {
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

    private void initClipCheckBoxes() {
        lineClippingCheckBox.setFocusable(false);
        lineClippingCheckBox.addItemListener(e -> {
            if (onLineClippingChangedListener != null)
                onLineClippingChangedListener.accept(e.getStateChange() == ItemEvent.SELECTED);
        });

        polygonClippingCheckBox.setFocusable(false);
        polygonClippingCheckBox.addItemListener(e -> {
            if (onPolygonClippingChangedListener != null)
                onPolygonClippingChangedListener.accept(e.getStateChange() == ItemEvent.SELECTED);
        });

        clipperVisualizationCheckBox.setFocusable(false);
        clipperVisualizationCheckBox.addItemListener(e -> {
            if (onClipperVisualizationChangedListener != null)
                onClipperVisualizationChangedListener.accept(e.getStateChange() == ItemEvent.SELECTED);
        });

        setClipCheckBoxesEnabled(false);
    }

    private void initUserModeCombobox() {
        modeComboBox.setFocusable(false);
        modeComboBox.addItem("Line");
        modeComboBox.addItem("Polygon");
        modeComboBox.addItem("Vertex");
        modeComboBox.addItem("Select");
        modeComboBox.addItem("Fill");
        modeComboBox.addItem("Clip");
        modeComboBox.setMaximumSize(modeComboBox.getPreferredSize());
        modeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selected = (String) e.getItem();

                if (onModeChangedListener != null) {
                    switch (selected) {
                        case "Line":
                            onModeChangedListener.accept(ToolType.LINE);
                            break;
                        case "Polygon":
                            onModeChangedListener.accept(ToolType.POLYGON);
                            break;
                        case "Fill":
                            onModeChangedListener.accept(ToolType.FILL);
                            break;
                        case "Vertex":
                            onModeChangedListener.accept(ToolType.VERTEX);
                            break;
                        case "Select":
                            onModeChangedListener.accept(ToolType.SELECT);
                            break;
                        case "Clip":
                            onModeChangedListener.accept(ToolType.CLIP);
                    }
                }
            }
        });
    }

    public void setOnToolChangedListener(Consumer<ToolType> listener) {
        this.onModeChangedListener = listener;
    }

    public void setOnLineColorChangedListener(Consumer<Integer> listener) {
        this.onLineColorChangedListener = listener;
    }

    public void setOnFillColorChangedListener(Consumer<Integer> listener) {
        this.onFillColorChangedListener = listener;
    }

    public void setOnLineClippingChangedListener(Consumer<Boolean> listener) {
        this.onLineClippingChangedListener = listener;
    }

    public void setOnPolygonClippingChangedListener(Consumer<Boolean> listener) {
        this.onPolygonClippingChangedListener = listener;
    }

    public void setOnClipperVisualizationChangedListener(Consumer<Boolean> listener) {
        this.onClipperVisualizationChangedListener = listener;
    }

    public void setClipCheckBoxesEnabled(boolean enable) {
        this.lineClippingCheckBox.setEnabled(enable);
        this.polygonClippingCheckBox.setEnabled(enable);
        this.clipperVisualizationCheckBox.setEnabled(enable);
    }

    public void resetClipCheckBoxes() {
        lineClippingCheckBox.setSelected(false);
        polygonClippingCheckBox.setSelected(false);
        clipperVisualizationCheckBox.setSelected(false);
    }

    public void setLineColor(int color) {
        this.pickLineColorIcon.setColor(color);
        this.pickLineColorButton.repaint();
    }
    public void setFillColor(int color) {
        this.pickFillColorIcon.setColor(color);
        this.pickFillColorButton.repaint();
    }
    public void setFillColorChooserEnabled(boolean enable) {
        this.pickFillColorButton.setEnabled(enable);
        this.repaint();
    }
    public void setLineColorChooserEnabled(boolean enable) {
        this.pickLineColorButton.setEnabled(enable);
        this.repaint();
    }

    public CanvasPanel getPanel() {
        return canvasPanel;
    }
}
