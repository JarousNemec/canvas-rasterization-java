package controller.tool;

import select.Hit;
import select.Selectable;
import select.SelectionUtil;
import utils.Logger;

import java.awt.*;
import java.util.List;

public class EditorToolUtil {

    public static Hit handleHitTest(List<? extends Selectable> candidates, Point mousePosition) {
        Hit result = SelectionUtil.findHit(candidates, (int) mousePosition.getX(), (int) mousePosition.getY());
        if (result != null) {
            Logger.info("Found an existing " + result);
            return result;
        } else {
            Logger.info("Hit not found.");
            return null;
        }
    }
}
