package controller.tool;

import fill.SeedFillStopMode;
import fill.pattern.FillerMode;
import fill.pattern.PatternFill;
import model.CanvasModel;
import utils.ColorUtil;

import java.util.function.Consumer;

public class ToolContext {
    private final CanvasModel canvasModel;
    private int currentLineColor = ColorUtil.DEFAULT_LINE_COLOR; // Default white
    private int currentFillColor = ColorUtil.DEFAULT_FILL_COLOR;// Default black
    //second color of the pattern, used only when filler mode is not SOLID
    private int currentPatternColor = ColorUtil.DEFAULT_PATTERN_COLOR;
    private SeedFillStopMode currentSeedFillMode = SeedFillStopMode.BACKGROUND;
    private FillerMode currentFillerMode = FillerMode.SOLID;

    private int rasterWidth;
    private int rasterHeight;

    // Callbacks for Controller / Window notify
    private Consumer<Integer> onLineColorChangedByTool;
    private Consumer<PatternFill> onFillPatternChangedByTool;


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

    public int getCurrentPatternColor() {
        return currentPatternColor;
    }

    public void setCurrentPatternColor(int currentPatternColor) {
        this.currentPatternColor = currentPatternColor;
    }

    public SeedFillStopMode getCurrentSeedFillMode() {
        return currentSeedFillMode;
    }

    public void setCurrentSeedFillMode(SeedFillStopMode currentSeedFillMode) {
        this.currentSeedFillMode = currentSeedFillMode;
    }

    public FillerMode getCurrentFillerMode() {
        return currentFillerMode;
    }

    public void setCurrentFillerMode(FillerMode currentFillerMode) {
        this.currentFillerMode = currentFillerMode;
    }

    //notify controller that color was changed from tool
    public void setLineColorFromTool(int color) {
        this.currentLineColor = color;
        if (onLineColorChangedByTool != null) {
            onLineColorChangedByTool.accept(color);
        }
    }

    //fill color, pattern color and filler mode are all set first and only then the controller is notified,
    //because notifying changes the UI and the UI writes the values from this context back to the selected object
    public void setFillPatternFromTool(PatternFill pattern) {
        this.currentFillColor = pattern.getPrimaryColor();
        //solid has no second color, so the pattern color the user picked before is kept
        if (pattern.getFillerMode() != FillerMode.SOLID)
            this.currentPatternColor = pattern.getSecondaryColor();
        this.currentFillerMode = pattern.getFillerMode();
        if (onFillPatternChangedByTool != null) {
            onFillPatternChangedByTool.accept(pattern);
        }
    }

    //pattern made of the current settings - for new and edited objects
    public PatternFill createFillPattern() {
        return currentFillerMode.createPattern(currentFillColor, currentPatternColor);
    }

    //the area is filled when user picked a fill color (default black is the same as background = no fill) or a pattern
    public boolean isFillWanted() {
        return currentFillColor != ColorUtil.DEFAULT_FILL_COLOR || currentFillerMode != FillerMode.SOLID;
    }

    // register listeners by controller
    public void setOnLineColorChangedByTool(Consumer<Integer> listener) { this.onLineColorChangedByTool = listener; }
    public void setOnFillPatternChangedByTool(Consumer<PatternFill> listener) { this.onFillPatternChangedByTool = listener; }

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
