package controller;

import clip.ClipContext;
import clip.Clippable;
import controller.tool.*;
import fill.ScanLineFiller;
import rasterize.*;
import fill.SeedFillerStackImpl;
import model.*;
import utils.Logger;
import view.CanvasPanel;
import view.Window;

import javax.swing.*;
import java.awt.event.*;

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

    private final ClipContext clipContext;

    //Model that contains all rasterizables and allowing us to render them all in one cycle in order they were added
    private final CanvasModel canvasModel;

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

        rasterizationContext = new RasterizationContext(lineRasterizer, polygonRasterizer, seedFiller, scanLineFiller);

        canvasModel = new CanvasModel();
        setActiveTool();
        initWindowEvents();
        initListeners();
        initKeyBindings();

        drawScene();
    }

    private void initToolContextEvents() {
        toolContext.setOnFillColorChangedByTool(this.window::setFillColor);
        toolContext.setOnLineColorChangedByTool(this.window::setLineColor);
    }

    private void initWindowEvents() {
        this.window.setOnToolChangedListener(this::setActiveToolType);
        this.window.setOnFillColorChangedListener(this::updateFillColor);
        this.window.setOnLineColorChangedListener(this::updateLineColor);
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

    private void setActiveToolType(ToolType activeToolType) {
        Logger.info("Drawing mode changing from:" + this.activeToolType + "to: " + activeToolType);
        this.activeToolType = activeToolType;
        setActiveTool();
        drawScene();
    }

    private void setActiveTool() {
        toolContext = new ToolContext(canvasModel, canvasPanel.getRaster().getHeight(), canvasPanel.getRaster().getWidth());
        initToolContextEvents();
        switch (this.activeToolType) {
            case LINE:
                activeTool = new LineTool(toolContext);
                window.setLineColorChooserEnabled(true);
                window.setFillColorChooserEnabled(false);
                break;
            case POLYGON:
                activeTool = new PolygonTool(toolContext);
                window.setLineColorChooserEnabled(true);
                window.setFillColorChooserEnabled(true);
                break;
            case FILL:
                activeTool = new FillerTool(toolContext);
                window.setLineColorChooserEnabled(false);
                window.setFillColorChooserEnabled(true);
                break;
            case VERTEX:
                activeTool = new VertexTool(toolContext);
                window.setLineColorChooserEnabled(false);
                window.setFillColorChooserEnabled(false);
                break;
            case SELECT:
                activeTool = new SelectTool(toolContext);
                window.setLineColorChooserEnabled(true);
                window.setFillColorChooserEnabled(true);
                break;
            case CLIP:
                activeTool = new ClipTool(toolContext);
                window.setLineColorChooserEnabled(false);
                window.setFillColorChooserEnabled(false);
                break;
        }
        window.setLineColor(toolContext.getCurrentLineColor());
        window.setFillColor(toolContext.getCurrentFillColor());
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

    private void clearScene() {
        canvasModel.removeAllRasterizables();
        drawScene();
        Logger.warning("User clear the scene.");
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

        if (activeTool != null)
            activeTool.drawPreview(rasterizationContext);

        canvasPanel.repaint();
    }
}
