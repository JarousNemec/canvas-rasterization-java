package controller;

import constants.DrawingMode;
import constants.RasterizationMode;
import fill.SeedFiller;
import model.*;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerTrivial;
import rasterize.PolygonRasterizer;
import rasterize.RasterizationContext;
import utils.Logger;
import view.CanvasPanel;
import view.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Controller2D {
    private final CanvasPanel canvasPanel;

    // Rasterizers
    private final LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;

    //Filler
    private final SeedFiller seedFiller;

    //Rasterization Context containing all rasterizers
    private final RasterizationContext rasterizationContext;

    //Model that contains all rasterizables and allowing us to render them all in one cycle in order they were added
    private final CanvasModel canvasModel;

    private Polygon currentPolygon = new Polygon();
    private Line currentLine = new Line();

    private DrawingMode drawingMode = DrawingMode.LINE;

    private boolean previewEnabled = false;

    public Controller2D(Window window) {
        this.canvasPanel = window.getPanel();
        initWindowEvents(window);

        lineRasterizer = new LineRasterizerTrivial(canvasPanel.getRaster());
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);

        seedFiller = new SeedFiller(canvasPanel.getRaster());

        rasterizationContext = new RasterizationContext(lineRasterizer, polygonRasterizer, seedFiller);
        canvasModel = new CanvasModel();


        initListeners();
        initKeyBindings();
    }

    private void initWindowEvents(Window window) {
        window.setOnModeChanged(newMode -> {
            this.drawingMode = newMode;
            Logger.info("Drawing mode changed to: " + newMode);
        });
    }

    private void initKeyBindings() {
        //keybindings for scene clearing
        canvasPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('c'), "clearAction");
        canvasPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('C'), "clearAction");

        canvasPanel.getActionMap().put("clearAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearScene();
            }
        });

        //keybindings for scene clearing
        canvasPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('a'), "addAction");
        canvasPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('A'), "addAction");

        canvasPanel.getActionMap().put("addAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (drawingMode == DrawingMode.POLYGON && currentPolygon.getSize() > 0) {
                    canvasModel.addRasterizable(currentPolygon);
                    currentPolygon = new Polygon();
                    Logger.info("Added new polygon.");
                }
            }
        });
    }

    private void initListeners() {
        canvasPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (drawingMode == DrawingMode.LINE && isPointInView(e.getPoint())) {
                        currentLine = new Line();
                        currentLine.setStart(new Point2D(e.getX(), e.getY()));
                        var mousePosition = canvasPanel.getMousePosition();
                        if (mousePosition != null) {
                            currentLine.setEnd((int) mousePosition.getX(), (int) mousePosition.getY());
                        }
                        Logger.info("New line: " + currentLine.toString());
                    }
                    enablePreview(true);
                    drawScene();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (drawingMode == DrawingMode.POLYGON && isPointInView(e.getPoint())) {
                        currentPolygon.addPoint(new Point2D(e.getX(), e.getY()));
                        canvasModel.addRasterizable(currentPolygon);
                        Logger.info("New point: " + e.getX() + ", " + e.getY() + " has been added to polygon.");

                    } else if (drawingMode == DrawingMode.LINE && isPointInView(e.getPoint())) {
                        currentLine.setEnd(new Point2D(e.getX(), e.getY()));
                        canvasModel.addRasterizable(currentLine);
                        Logger.info("New point: " + e.getX() + ", " + e.getY() + " has been added to polygon.");

                    } else if (drawingMode == DrawingMode.FILLER && isPointInView(e.getPoint())) {
                        var seedFill = new SeedFill(new Point2D(e.getX(), e.getY()), Color.GREEN.getRGB());
                        canvasModel.addRasterizable(seedFill);
                    }
                    enablePreview(false);
                    drawScene();
                }
            }
        });
        canvasPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if ((drawingMode == DrawingMode.POLYGON || drawingMode == DrawingMode.LINE) && previewEnabled) {
                    drawScene();
                }
            }
        });
    }

    private boolean isPointInView(Point p) {
        return canvasPanel.getRaster().contains((int) p.getX(), (int) p.getY());
    }

    private void enablePreview(boolean enable) {
        previewEnabled = enable;
        Logger.info("Preview " + (enable ? "enabled." : "disabled."));
    }

    private void clearScene() {
        canvasModel.removeAllRasterizables();
        currentLine = new Line();
        currentPolygon = new Polygon();
        drawScene();
        Logger.warning("User clear the scene.");
    }

    private void drawScene() {
        canvasPanel.getRaster().clear();

        for (var rasterizable : canvasModel.getRasterizables()) {
            rasterizable.rasterize(rasterizationContext);
        }

        if (previewEnabled) {
            var mousePosition = canvasPanel.getMousePosition();
            if (mousePosition != null) {
                if (drawingMode == DrawingMode.POLYGON && currentPolygon != null)
                    polygonRasterizer.rasterizePointPreview((int) mousePosition.getX(), (int) mousePosition.getY(), currentPolygon);
                else if (drawingMode == DrawingMode.LINE && currentLine != null)
                    lineRasterizer.rasterize(currentLine.getStart(), (int) mousePosition.getX(), (int) mousePosition.getY(), RasterizationMode.PREVIEW);
            }
        }
        canvasPanel.repaint();
    }
}
