package persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fill.SeedFillStopMode;
import fill.pattern.FillerMode;
import fill.pattern.PatternFill;
import model.ClipWindow;
import model.Line;
import model.Point2D;
import model.Polygon;
import model.SeedFill;
import rasterize.Rasterizable;
import utils.ColorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//converts the scene to JSON and back
//Gson works only with the simple records below, model classes are converted to them by hand,
//so the file format doesn't depend on private fields of the model
public class SceneJsonSerializer {
    //bump when the format changes, so old files can be recognized
    private static final int FORMAT_VERSION = 1;

    private static final String TYPE_LINE = "line";
    private static final String TYPE_POLYGON = "polygon";
    private static final String TYPE_SEED_FILL = "seedFill";

    //structure of the file
    record SceneData(int version, List<ObjectData> rasterizables, ClipData clip) {
    }

    //one record for all object types, fields that the type doesn't use are null (and are not written to the file)
    //colors are saved as hex "#AARRGGBB", so they can be read and edited by hand
    //fill pattern is saved as its mode + two colors: polygon -> fillColor + patternColor, seed fill -> color + patternColor
    record ObjectData(String type, String color, String fillColor, FillerMode fillerMode, String patternColor, Boolean filled,
                      SeedFillStopMode mode, String borderColor, List<Point2D> points, Point2D seed) {
    }

    //window is saved as two opposite corners [top left, bottom right]
    record ClipData(List<Point2D> window, List<Point2D> polygon,
                    boolean lineClippingEnabled, boolean polygonClippingEnabled, boolean clipperVisualizationEnabled) {
    }

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public String serialize(SceneSnapshot snapshot) {
        List<ObjectData> rasterizables = new ArrayList<>();
        for (Rasterizable rasterizable : snapshot.rasterizables()) {
            rasterizables.add(toData(rasterizable));
        }

        ClipWindow clipWindow = snapshot.clipWindow();
        List<Point2D> window = clipWindow == null ? null : List.of(
                new Point2D(clipWindow.getXMin(), clipWindow.getYMin()),
                new Point2D(clipWindow.getXMax(), clipWindow.getYMax()));
        List<Point2D> polygon = snapshot.clipPolygon() == null ? null : snapshot.clipPolygon().getPointsReadOnly();
        ClipData clip = new ClipData(window, polygon, snapshot.lineClippingEnabled(),
                snapshot.polygonClippingEnabled(), snapshot.clipperVisualizationEnabled());

        return gson.toJson(new SceneData(FORMAT_VERSION, rasterizables, clip));
    }

    //the whole file is read first and only then the snapshot is created,
    //so the caller gets either a complete scene or an exception, never half of it
    public SceneSnapshot deserialize(String json) throws SceneFormatException {
        try {
            SceneData scene = gson.fromJson(json, SceneData.class);
            if (scene == null)
                throw new SceneFormatException("Scene file is empty");
            if (scene.version() != FORMAT_VERSION)
                throw new SceneFormatException("Unsupported scene version: " + scene.version());

            List<Rasterizable> rasterizables = new ArrayList<>();
            for (ObjectData data : scene.rasterizables()) {
                rasterizables.add(fromData(data));
            }

            ClipData clip = scene.clip();
            ClipWindow clipWindow = null;
            if (clip.window() != null)
                //constructor takes the bottom right corner first
                clipWindow = new ClipWindow(clip.window().get(1), clip.window().get(0));

            Polygon clipPolygon = null;
            if (clip.polygon() != null) {
                if (clip.polygon().size() < 3)
                    throw new SceneFormatException("Clip polygon must have at least 3 points");
                clipPolygon = new Polygon(new ArrayList<>(clip.polygon()));
            }

            return new SceneSnapshot(rasterizables, clipWindow, clipPolygon, clip.lineClippingEnabled(),
                    clip.polygonClippingEnabled(), clip.clipperVisualizationEnabled());
        } catch (RuntimeException e) {
            //broken JSON, wrong type of a value or missing value (null) - all of it means an invalid file
            throw new SceneFormatException("Invalid scene file: " + e.getMessage(), e);
        }
    }

    private ObjectData toData(Rasterizable rasterizable) {
        return switch (rasterizable) {
            case Line line -> new ObjectData(TYPE_LINE, ColorUtil.integerToHex(line.getColor()), null, null, null, null,
                    null, null, line.getVertices(), null);
            case Polygon polygon -> {
                PatternFill pattern = polygon.getFillPattern();
                yield new ObjectData(TYPE_POLYGON, ColorUtil.integerToHex(polygon.getColor()),
                        ColorUtil.integerToHex(pattern.getPrimaryColor()), pattern.getFillerMode(), writePatternColor(pattern), polygon.isFilled(),
                        null, null, polygon.getPointsReadOnly(), null);
            }
            case SeedFill seedFill -> {
                PatternFill pattern = seedFill.getFillPattern();
                yield new ObjectData(TYPE_SEED_FILL, ColorUtil.integerToHex(pattern.getPrimaryColor()),
                        null, pattern.getFillerMode(), writePatternColor(pattern), null,
                        seedFill.getMode(), ColorUtil.integerToHex(seedFill.getBorderColor()), null, seedFill.getSeed());
            }
            default -> throw new IllegalArgumentException("Can't save " + rasterizable.getClass().getSimpleName());
        };
    }

    //missing values are null - hexToInteger and unboxing of Boolean throw NullPointerException, so they end up as SceneFormatException
    private Rasterizable fromData(ObjectData data) throws SceneFormatException {
        switch (data.type()) {
            case TYPE_LINE: {
                Line line = new Line(data.points().get(0), data.points().get(1));
                line.setColor(ColorUtil.hexToInteger(data.color()));
                return line;
            }
            case TYPE_POLYGON: {
                //polygon in progress is in the model from its first point, so less than 3 points is valid here
                if (data.points().isEmpty())
                    throw new SceneFormatException("Polygon must have at least 1 point");
                //new ArrayList, because Polygon doesn't copy the list and its editing writes into it
                Polygon polygon = new Polygon(new ArrayList<>(data.points()));
                polygon.setColor(ColorUtil.hexToInteger(data.color()));
                polygon.setFillPattern(readPattern(data, data.fillColor()));
                if (data.filled())
                    polygon.enableFill();
                return polygon;
            }
            case TYPE_SEED_FILL: {
                //Gson returns null for unknown enum value, so mode must be checked
                return new SeedFill(Objects.requireNonNull(data.seed(), "seed"), readPattern(data, data.color()),
                        Objects.requireNonNull(data.mode(), "mode"), ColorUtil.hexToInteger(data.borderColor()));
            }
            default:
                throw new SceneFormatException("Unknown object type: " + data.type());
        }
    }

    //solid has no second color, so it is not written
    private String writePatternColor(PatternFill pattern) {
        if (pattern.getFillerMode() == FillerMode.SOLID)
            return null;
        return ColorUtil.integerToHex(pattern.getSecondaryColor());
    }

    //missing filler mode means SOLID - files saved before patterns existed have none
    //(Gson returns null for unknown value as well, so unknown pattern is loaded as SOLID too)
    private PatternFill readPattern(ObjectData data, String primaryColor) {
        FillerMode fillerMode = data.fillerMode() == null ? FillerMode.SOLID : data.fillerMode();
        int primary = ColorUtil.hexToInteger(primaryColor);
        int secondary = fillerMode == FillerMode.SOLID ? primary : ColorUtil.hexToInteger(data.patternColor());
        return fillerMode.createPattern(primary, secondary);
    }
}
