package persistence;

import fill.SeedFillMode;
import model.ClipWindow;
import model.Line;
import model.Point2D;
import model.Polygon;
import model.SeedFill;
import org.junit.jupiter.api.Test;
import rasterize.Rasterizable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SceneJsonSerializerTest {

    private final SceneJsonSerializer serializer = new SceneJsonSerializer();

    //clippers are the same as the defaults in ClipContext
    private static final ClipWindow CLIP_WINDOW = new ClipWindow(new Point2D(600, 450), new Point2D(200, 150));

    //helper for creating a polygon in one line, same as in PolygonTest
    //polygonOf(0, 0, 100, 0) creates a polygon with points (0, 0) and (100, 0)
    private static Polygon polygonOf(int... coordinates) {
        Polygon polygon = new Polygon();
        for (int i = 0; i < coordinates.length; i += 2) {
            polygon.addPoint(new Point2D(coordinates[i], coordinates[i + 1]));
        }
        return polygon;
    }

    private static SceneSnapshot snapshotOf(Rasterizable... rasterizables) {
        return new SceneSnapshot(new ArrayList<>(List.of(rasterizables)), CLIP_WINDOW,
                polygonOf(400, 100, 600, 220, 560, 440, 240, 440, 200, 220), false, false, false);
    }

    //serialize and deserialize back - what is saved must be loaded the same
    private SceneSnapshot roundTrip(SceneSnapshot snapshot) throws SceneFormatException {
        return serializer.deserialize(serializer.serialize(snapshot));
    }

    private static void assertPointsEqual(List<Point2D> expected, List<Point2D> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getX(), actual.get(i).getX(), "x of point " + i);
            assertEquals(expected.get(i).getY(), actual.get(i).getY(), "y of point " + i);
        }
    }

    //valid file with one object, the object part is passed as string so the tests can break it
    private static String sceneJsonWith(String rasterizable) {
        return """
                {
                  "version": 1,
                  "rasterizables": [%s],
                  "clip": {
                    "lineClippingEnabled": false,
                    "polygonClippingEnabled": false,
                    "clipperVisualizationEnabled": false
                  }
                }
                """.formatted(rasterizable);
    }

    // round trip

    @Test
    void roundTrip_line_keepsPointsAndColor() throws SceneFormatException {
        Line line = new Line(new Point2D(10, 20), new Point2D(300, 40));
        line.setColor(0xFF00FF00);

        SceneSnapshot loaded = roundTrip(snapshotOf(line));

        Line loadedLine = assertInstanceOf(Line.class, loaded.rasterizables().getFirst());
        assertPointsEqual(line.getVertices(), loadedLine.getVertices());
        assertEquals(0xFF00FF00, loadedLine.getColor());
    }

    @Test
    void roundTrip_polygon_keepsPointsColorsAndFill() throws SceneFormatException {
        Polygon polygon = polygonOf(0, 0, 100, 0, 100, 100, 0, 100);
        polygon.setColor(0xFFFF0000);
        polygon.setFillColor(0xFF0000FF);
        polygon.enableFill();

        SceneSnapshot loaded = roundTrip(snapshotOf(polygon));

        Polygon loadedPolygon = assertInstanceOf(Polygon.class, loaded.rasterizables().getFirst());
        assertPointsEqual(polygon.getPointsReadOnly(), loadedPolygon.getPointsReadOnly());
        assertEquals(0xFFFF0000, loadedPolygon.getColor());
        assertEquals(0xFF0000FF, loadedPolygon.getFillColor());
        assertTrue(loadedPolygon.isFilled());
    }

    @Test
    void roundTrip_polygon_canBeEditedAfterLoad() throws SceneFormatException {
        //Polygon doesn't copy the list of points, so the loaded list must not be immutable
        SceneSnapshot loaded = roundTrip(snapshotOf(polygonOf(0, 0, 100, 0, 100, 100, 0, 100)));

        Polygon loadedPolygon = (Polygon) loaded.rasterizables().getFirst();
        loadedPolygon.addPoint(new Point2D(50, 150));
        loadedPolygon.removePoint(0);

        assertEquals(4, loadedPolygon.getSize());
    }

    @Test
    void roundTrip_unfinishedPolygon_isLoaded() throws SceneFormatException {
        //polygon in progress is in the model from its first point
        SceneSnapshot loaded = roundTrip(snapshotOf(polygonOf(10, 10, 50, 50)));

        Polygon loadedPolygon = assertInstanceOf(Polygon.class, loaded.rasterizables().getFirst());
        assertEquals(2, loadedPolygon.getSize());
        assertFalse(loadedPolygon.isFilled());
    }

    @Test
    void roundTrip_seedFill_keepsAllModes() throws SceneFormatException {
        for (SeedFillMode mode : SeedFillMode.values()) {
            SeedFill seedFill = new SeedFill(new Point2D(140, 150), 0xFF0000FF, mode, 0xFFFFFFFF);

            SceneSnapshot loaded = roundTrip(snapshotOf(seedFill));

            SeedFill loadedSeedFill = assertInstanceOf(SeedFill.class, loaded.rasterizables().getFirst());
            assertEquals(140, loadedSeedFill.getSeed().getX());
            assertEquals(150, loadedSeedFill.getSeed().getY());
            assertEquals(0xFF0000FF, loadedSeedFill.getColor());
            assertEquals(mode, loadedSeedFill.getMode());
            assertEquals(0xFFFFFFFF, loadedSeedFill.getBorderColor());
        }
    }

    @Test
    void roundTrip_keepsOrderOfRasterizables() throws SceneFormatException {
        //order matters - seed fill fills what is drawn before it
        SceneSnapshot loaded = roundTrip(snapshotOf(
                polygonOf(0, 0, 100, 0, 100, 100),
                new SeedFill(new Point2D(60, 30), 0xFF0000FF, SeedFillMode.BACKGROUND, 0xFFFFFFFF),
                new Line(new Point2D(0, 0), new Point2D(10, 10))
        ));

        assertEquals(3, loaded.rasterizables().size());
        assertInstanceOf(Polygon.class, loaded.rasterizables().get(0));
        assertInstanceOf(SeedFill.class, loaded.rasterizables().get(1));
        assertInstanceOf(Line.class, loaded.rasterizables().get(2));
    }

    @Test
    void roundTrip_clippers_keepsGeometryAndFlags() throws SceneFormatException {
        Polygon clipPolygon = polygonOf(400, 100, 600, 220, 560, 440, 240, 440, 200, 220);
        SceneSnapshot snapshot = new SceneSnapshot(new ArrayList<>(), CLIP_WINDOW, clipPolygon, true, false, true);

        SceneSnapshot loaded = roundTrip(snapshot);

        assertEquals(200, loaded.clipWindow().getXMin());
        assertEquals(150, loaded.clipWindow().getYMin());
        assertEquals(600, loaded.clipWindow().getXMax());
        assertEquals(450, loaded.clipWindow().getYMax());
        assertPointsEqual(clipPolygon.getPointsReadOnly(), loaded.clipPolygon().getPointsReadOnly());
        assertTrue(loaded.lineClippingEnabled());
        assertFalse(loaded.polygonClippingEnabled());
        assertTrue(loaded.clipperVisualizationEnabled());
    }

    @Test
    void roundTrip_colorWithAlpha_isNotChanged() throws SceneFormatException {
        Line line = new Line(new Point2D(0, 0), new Point2D(1, 1));
        line.setColor(0xFFFF8000); //alpha FF makes the int negative

        SceneSnapshot loaded = roundTrip(snapshotOf(line));

        assertEquals(0xFFFF8000, loaded.rasterizables().getFirst().getColor());
    }

    @Test
    void serialize_line_doesNotWriteUnusedFields() {
        String json = serializer.serialize(snapshotOf(new Line(new Point2D(0, 0), new Point2D(1, 1))));

        assertFalse(json.contains("fillColor"), json);
        assertFalse(json.contains("seed"), json);
    }

    // invalid files

    @Test
    void deserialize_brokenJson_throws() {
        assertThrows(SceneFormatException.class, () -> serializer.deserialize("{ \"version\": 1, "));
    }

    @Test
    void deserialize_emptyFile_throws() {
        assertThrows(SceneFormatException.class, () -> serializer.deserialize(""));
    }

    @Test
    void deserialize_unknownVersion_throws() {
        String json = sceneJsonWith("").replace("\"version\": 1", "\"version\": 2");

        assertThrows(SceneFormatException.class, () -> serializer.deserialize(json));
    }

    @Test
    void deserialize_unknownType_throws() {
        String json = sceneJsonWith("{ \"type\": \"circle\", \"color\": -1 }");

        assertThrows(SceneFormatException.class, () -> serializer.deserialize(json));
    }

    @Test
    void deserialize_missingValue_throws() {
        //line without color
        String json = sceneJsonWith("{ \"type\": \"line\", \"points\": [{\"x\": 0, \"y\": 0}, {\"x\": 10, \"y\": 10}] }");

        assertThrows(SceneFormatException.class, () -> serializer.deserialize(json));
    }

    @Test
    void deserialize_lineWithOnePoint_throws() {
        String json = sceneJsonWith("{ \"type\": \"line\", \"color\": -1, \"points\": [{\"x\": 0, \"y\": 0}] }");

        assertThrows(SceneFormatException.class, () -> serializer.deserialize(json));
    }

    @Test
    void deserialize_pointWithText_throws() {
        String json = sceneJsonWith("{ \"type\": \"line\", \"color\": -1, \"points\": [{\"x\": \"a\", \"y\": 0}, {\"x\": 10, \"y\": 10}] }");

        assertThrows(SceneFormatException.class, () -> serializer.deserialize(json));
    }

    @Test
    void deserialize_colorAsText_throws() {
        String json = sceneJsonWith("{ \"type\": \"line\", \"color\": \"white\", \"points\": [{\"x\": 0, \"y\": 0}, {\"x\": 10, \"y\": 10}] }");

        assertThrows(SceneFormatException.class, () -> serializer.deserialize(json));
    }

    @Test
    void deserialize_unknownSeedFillMode_throws() {
        String json = sceneJsonWith("{ \"type\": \"seedFill\", \"color\": -1, \"mode\": \"FLOOD\", \"borderColor\": -1, \"seed\": {\"x\": 1, \"y\": 1} }");

        assertThrows(SceneFormatException.class, () -> serializer.deserialize(json));
    }

    @Test
    void deserialize_clipPolygonWithTwoPoints_throws() {
        String json = sceneJsonWith("").replace("\"clip\": {", "\"clip\": { \"polygon\": [{\"x\": 0, \"y\": 0}, {\"x\": 10, \"y\": 10}],");

        assertThrows(SceneFormatException.class, () -> serializer.deserialize(json));
    }

    @Test
    void deserialize_missingClippers_returnsNull() throws SceneFormatException {
        //controller uses the default clippers in this case
        SceneSnapshot loaded = serializer.deserialize(sceneJsonWith(""));

        assertNull(loaded.clipWindow());
        assertNull(loaded.clipPolygon());
    }

    // demo scene

    @Test
    void loadDemo_isValid() throws Exception {
        SceneSnapshot demo = new SceneFileStorage().loadDemo();

        assertFalse(demo.rasterizables().isEmpty());
        assertNotNull(demo.clipWindow());
        assertNotNull(demo.clipPolygon());
    }
}
