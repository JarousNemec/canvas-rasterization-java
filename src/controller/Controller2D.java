package controller;

import mode.DrawingMode;
import model.Point2D;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerTrivial;
import rasterize.PolygonRasterizer;
import utils.Logger;
import view.Panel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller2D {
    private final Panel panel;

    // Rasterizers
    private LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;

    private final Polygon polygon;
    private final DrawingMode drawingMode = DrawingMode.POLYGON;
    private boolean previewEnabled = false;

    public Controller2D(Panel panel) {
        this.panel = panel;

        lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);

        polygon = new Polygon();

        initListeners();
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (drawingMode == DrawingMode.POLYGON) {
                        polygon.addPoint(new Point2D(e.getX(), e.getY()));
                        Logger.info("New point: " + e.getX() + ", " + e.getY() + " has been added to polygon.");

                        previewEnabled = false;
                        drawScene();
                        Logger.info("Polygon preview disabled.");
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (drawingMode == DrawingMode.POLYGON) {
                        previewEnabled = true;
                        drawScene();
                        Logger.info("Polygon preview enabled.");
                    }
                }
            }
        });
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawingMode == DrawingMode.POLYGON && previewEnabled) {
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

    private void drawScene() {
        panel.getRaster().clear();

        polygonRasterizer.rasterize(polygon);
        if (previewEnabled) {
            var mousePosition = panel.getMousePosition();
            polygonRasterizer.rasterizePointPreview(mousePosition.x, mousePosition.y, polygon);
        }
        panel.repaint();
    }
}
