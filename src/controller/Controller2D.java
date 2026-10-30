package controller;

import fill.ScanLineFiller;
import rasterize.*;
import fill.SeedFillerStackImpl;
import model.*;
import model.Polygon;
import select.Hit;
import select.HitType;
import select.SelectionService;
import transform.Transformable;
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
    private final SeedFillerStackImpl seedFiller;
    private final ScanLineFiller scanLineFiller;

    //Rasterization Context containing all rasterizers
    private final RasterizationContext rasterizationContext;

    //Model that contains all rasterizables and allowing us to render them all in one cycle in order they were added
    private final CanvasModel canvasModel;

    //Service that serves information if the clicked point hit some component
    private final SelectionService selectionService;

    private Polygon currentPolygon = new Polygon();
    private Line currentLine = new Line();

    private Hit currentHit = null;

    private UserMode userMode = UserMode.LINE;

    private boolean isBtn1Pressed = false;
    private Point mousePosition = null;

    public Controller2D(Window window) {
        this.canvasPanel = window.getPanel();
        initWindowEvents(window);

        lineRasterizer = new LineRasterizerTrivial(canvasPanel.getRaster());
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);

        seedFiller = new SeedFillerStackImpl(canvasPanel.getRaster());
        scanLineFiller = new ScanLineFiller(canvasPanel.getRaster());

        rasterizationContext = new RasterizationContext(lineRasterizer, polygonRasterizer, seedFiller, scanLineFiller);
        canvasModel = new CanvasModel();

        selectionService = new SelectionService(canvasModel);

        initListeners();
        initKeyBindings();
    }

    private void initWindowEvents(Window window) {
        window.setOnModeChanged(this::setUserMode);
    }

    private void setUserMode(UserMode mode) {
        Logger.info("Drawing mode changing from: " + this.userMode + " to: " + mode);
        if (this.userMode == UserMode.SELECT && mode != UserMode.SELECT) {
            currentHit = null;
        }
        this.userMode = mode;
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

        //keybindings for scene filler
        canvasPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('s'), "fillAction");
        canvasPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('S'), "fillAction");

        canvasPanel.getActionMap().put("fillAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentHit != null && userMode == UserMode.SELECT) {
                    if (currentHit.target() instanceof Polygon) {
                        scanLineFiller.setPolygon((Polygon) currentHit.target());
                        scanLineFiller.fill();
                        canvasPanel.repaint();
                    }
                }
            }
        });

        //keybindings for scene clearing
        canvasPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('a'), "addAction");
        canvasPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('A'), "addAction");

        canvasPanel.getActionMap().put("addAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userMode == UserMode.POLYGON && currentPolygon.getSize() > 0) {
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
                mousePosition = e.getPoint();
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (userMode == UserMode.LINE && isPointInCanvas(e.getPoint())) {
                        currentLine = new Line();
                        currentLine.setStart(new Point2D(e.getX(), e.getY()));
                        var mousePosition = canvasPanel.getMousePosition();
                        if (mousePosition != null) {
                            currentLine.setEnd((int) mousePosition.getX(), (int) mousePosition.getY());
                        }
                    } else if ((userMode == UserMode.SELECT || userMode == UserMode.POINT) && isPointInCanvas(e.getPoint())) {
                        var mousePosition = canvasPanel.getMousePosition();
                        if (mousePosition != null) {
                            Hit result = selectionService.findHit((int) mousePosition.getX(), (int) mousePosition.getY());
                            if (result != null) {
                                Logger.info("Found an existing " + result.toString());
                                currentHit = result;
                            } else {
                                Logger.info("Hit not found.");
                                currentHit = null;
                            }
                        }
                    }
                    isBtn1Pressed = true;

                    drawScene();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePosition = e.getPoint();
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (userMode == UserMode.POLYGON && isPointInCanvas(e.getPoint())) {
                        currentPolygon.addPoint(new Point2D(e.getX(), e.getY()));
                        if (!canvasModel.contains(currentPolygon))
                            canvasModel.addRasterizable(currentPolygon);
                        Logger.info("New point: " + e.getX() + ", " + e.getY() + " has been added to polygon.");

                    } else if (userMode == UserMode.LINE && isPointInCanvas(e.getPoint())) {
                        currentLine.setEnd(new Point2D(e.getX(), e.getY()));
                        if (!canvasModel.contains(currentLine))
                            canvasModel.addRasterizable(currentLine);
                        Logger.info("New line added: " + currentLine.toString());

                    } else if (userMode == UserMode.FILLER && isPointInCanvas(e.getPoint())) {
                        var seedFill = new SeedFill(new Point2D(e.getX(), e.getY()), Color.GREEN.getRGB());
                        canvasModel.addRasterizable(seedFill);
                    }
                    isBtn1Pressed = false;
                    drawScene();
                }
            }
        });
        canvasPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mousePosition = e.getPoint();
                if ((userMode == UserMode.POLYGON || userMode == UserMode.LINE) && isBtn1Pressed) {
                    drawScene();
                } else if (userMode == UserMode.POINT && isPointInCanvas(e.getPoint()) && isBtn1Pressed) {
                    if (currentHit != null) {
                        if (currentHit.target() instanceof Transformable && currentHit.hitType() == HitType.VERTEX) {
                            ((Transformable) currentHit.target()).setNewPointPosition(currentHit.index(), new Point2D(e.getX(), e.getY()));
                            drawScene();
                        }
                    }
                }

            }
        });
    }

    private boolean isPointInCanvas(Point p) {
        return canvasPanel.getRaster().contains((int) p.getX(), (int) p.getY());
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
            if (rasterizable instanceof Polygon) {
                scanLineFiller.setPolygon((Polygon) rasterizable);
                scanLineFiller.fill();
            }
            rasterizable.rasterize(rasterizationContext);
        }

        if (isBtn1Pressed) {
            if (this.mousePosition != null) {
                if (userMode == UserMode.POLYGON && currentPolygon != null)
                    polygonRasterizer.rasterizePointPreview((int) this.mousePosition.getX(), (int) this.mousePosition.getY(), currentPolygon);
                else if (userMode == UserMode.LINE && currentLine != null)
                    lineRasterizer.rasterize(currentLine.getStart(), (int) this.mousePosition.getX(), (int) this.mousePosition.getY(), RasterizationMode.PREVIEW);
            }
        } else if (userMode == UserMode.SELECT) {
            if (currentHit.target() instanceof Rasterizable) {
                ((Rasterizable) currentHit.target()).rasterize(rasterizationContext, RasterizationMode.GLOWING);
            }
        }
        canvasPanel.repaint();
    }
}
