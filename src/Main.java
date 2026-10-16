import controller.Controller2D;
import utils.Logger;
import view.Window;

public class Main {
    public static void main(String[] args) {
        Window window = new Window();
        new Controller2D(window);
        Logger.info("Rasterization has been started...");
    }
}