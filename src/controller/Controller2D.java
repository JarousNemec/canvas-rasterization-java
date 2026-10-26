package controller;

import rasterize.*;
import fill.SeedFiller;
import model.*;
import model.Polygon;
import select.Hit;
import select.Selectable;
import select.SelectionService;
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

    //Service that serves information if the clicked point hit some component
    private final SelectionService selectionService;

    private Polygon currentPolygon = new Polygon();
    private Line currentLine = new Line();

    private Selectable currentlySelected = null;

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

        selectionService = new SelectionService(canvasModel);

        initListeners();
        initKeyBindings();
    }

    private void initWindowEvents(Window window) {
        window.setOnModeChanged(this::setDrawingMode);
    }

    private void setDrawingMode(DrawingMode mode) {
        Logger.info("Drawing mode changing from: " + this.drawingMode + " to: " + mode);
        if (this.drawingMode == DrawingMode.SELECT && mode != DrawingMode.SELECT) {
            currentlySelected = null;
        }
        this.drawingMode = mode;
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
                    if (!canvasModel.contains(currentPolygon))
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
                    if (drawingMode == DrawingMode.LINE && isPointInCanvas(e.getPoint())) {
                        currentLine = new Line();
                        currentLine.setStart(new Point2D(e.getX(), e.getY()));
                        var mousePosition = canvasPanel.getMousePosition();
                        if (mousePosition != null) {
                            currentLine.setEnd((int) mousePosition.getX(), (int) mousePosition.getY());
                        }
                    } else if (drawingMode == DrawingMode.SELECT && isPointInCanvas(e.getPoint())) {
                        var mousePosition = canvasPanel.getMousePosition();
                        if (mousePosition != null) {
                            Hit result = selectionService.findHit((int) mousePosition.getX(), (int) mousePosition.getY());
                            if (result != null) {
                                Logger.info("Found an existing " + result.toString());
                                currentlySelected = result.target();
                            } else {
                                Logger.info("Hit not found.");
                                currentlySelected = null;
                            }
                        }
                    }
                    enablePreview(true);
                    drawScene();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (drawingMode == DrawingMode.POLYGON && isPointInCanvas(e.getPoint())) {
                        currentPolygon.addPoint(new Point2D(e.getX(), e.getY()));
                        if (!canvasModel.contains(currentPolygon))
                            canvasModel.addRasterizable(currentPolygon);
                        Logger.info("New point: " + e.getX() + ", " + e.getY() + " has been added to polygon.");

                    } else if (drawingMode == DrawingMode.LINE && isPointInCanvas(e.getPoint())) {
                        currentLine.setEnd(new Point2D(e.getX(), e.getY()));
                        if (!canvasModel.contains(currentLine))
                            canvasModel.addRasterizable(currentLine);
                        Logger.info("New line added: " + currentLine.toString());

                    } else if (drawingMode == DrawingMode.FILLER && isPointInCanvas(e.getPoint())) {
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

    private boolean isPointInCanvas(Point p) {
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
        } else if (drawingMode == DrawingMode.SELECT) {
            if(currentlySelected instanceof Rasterizable){
                ((Rasterizable) currentlySelected).rasterize(rasterizationContext, RasterizationMode.SELECT);
            }
        }
        canvasPanel.repaint();
    }
}
