import controller.Controller2D;
import utils.Logger;
import view.Window;

public class Main {
    public static void main(String[] args) {
        Window window = new Window(800, 600,"Canvas Rasterization");
        new Controller2D(window.getPanel());
        Logger.info("Rasterization has been started...");
    }
}