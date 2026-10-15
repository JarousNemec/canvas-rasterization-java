package utils;

import java.awt.*;

public class ColorUtils {

    public static int toRgbInt(Color color) {
        if (color == null) {
            return 0x000000;
        }
        return (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue();
    }
}
