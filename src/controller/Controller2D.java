package controller;

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
    private final CanvasPanel canvasPanel;

    // Rasterizers
    private final LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;

    //Filler
    private final SeedFillerStackImpl seedFiller;
    private final ScanLineFiller scanLineFiller;

    //Rasterization Context containing all rasterizers
    private final RasterizationContext rasterizationContext;

    private final ToolContext toolContext;

    //Model that contains all rasterizables and allowing us to render them all in one cycle in order they were added
    private final CanvasModel canvasModel;

    private ToolType activeToolType = ToolType.LINE;
    private EditorTool activeTool = null;

    public Controller2D(Window window) {
        this.canvasPanel = window.getPanel();

        lineRasterizer = new LineRasterizerTrivial(canvasPanel.getRaster());
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);

        seedFiller = new SeedFillerStackImpl(canvasPanel.getRaster());
        scanLineFiller = new ScanLineFiller(canvasPanel.getRaster());

        rasterizationContext = new RasterizationContext(lineRasterizer, polygonRasterizer, seedFiller, scanLineFiller);

        canvasModel = new CanvasModel();
        toolContext = new ToolContext(canvasModel, canvasPanel.getRaster().getHeight(), canvasPanel.getRaster().getWidth());
        setActiveTool();

        initWindowEvents(window);
        initToolContextEvents(window);
        initListeners();
        initKeyBindings();

        drawScene();
    }

    private void initToolContextEvents(Window window) {
        toolContext.setOnFillColorChangedByTool(window::setFillColor);
        toolContext.setOnLineColorChangedByTool(window::setLineColor);
    }

    private void initWindowEvents(Window window) {
        window.setOnToolChangedListener(this::setActiveToolType);
        window.setOnFillColorChangedListener(this::updateFillColor);
        window.setOnLineColorChangedListener(this::updateLineColor);
    }

    private void updateLineColor(int color) {
        toolContext.setCurrentLineColor(color);
        drawScene();
    }

    private void updateFillColor(int color) {
        toolContext.setCurrentFillColor(color);
        drawScene();
    }

    private void setActiveToolType(ToolType activeToolType) {
        Logger.info("Drawing mode changing from:" + this.activeToolType + "to: " + activeToolType);
        this.activeToolType = activeToolType;
        setActiveTool();
        drawScene();
    }

    private void setActiveTool() {
        switch (this.activeToolType) {
            case LINE:
                activeTool = new LineTool(toolContext);
                break;
            case POLYGON:
                activeTool = new PolygonTool(toolContext);
                break;
            case FILLER:
                activeTool = new FillerTool(toolContext);
                break;
            case VERTEX:
                activeTool = new VertexTool(toolContext);
                break;
            case SELECT:
                activeTool = new SelectTool(toolContext);
                break;
        }
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
        setActiveTool();
        drawScene();
        Logger.warning("User clear the scene.");
    }

    private void drawScene() {
        canvasPanel.getRaster().clear();

        for (var rasterizable : canvasModel.getRasterizables()) {
            rasterizable.rasterize(rasterizationContext);
        }

        if (activeTool != null)
            activeTool.drawPreview(rasterizationContext);

        canvasPanel.repaint();
    }
}
