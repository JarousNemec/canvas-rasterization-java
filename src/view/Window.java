package view;

import controller.tool.ToolType;
import fill.SeedFillStopMode;
import fill.pattern.FillerMode;
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
    private Consumer<Integer> onPatternColorChangedListener;
    private Consumer<Boolean> onLineClippingChangedListener;
    private Consumer<Boolean> onPolygonClippingChangedListener;
    private Consumer<Boolean> onClipperVisualizationChangedListener;
    private Consumer<SeedFillStopMode> onSeedFillModeChangedListener;
    private Consumer<FillerMode> onFillerModeChangedListener;
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
    //second color of the pattern, fill color is the first one
    private final ColorIcon pickPatternColorIcon = new ColorIcon(15);
    private final JButton pickPatternColorButton = new JButton(" Pattern color ", pickPatternColorIcon);
    private final JLabel seedFillModeLabel = new JLabel(" Seed fill mode: ");
    private final JComboBox<String> seedFillModeComboBox = new JComboBox<>();
    private final JLabel fillerModeLabel = new JLabel(" Filler mode: ");
    private final JComboBox<String> fillerModeComboBox = new JComboBox<>();
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

        initFillerModeComboBox();

        initClipCheckBoxes();

        //init mode selector label
        toolBar.add(new JLabel("Mode: "));

        //init main drawing panel
        canvasPanel = new CanvasPanel(PANEL_WIDTH, PANEL_HEIGHT);

        //add components to the window container
        toolBar.add(modeComboBox);
        toolBar.add(pickLineColorButton);
        toolBar.add(seedFillModeLabel);
        toolBar.add(seedFillModeComboBox);
        //filler mode is followed by its colors
        toolBar.add(fillerModeLabel);
        toolBar.add(fillerModeComboBox);
        toolBar.add(pickFillColorButton);
        toolBar.add(pickPatternColorButton);
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
        openSceneItem.addActionListener(e -> {
            Path path = chooseSceneFileToOpen();
            if (path != null && onOpenSceneListener != null)
                onOpenSceneListener.accept(path);
            canvasPanel.grabFocus();
        });

        JMenuItem saveSceneItem = new JMenuItem("Save scene...");
        saveSceneItem.addActionListener(e -> {
            Path path = chooseSceneFileToSave();
            if (path != null && onSaveSceneListener != null)
                onSaveSceneListener.accept(path);
            canvasPanel.grabFocus();
        });

        JMenuItem loadDemoSceneItem = new JMenuItem("Load demo scene");
        loadDemoSceneItem.addActionListener(e -> {
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
        //listeners are set later by the controller, so they are read in the moment the color is picked
        initColorChooser(pickLineColorButton, pickLineColorIcon, "Pick line color", color -> {
            if (onLineColorChangedListener != null)
                onLineColorChangedListener.accept(color);
        });
        initColorChooser(pickFillColorButton, pickFillColorIcon, "Pick fill color", color -> {
            if (onFillColorChangedListener != null)
                onFillColorChangedListener.accept(color);
        });
        initColorChooser(pickPatternColorButton, pickPatternColorIcon, "Pick pattern color", color -> {
            if (onPatternColorChangedListener != null)
                onPatternColorChangedListener.accept(color);
        });

        //visible only when a pattern is selected
        setPatternColorChooserVisible(false);
    }

    //same behavior for all color buttons - open the dialog, update the icon and notify the listener
    private void initColorChooser(JButton button, ColorIcon icon, String title, Consumer<Integer> onColorPicked) {
        button.setFocusable(false);
        button.addActionListener(e -> {
            Color color = JColorChooser.showDialog(Window.this, title, icon.getColor(), false);
            if (color == null)
                return;
            int currentColor = color.getRGB();
            icon.setColor(currentColor);
            button.repaint();
            onColorPicked.accept(currentColor);
        });
    }

    private void initSeedFillModeComboBox() {
        seedFillModeComboBox.setFocusable(false);
        seedFillModeComboBox.addItem("Background");
        seedFillModeComboBox.addItem("Border");
        seedFillModeComboBox.setMaximumSize(seedFillModeComboBox.getPreferredSize());
        seedFillModeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selected = (String) e.getItem();

                if (onSeedFillModeChangedListener != null) {
                    switch (selected) {
                        case "Background":
                            onSeedFillModeChangedListener.accept(SeedFillStopMode.BACKGROUND);
                            break;
                        case "Border":
                            onSeedFillModeChangedListener.accept(SeedFillStopMode.BORDER);
                    }
                }
            }
        });

        //visible only for the fill tool
        setSeedFillModeComboBoxVisible(false);
    }

    private void initFillerModeComboBox() {
        fillerModeComboBox.setFocusable(false);
        fillerModeComboBox.addItem("Solid");
        fillerModeComboBox.addItem("Checkerboard");
        fillerModeComboBox.addItem("Stripes");
        fillerModeComboBox.addItem("Diagonal stripes");
        fillerModeComboBox.setMaximumSize(fillerModeComboBox.getPreferredSize());
        fillerModeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selected = (String) e.getItem();

                if (onFillerModeChangedListener != null) {
                    switch (selected) {
                        case "Solid":
                            onFillerModeChangedListener.accept(FillerMode.SOLID);
                            break;
                        case "Checkerboard":
                            onFillerModeChangedListener.accept(FillerMode.CHECKERBOARD);
                            break;
                        case "Stripes":
                            onFillerModeChangedListener.accept(FillerMode.STRIPES);
                            break;
                        case "Diagonal stripes":
                            onFillerModeChangedListener.accept(FillerMode.DIAGONAL_STRIPES);
                    }
                }
            }
        });

        //visible only for the tools that fill something
        setFillerModeComboBoxVisible(false);
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

    public void setOnPatternColorChangedListener(Consumer<Integer> listener) {
        this.onPatternColorChangedListener = listener;
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

    public void setOnSeedFillModeChangedListener(Consumer<SeedFillStopMode> listener) {
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

    public void setOnFillerModeChangedListener(Consumer<FillerMode> listener) {
        this.onFillerModeChangedListener = listener;
    }

    public void setFillerModeComboBoxVisible(boolean visible) {
        this.fillerModeLabel.setVisible(visible);
        this.fillerModeComboBox.setVisible(visible);
        updateToolBarLayout();
    }

    public void setFillerMode(FillerMode mode) {
        switch (mode) {
            case SOLID:
                this.fillerModeComboBox.setSelectedItem("Solid");
                break;
            case CHECKERBOARD:
                this.fillerModeComboBox.setSelectedItem("Checkerboard");
                break;
            case STRIPES:
                this.fillerModeComboBox.setSelectedItem("Stripes");
                break;
            case DIAGONAL_STRIPES:
                this.fillerModeComboBox.setSelectedItem("Diagonal stripes");
        }
    }

    public void setSeedFillModeComboBoxVisible(boolean visible) {
        this.seedFillModeLabel.setVisible(visible);
        this.seedFillModeComboBox.setVisible(visible);
        updateToolBarLayout();
    }

    public void setSeedFillMode(SeedFillStopMode mode) {
        switch (mode) {
            case BACKGROUND:
                this.seedFillModeComboBox.setSelectedItem("Background");
                break;
            case BORDER:
                this.seedFillModeComboBox.setSelectedItem("Border");
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
    public void setPatternColor(int color) {
        this.pickPatternColorIcon.setColor(color);
        this.pickPatternColorButton.repaint();
    }
    public void setPatternColorChooserVisible(boolean visible) {
        this.pickPatternColorButton.setVisible(visible);
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
