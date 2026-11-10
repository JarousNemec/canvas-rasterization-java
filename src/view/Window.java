package view;

import controller.tool.ToolType;
import fill.SeedFillMode;
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
    private Consumer<SeedFillMode> onSeedFillModeChangedListener;

    private final JToolBar toolBar = new JToolBar();
    private final JComboBox<String> modeComboBox = new JComboBox<>();
    private final ColorIcon pickLineColorIcon = new ColorIcon(15);
    private final JButton pickLineColorButton = new JButton(" Line color ", pickLineColorIcon);
    private final ColorIcon pickFillColorIcon = new ColorIcon(15);
    private final JButton pickFillColorButton = new JButton(" Fill color ", pickFillColorIcon);
    private final JLabel seedFillModeLabel = new JLabel(" Seed fill mode: ");
    private final JComboBox<String> seedFillModeComboBox = new JComboBox<>();
    private final JCheckBox lineClippingCheckBox = new JCheckBox(" Clip lines ");
    private final JCheckBox polygonClippingCheckBox = new JCheckBox(" Clip polygons ");
    private final JCheckBox clipperVisualizationCheckBox = new JCheckBox(" Show clippers ");

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

        initSeedFillModeComboBox();

        initClipCheckBoxes();

        //init mode selector label
        toolBar.add(new JLabel("Mode: "));

        //init main drawing panel
        canvasPanel = new CanvasPanel(PANEL_WIDTH, PANEL_HEIGHT);

        //add components to the window container
        toolBar.add(modeComboBox);
        toolBar.add(pickLineColorButton);
        toolBar.add(pickFillColorButton);
        toolBar.add(seedFillModeLabel);
        toolBar.add(seedFillModeComboBox);
        toolBar.add(lineClippingCheckBox);
        toolBar.add(polygonClippingCheckBox);
        toolBar.add(clipperVisualizationCheckBox);

        //set minimal toolbar height by adding rigid area
        toolBar.add(Box.createRigidArea(new Dimension(0, pickLineColorButton.getPreferredSize().height)));

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

    private void initSeedFillModeComboBox() {
        seedFillModeComboBox.setFocusable(false);
        seedFillModeComboBox.addItem("Background");
        seedFillModeComboBox.addItem("Border");
        seedFillModeComboBox.addItem("Pattern");
        seedFillModeComboBox.setMaximumSize(seedFillModeComboBox.getPreferredSize());
        seedFillModeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selected = (String) e.getItem();

                if (onSeedFillModeChangedListener != null) {
                    switch (selected) {
                        case "Background":
                            onSeedFillModeChangedListener.accept(SeedFillMode.BACKGROUND);
                            break;
                        case "Border":
                            onSeedFillModeChangedListener.accept(SeedFillMode.BORDER);
                            break;
                        case "Pattern":
                            onSeedFillModeChangedListener.accept(SeedFillMode.PATTERN);
                    }
                }
            }
        });

        //visible only for the fill tool
        setSeedFillModeComboBoxVisible(false);
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

        setClipCheckBoxesVisible(false);
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

    public void setOnSeedFillModeChangedListener(Consumer<SeedFillMode> listener) {
        this.onSeedFillModeChangedListener = listener;
    }

    public void setSeedFillModeComboBoxVisible(boolean visible) {
        this.seedFillModeLabel.setVisible(visible);
        this.seedFillModeComboBox.setVisible(visible);
        updateToolBarLayout();
    }

    public void setSeedFillMode(SeedFillMode mode) {
        switch (mode) {
            case BACKGROUND:
                this.seedFillModeComboBox.setSelectedItem("Background");
                break;
            case BORDER:
                this.seedFillModeComboBox.setSelectedItem("Border");
                break;
            case PATTERN:
                this.seedFillModeComboBox.setSelectedItem("Pattern");
        }
    }

    public void setClipCheckBoxesVisible(boolean visible) {
        this.lineClippingCheckBox.setVisible(visible);
        this.polygonClippingCheckBox.setVisible(visible);
        this.clipperVisualizationCheckBox.setVisible(visible);
        updateToolBarLayout();
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
    public void setFillColorChooserVisible(boolean visible) {
        this.pickFillColorButton.setVisible(visible);
        updateToolBarLayout();
    }
    public void setLineColorChooserVisible(boolean visible) {
        this.pickLineColorButton.setVisible(visible);
        updateToolBarLayout();
    }

    //hidden components take no space in the toolbar, so the layout must be recalculated after every change
    private void updateToolBarLayout() {
        toolBar.revalidate();
        toolBar.repaint();
    }

    public CanvasPanel getPanel() {
        return canvasPanel;
    }
}
