package persistence;

//thrown when the scene file can't be read - broken JSON, missing or invalid values
public class SceneFormatException extends Exception {
    public SceneFormatException(String message) {
        super(message);
    }

    public SceneFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
