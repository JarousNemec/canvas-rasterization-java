package persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

//saves and loads scenes from files, the demo scene is loaded from resources
public class SceneFileStorage {
    //path on the classpath (res folder), so it works no matter where the app is started from
    private static final String DEMO_SCENE_RESOURCE = "/scenes/demo.json";

    private final SceneJsonSerializer serializer = new SceneJsonSerializer();

    public void save(Path path, SceneSnapshot snapshot) throws IOException {
        Files.writeString(path, serializer.serialize(snapshot), StandardCharsets.UTF_8);
    }

    public SceneSnapshot load(Path path) throws IOException, SceneFormatException {
        return serializer.deserialize(Files.readString(path, StandardCharsets.UTF_8));
    }

    public SceneSnapshot loadDemo() throws IOException, SceneFormatException {
        try (InputStream stream = SceneFileStorage.class.getResourceAsStream(DEMO_SCENE_RESOURCE)) {
            if (stream == null)
                throw new IOException("Demo scene not found: " + DEMO_SCENE_RESOURCE);
            return serializer.deserialize(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
        }
    }
}
