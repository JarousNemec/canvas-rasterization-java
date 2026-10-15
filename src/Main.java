import controller.Controller2D;
import view.Window;

public class Main {
    public static void main(String[] args) {
        Window window = new Window(800, 600);
        new Controller2D(window.getPanel());

        int a = 5;
        int b = 3;
        float c = a / (float) b;

        System.out.println(c);
    }
}