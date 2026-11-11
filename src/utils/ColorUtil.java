package utils;

import java.awt.*;

public class ColorUtil {
    public static final int DEFAULT_LINE_COLOR = Color.WHITE.getRGB();
    public static final int DEFAULT_FILL_COLOR = Color.BLACK.getRGB();
    public static final int PREVIEW_COLOR = Color.ORANGE.getRGB();
    public static final int ALERT_COLOR = Color.red.getRGB();
    public static final int SELECT_COLOR = Color.CYAN.getRGB();
    public static final int CLIP_WINDOW_COLOR = Color.YELLOW.getRGB();
    public static final int CLIP_POLYGON_COLOR = Color.RED.getRGB();
    public static final int VERTEX_MARKER_COLOR = Color.GRAY.getRGB();
    public static final int DEFAULT_ICON_COLOR = Color.LIGHT_GRAY.getRGB();
    public static final int DEFAULT_BACKGROUND_COLOR = Color.BLACK.getRGB();

    //color as "#AARRGGBB", e.g. white -1 -> "#FFFFFFFF"
    public static String integerToHex(int color) {
        return String.format("#%08X", color);
    }

    //accepts "#AARRGGBB" or "#RRGGBB" (then the color is opaque), # is optional
    //parseUnsignedInt, because colors with alpha FF don't fit into positive int
    public static int hexToInteger(String hex) {
        String digits = hex.startsWith("#") ? hex.substring(1) : hex;
        return switch (digits.length()) {
            case 8 -> Integer.parseUnsignedInt(digits, 16);
            case 6 -> 0xFF000000 | Integer.parseInt(digits, 16);
            default -> throw new IllegalArgumentException("Color must be #AARRGGBB or #RRGGBB, found " + hex);
        };
    }
}
