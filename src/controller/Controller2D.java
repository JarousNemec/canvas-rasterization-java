package controller;

import mode.DrawingMode;
import mode.RasterizationMode;
import model.Line;
import model.Point2D;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerTrivial;
import rasterize.PolygonRasterizer;
import utils.Logger;
import view.Panel;
import view.Window;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller2D {
    private final Panel panel;

    // Rasterizers
    private LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;

    private Polygon polygon;
    private Line currentLine;
    private DrawingMode drawingMode = DrawingMode.LINE;
    private boolean previewEnabled = false;

    public Controller2D(Window window) {
        this.panel = window.getPanel();
        window.setOnModeChanged(newMode -> {
            this.drawingMode = newMode;
            Logger.info("Drawing mode changed to: " + newMode);
        });

        lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);

        polygon = new Polygon();
        currentLine = new Line();

        initListeners();
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (drawingMode == DrawingMode.POLYGON && isPointInView(e.getPoint())) {
                        polygon.addPoint(new Point2D(e.getX(), e.getY()));
                        Logger.info("New point: " + e.getX() + ", " + e.getY() + " has been added to polygon.");

                    } else if (drawingMode == DrawingMode.LINE && isPointInView(e.getPoint())) {
                        currentLine.setEnd(new Point2D(e.getX(), e.getY()));
                        Logger.info("New point: " + e.getX() + ", " + e.getY() + " has been added to polygon.");

                    }
                    previewEnabled = false;
                    drawScene();
                    Logger.info("Preview disabled.");
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (drawingMode == DrawingMode.POLYGON) {
                    } else if (drawingMode == DrawingMode.LINE && isPointInView(e.getPoint())) {
                        currentLine = new Line();
                        currentLine.setStart(new Point2D(e.getX(), e.getY()));
                        var mousePosition = panel.getMousePosition();
                        if (mousePosition != null) {
                            currentLine.setEnd((int) mousePosition.getX(), (int) mousePosition.getY());
                        }
                        Logger.info("New line: " + currentLine.toString());
                    }
                    previewEnabled = true;
                    drawScene();
                    Logger.info("Preview enabled.");
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
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyChar() == 'c' || e.getKeyChar() == 'C') {
                    polygon.clear();
                    drawScene();
                    Logger.warning("User clear the scene.");
                }
            }
        });
    }

    private boolean isPointInView(Point p) {
        return p.getX() >= 0 && p.getY() >= 0 && p.getX() < panel.getRaster().getWidth() && p.getY() < panel.getRaster().getHeight();
    }

    private void drawScene() {
        panel.getRaster().clear();

        polygonRasterizer.rasterize(polygon);
        lineRasterizer.rasterize(currentLine.getStart(), currentLine.getEnd());
        if (previewEnabled) {
            var mousePosition = panel.getMousePosition();
            if (mousePosition != null) {
                if (drawingMode == DrawingMode.POLYGON)
                    polygonRasterizer.rasterizePointPreview((int) mousePosition.getX(), (int) mousePosition.getY(), polygon);
                else if (drawingMode == DrawingMode.LINE)
                    lineRasterizer.rasterize(currentLine.getStart(), (int) mousePosition.getX(), (int) mousePosition.getY(), RasterizationMode.PREVIEW);
            }
        }
        panel.repaint();
    }
}
