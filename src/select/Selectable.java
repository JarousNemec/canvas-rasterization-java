package select;

public interface Selectable {
    Hit hitTest(int x, int y, int tolerance);
}
