package view;

import controller.tool.ToolType;
import fill.SeedFillMode;
import view.component.ColorIcon;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.nio.file.Path;
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
    private Consumer<Path> onOpenSceneListener;
    private Consumer<Path> onSaveSceneListener;
    private Runnable onLoadDemoSceneListener;

    private static final String SCENE_FILE_EXTENSION = "json";

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

        initFileMenu();

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

    private void initFileMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem openSceneItem = new JMenuItem("Open scene...");
        openSceneItem.addActionListener(_ -> {
            Path path = chooseSceneFileToOpen();
            if (path != null && onOpenSceneListener != null)
                onOpenSceneListener.accept(path);
            canvasPanel.grabFocus();
        });

        JMenuItem saveSceneItem = new JMenuItem("Save scene...");
        saveSceneItem.addActionListener(_ -> {
            Path path = chooseSceneFileToSave();
            if (path != null && onSaveSceneListener != null)
                onSaveSceneListener.accept(path);
            canvasPanel.grabFocus();
        });

        JMenuItem loadDemoSceneItem = new JMenuItem("Load demo scene");
        loadDemoSceneItem.addActionListener(_ -> {
            if (onLoadDemoSceneListener != null)
                onLoadDemoSceneListener.run();
            canvasPanel.grabFocus();
        });

        fileMenu.add(openSceneItem);
        fileMenu.add(saveSceneItem);
        fileMenu.addSeparator();
        fileMenu.add(loadDemoSceneItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private JFileChooser createSceneFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Scene (*.json)", SCENE_FILE_EXTENSION));
        return fileChooser;
    }

    //returns null when the user cancels the dialog
    private Path chooseSceneFileToOpen() {
        JFileChooser fileChooser = createSceneFileChooser();
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return null;
        return fileChooser.getSelectedFile().toPath();
    }

    //returns null when the user cancels the dialog or doesn't want to overwrite the existing file
    private Path chooseSceneFileToSave() {
        JFileChooser fileChooser = createSceneFileChooser();
        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
            return null;

        File file = fileChooser.getSelectedFile();
        //user usually writes only the name, so the extension is added
        if (!file.getName().toLowerCase().endsWith("." + SCENE_FILE_EXTENSION))
            file = new File(file.getParentFile(), file.getName() + "." + SCENE_FILE_EXTENSION);

        if (file.exists()) {
            int result = JOptionPane.showConfirmDialog(this, "File " + file.getName() + " already exists. Overwrite it?", "Save scene", JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION)
                return null;
        }
        return file.toPath();
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

    public void setOnOpenSceneListener(Consumer<Path> listener) {
        this.onOpenSceneListener = listener;
    }

    public void setOnSaveSceneListener(Consumer<Path> listener) {
        this.onSaveSceneListener = listener;
    }

    public void setOnLoadDemoSceneListener(Runnable listener) {
        this.onLoadDemoSceneListener = listener;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
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

    //used after loading a scene, check boxes fire their listeners, so the clip context gets the same values
    public void setClipCheckBoxes(boolean lineClipping, boolean polygonClipping, boolean clipperVisualization) {
        lineClippingCheckBox.setSelected(lineClipping);
        polygonClippingCheckBox.setSelected(polygonClipping);
        clipperVisualizationCheckBox.setSelected(clipperVisualization);
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
