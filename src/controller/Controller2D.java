package controller;

import clip.ClipContext;
import clip.Clippable;
import controller.tool.*;
import fill.ScanLineFiller;
import rasterize.*;
import fill.SeedFillStopMode;
import fill.pattern.FillerMode;
import fill.pattern.PatternFill;
import fill.SeedFillerStackImpl;
import model.*;
import persistence.SceneFileStorage;
import persistence.SceneFormatException;
import persistence.SceneSnapshot;
import utils.ColorUtil;
import utils.Logger;
import view.CanvasPanel;
import view.Window;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class Controller2D {
    private final Window window;
    private final CanvasPanel canvasPanel;

    // Rasterizers
    private final LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;

    //Filler
    private final SeedFillerStackImpl seedFiller;
    private final ScanLineFiller scanLineFiller;

    //Rasterization Context containing all rasterizers
    private final RasterizationContext rasterizationContext;

    private ToolContext toolContext;

    private ClipContext clipContext;

    //Model that contains all rasterizables and allows us to render them all in one cycle in the order they were added
    private final CanvasModel canvasModel;

    private final SceneFileStorage sceneFileStorage = new SceneFileStorage();

    private ToolType activeToolType = ToolType.LINE;
    private EditorTool activeTool = null;

    public Controller2D(Window window) {
        this.window = window;
        this.clipContext = new ClipContext();
        this.canvasPanel = this.window.getPanel();

        lineRasterizer = new LineRasterizerTrivial(canvasPanel.getRaster());
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);

        seedFiller = new SeedFillerStackImpl(canvasPanel.getRaster());
        scanLineFiller = new ScanLineFiller(canvasPanel.getRaster());

        rasterizationContext = new RasterizationContext(lineRasterizer, polygonRasterizer, seedFiller, scanLineFiller, new VertexMarkerRasterizer(canvasPanel.getRaster()));

        canvasModel = new CanvasModel();
        setActiveTool();
        initWindowEvents();
        initListeners();
        initKeyBindings();

        drawScene();
    }

    private void initToolContextEvents() {
        toolContext.setOnFillPatternChangedByTool(this::showFillPattern);
        toolContext.setOnLineColorChangedByTool(this.window::setLineColor);
    }

    //selected object's pattern shown in the UI, values are already in the tool context
    private void showFillPattern(PatternFill pattern) {
        window.setFillColor(toolContext.getCurrentFillColor());
        window.setPatternColor(toolContext.getCurrentPatternColor());
        //fires the combobox listener -> updateFillerMode
        window.setFillerMode(toolContext.getCurrentFillerMode());
        window.setPatternColorChooserVisible(isPatternColorChooserVisible());
    }

    private void initWindowEvents() {
        this.window.setOnToolChangedListener(this::setActiveToolType);
        this.window.setOnFillColorChangedListener(this::updateFillColor);
        this.window.setOnPatternColorChangedListener(this::updatePatternColor);
        this.window.setOnLineColorChangedListener(this::updateLineColor);
        this.window.setOnLineClippingChangedListener(this::updateLineClipping);
        this.window.setOnPolygonClippingChangedListener(this::updatePolygonClipping);
        this.window.setOnClipperVisualizationChangedListener(this::updateClipperVisualization);
        this.window.setOnSeedFillModeChangedListener(this::updateSeedFillMode);
        this.window.setOnFillerModeChangedListener(this::updateFillerMode);
        this.window.setOnOpenSceneListener(this::openScene);
        this.window.setOnSaveSceneListener(this::saveScene);
        this.window.setOnLoadDemoSceneListener(this::loadDemoScene);
    }

    private void updateSeedFillMode(SeedFillStopMode mode) {
        toolContext.setCurrentSeedFillMode(mode);
        //line color is the border color, so it can be picked only for the border seed fill
        window.setLineColorChooserVisible(isLineColorChooserVisible());
    }

    private void updateFillerMode(FillerMode mode) {
        toolContext.setCurrentFillerMode(mode);
        window.setPatternColorChooserVisible(isPatternColorChooserVisible());
        //same as colors - the polygon in progress / selected object gets the new pattern right away
        activeTool.updateByContext();
        drawScene();
    }

    private boolean isLineColorChooserVisible() {
        return switch (activeToolType) {
            case LINE, POLYGON, SELECT -> true;
            case FILL -> toolContext.getCurrentSeedFillMode() == SeedFillStopMode.BORDER;
            case VERTEX, CLIP -> false;
        };
    }

    //filler mode and fill color make sense only for the tools that fill something
    private boolean isFillingTool() {
        return switch (activeToolType) {
            case POLYGON, FILL, SELECT -> true;
            case LINE, VERTEX, CLIP -> false;
        };
    }

    //pattern has two colors - fill color and pattern color, solid fill uses only the fill color
    private boolean isPatternColorChooserVisible() {
        return isFillingTool() && toolContext.getCurrentFillerMode() != FillerMode.SOLID;
    }

    private void updateLineClipping(boolean enabled) {
        clipContext.setLineClippingEnabled(enabled);
        drawScene();
    }

    private void updatePolygonClipping(boolean enabled) {
        clipContext.setPolygonClippingEnabled(enabled);
        drawScene();
    }

    private void updateClipperVisualization(boolean enabled) {
        clipContext.setClipperVisualizationEnabled(enabled);
        drawScene();
    }

    private void updateLineColor(int color) {
        toolContext.setCurrentLineColor(color);
        activeTool.updateByContext();
        drawScene();
    }

    private void updateFillColor(int color) {
        toolContext.setCurrentFillColor(color);
        activeTool.updateByContext();
        drawScene();
    }

    private void updatePatternColor(int color) {
        toolContext.setCurrentPatternColor(color);
        activeTool.updateByContext();
        drawScene();
    }

    private void setActiveToolType(ToolType activeToolType) {
        Logger.info("Drawing mode changing from: " + this.activeToolType + " to: " + activeToolType);
        this.activeToolType = activeToolType;
        setActiveTool();
        drawScene();
    }

    private void setActiveTool() {
        //seed fill mode is a setting of the user, not of the tool, so it is kept when the tool (and its context) changes
        SeedFillStopMode seedFillMode = toolContext != null ? toolContext.getCurrentSeedFillMode() : SeedFillStopMode.BACKGROUND;
        //same for the filler mode
        FillerMode fillerMode = toolContext != null ? toolContext.getCurrentFillerMode() : FillerMode.SOLID;
        toolContext = new ToolContext(canvasModel, canvasPanel.getRaster().getHeight(), canvasPanel.getRaster().getWidth());
        toolContext.setCurrentSeedFillMode(seedFillMode);
        toolContext.setCurrentFillerMode(fillerMode);
        initToolContextEvents();
        switch (this.activeToolType) {
            case LINE:
                activeTool = new LineTool(toolContext);
                break;
            case POLYGON:
                activeTool = new PolygonTool(toolContext);
                break;
            case FILL:
                activeTool = new FillerTool(toolContext);
                break;
            case VERTEX:
                activeTool = new VertexTool(toolContext);
                break;
            case SELECT:
                activeTool = new SelectTool(toolContext);
                break;
            case CLIP:
                activeTool = new ClipTool(toolContext, clipContext);
                break;
        }
        window.setFillColorChooserVisible(isFillingTool());
        window.setPatternColorChooserVisible(isPatternColorChooserVisible());
        window.setLineColorChooserVisible(isLineColorChooserVisible());
        window.setClipCheckBoxesVisible(this.activeToolType == ToolType.CLIP);
        window.setSeedFillModeComboBoxVisible(this.activeToolType == ToolType.FILL);
        window.setSeedFillMode(toolContext.getCurrentSeedFillMode());
        window.setFillerModeComboBoxVisible(isFillingTool());
        window.setFillerMode(toolContext.getCurrentFillerMode());
        window.setLineColor(toolContext.getCurrentLineColor());
        window.setFillColor(toolContext.getCurrentFillColor());
        window.setPatternColor(toolContext.getCurrentPatternColor());
    }

    private void initKeyBindings() {
        //keybindings for CLEAR action
        canvasPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('c'), "clearAction");
        canvasPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('C'), "clearAction");

        canvasPanel.getActionMap().put("clearAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearScene();
            }
        });
    }

    private void initListeners() {
        canvasPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                activeTool.mousePressed(e);
                drawScene();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                activeTool.mouseReleased(e);
                drawScene();
            }
        });
        canvasPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                activeTool.mouseDragged(e);
                drawScene();
            }
        });
        canvasPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                activeTool.keyPressed(e);
                drawScene();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                activeTool.keyReleased(e);
                drawScene();
            }
        });
    }

    //C key: clears the canvas and resets all states - model, clippers and the state of the active tool
    private void clearScene() {
        canvasModel.removeAllRasterizables();
        clipContext = new ClipContext();
        window.resetClipCheckBoxes();
        //new tool and tool context, so no tool keeps a reference to removed objects (unfinished polygon, selection)
        setActiveTool();
        drawScene();
        Logger.warning("User cleared the scene.");
    }

    private void saveScene(Path path) {
        SceneSnapshot snapshot = new SceneSnapshot(
                new ArrayList<>(canvasModel.getRasterizables()),
                clipContext.getClipWindow(),
                clipContext.getClipPolygon(),
                clipContext.isLineClippingEnabled(),
                clipContext.isPolygonClippingEnabled(),
                clipContext.isClipperVisualizationEnabled()
        );
        try {
            sceneFileStorage.save(path, snapshot);
            Logger.info("Scene saved to: " + path);
        } catch (IOException e) {
            Logger.error("Saving scene failed: " + e.getMessage());
            window.showError("Saving scene failed: " + e.getMessage());
        }
    }

    private void openScene(Path path) {
        try {
            applySceneSnapshot(sceneFileStorage.load(path));
            Logger.info("Scene loaded from: " + path);
        } catch (IOException | SceneFormatException e) {
            Logger.error("Loading scene failed: " + e.getMessage());
            window.showError("Loading scene failed: " + e.getMessage());
        }
    }

    private void loadDemoScene() {
        try {
            applySceneSnapshot(sceneFileStorage.loadDemo());
            Logger.info("Demo scene loaded.");
        } catch (IOException | SceneFormatException e) {
            Logger.error("Loading demo scene failed: " + e.getMessage());
            window.showError("Loading demo scene failed: " + e.getMessage());
        }
    }

    //replaces the whole scene, the same way as clearScene - model, clippers and the active tool are reset
    //snapshot is already complete here, so a broken file never leaves the canvas half loaded
    private void applySceneSnapshot(SceneSnapshot snapshot) {
        canvasModel.removeAllRasterizables();
        for (var rasterizable : snapshot.rasterizables()) {
            canvasModel.addRasterizable(rasterizable);
        }

        //missing clipper in the file means the default one
        clipContext = new ClipContext();
        if (snapshot.clipWindow() != null)
            clipContext.setClipWindow(snapshot.clipWindow());
        if (snapshot.clipPolygon() != null)
            clipContext.setClipPolygon(snapshot.clipPolygon());
        clipContext.setLineClippingEnabled(snapshot.lineClippingEnabled());
        clipContext.setPolygonClippingEnabled(snapshot.polygonClippingEnabled());
        clipContext.setClipperVisualizationEnabled(snapshot.clipperVisualizationEnabled());
        window.setClipCheckBoxes(snapshot.lineClippingEnabled(), snapshot.polygonClippingEnabled(), snapshot.clipperVisualizationEnabled());

        //new tool and tool context, so no tool keeps a reference to the objects of the previous scene
        setActiveTool();
        drawScene();
    }

    private void drawScene() {
        canvasPanel.getRaster().clear();

        for (var rasterizable : canvasModel.getRasterizables()) {
            Rasterizable toDraw = rasterizable;
            if (toDraw instanceof Clippable clippable)
                toDraw = clippable.clip(clipContext);

            if (toDraw != null)
                toDraw.rasterize(rasterizationContext);
        }

        if (clipContext.isClipperVisualizationEnabled())
            drawClippers();

        if (activeTool != null)
            activeTool.drawPreview(rasterizationContext);

        canvasPanel.repaint();
    }

    private void drawClippers() {
        var clipWindow = clipContext.getClipWindow();
        if (clipContext.isLineClippingEnabled() && clipWindow != null) {
            clipWindow.visualize(rasterizationContext);
        }

        var clipPolygon = clipContext.getClipPolygon();
        if (clipContext.isPolygonClippingEnabled() && clipPolygon != null) {
            polygonRasterizer.setColor(ColorUtil.CLIP_POLYGON_COLOR);
            polygonRasterizer.rasterize(clipPolygon);
        }
    }
}
