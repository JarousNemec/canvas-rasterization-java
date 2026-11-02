package controller.tool;

import model.CanvasModel;

import java.util.function.Consumer;

public class ToolContext {
    private final CanvasModel canvasModel;
    private int currentLineColor = 0xFFFFFF; // Default white
    private int currentFillColor = 0x000000;// Default black

    private int rasterWidth;
    private int rasterHeight;

    // Callbacks for Controller / Window notify
    private Consumer<Integer> onLineColorChangedByTool;
    private Consumer<Integer> onFillColorChangedByTool;


    public ToolContext(CanvasModel canvasModel, int rasterHeight, int rasterWidth) {
        this.canvasModel = canvasModel;
        this.rasterHeight = rasterHeight;
        this.rasterWidth = rasterWidth;
    }


    public CanvasModel getCanvasModel() {
        return canvasModel;
    }

    public int getCurrentLineColor() {
        return currentLineColor;
    }

    public void setCurrentLineColor(int currentLineColor) {
        this.currentLineColor = currentLineColor;
    }

    public int getCurrentFillColor() {
        return currentFillColor;
    }

    public void setCurrentFillColor(int currentFillColor) {
        this.currentFillColor = currentFillColor;
    }

    //notify controller that color was changed from tool
    public void setLineColorFromTool(int color) {
        this.currentLineColor = color;
        if (onLineColorChangedByTool != null) {
            onLineColorChangedByTool.accept(color);
        }
    }

    public void setFillColorFromTool(int color) {
        this.currentFillColor = color;
        if (onFillColorChangedByTool != null) {
            onFillColorChangedByTool.accept(color);
        }
    }

    // register listeners by controller
    public void setOnLineColorChangedByTool(Consumer<Integer> listener) { this.onLineColorChangedByTool = listener; }
    public void setOnFillColorChangedByTool(Consumer<Integer> listener) { this.onFillColorChangedByTool = listener; }

    public int getRasterWidth() {
        return rasterWidth;
    }

    public void setRasterWidth(int rasterWidth) {
        this.rasterWidth = rasterWidth;
    }

    public int getRasterHeight() {
        return rasterHeight;
    }

    public void setRasterHeight(int rasterHeight) {
        this.rasterHeight = rasterHeight;
    }
}
