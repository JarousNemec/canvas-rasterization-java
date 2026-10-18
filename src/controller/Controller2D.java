package controller;

import constants.DrawingMode;
import constants.RasterizationMode;
import model.Line;
import model.Point2D;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerTrivial;
import rasterize.PolygonRasterizer;
import utils.Logger;
import view.Panel;
import view.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Controller2D {
    private final Panel panel;

    // Rasterizers
    private final LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;

    // Lists of canvas objects
    private final ArrayList<Line> lines;
    private final ArrayList<Polygon> polygons;

    private DrawingMode drawingMode = DrawingMode.LINE;

    private boolean previewEnabled = false;

    public Controller2D(Window window) {
        this.panel = window.getPanel();
        initWindowEvents(window);

        lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);

        lines = new ArrayList<>();
        polygons = new ArrayList<>();
        polygons.add(new Polygon());

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
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('c'), "clearAction");
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('C'), "clearAction");

        panel.getActionMap().put("clearAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearScene();
            }
        });

        //keybindings for scene clearing
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('a'), "addAction");
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('A'), "addAction");

        panel.getActionMap().put("addAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (drawingMode == DrawingMode.POLYGON && !polygons.isEmpty() && polygons.getLast().getSize() > 0) {
                    polygons.add(new Polygon());
                    Logger.info("Added new polygon.");
                }
            }
        });
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (drawingMode == DrawingMode.LINE && isPointInView(e.getPoint())) {
                        lines.add(new Line());
                        lines.getLast().setStart(new Point2D(e.getX(), e.getY()));
                        var mousePosition = panel.getMousePosition();
                        if (mousePosition != null) {
                            lines.getLast().setEnd((int) mousePosition.getX(), (int) mousePosition.getY());
                        }
                        Logger.info("New line: " + lines.getLast().toString());
                    }
                    enablePreview(true);
                    drawScene();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (drawingMode == DrawingMode.POLYGON && isPointInView(e.getPoint())) {
                        if (polygons.isEmpty())
                            polygons.add(new Polygon());
                        polygons.getLast().addPoint(new Point2D(e.getX(), e.getY()));
                        Logger.info("New point: " + e.getX() + ", " + e.getY() + " has been added to polygon.");

                    } else if (drawingMode == DrawingMode.LINE && isPointInView(e.getPoint())) {
                        lines.getLast().setEnd(new Point2D(e.getX(), e.getY()));
                        Logger.info("New point: " + e.getX() + ", " + e.getY() + " has been added to polygon.");
                    }
                    enablePreview(false);
                    drawScene();
                }
            }
        });
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if ((drawingMode == DrawingMode.POLYGON || drawingMode == DrawingMode.LINE) && previewEnabled) {
                    drawScene();
                }
            }
        });
    }

    private boolean isPointInView(Point p) {
        return p.getX() >= 0 && p.getY() >= 0 && p.getX() < panel.getRaster().getWidth() && p.getY() < panel.getRaster().getHeight();
    }

    private void enablePreview(boolean enable) {
        previewEnabled = enable;
        Logger.info("Preview " + (enable ? "enabled." : "disabled."));
    }

    private void clearScene() {
        polygons.clear();
        lines.clear();
        drawScene();
        Logger.warning("User clear the scene.");
    }

    private void drawScene() {
        panel.getRaster().clear();

        for (Line line : lines) {
            lineRasterizer.rasterize(line.getStart(), line.getEnd());
        }

        for (Polygon polygon : polygons) {
            polygonRasterizer.rasterize(polygon);
        }

        if (previewEnabled) {
            var mousePosition = panel.getMousePosition();
            if (mousePosition != null) {
                if (drawingMode == DrawingMode.POLYGON && !polygons.isEmpty())
                    polygonRasterizer.rasterizePointPreview((int) mousePosition.getX(), (int) mousePosition.getY(), polygons.getLast());
                else if (drawingMode == DrawingMode.LINE)
                    lineRasterizer.rasterize(lines.getLast().getStart(), (int) mousePosition.getX(), (int) mousePosition.getY(), RasterizationMode.PREVIEW);
            }
        }

        panel.repaint();
    }
}
