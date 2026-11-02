package controller.tool;

import select.Hit;
import select.SelectionService;
import utils.Logger;

import java.awt.*;

public class EditorToolUtil {

    public static Hit handleHitTest(SelectionService selectionService, Point mousePosition) {
        Hit result = selectionService.findHit((int) mousePosition.getX(), (int) mousePosition.getY());
        if (result != null) {
            Logger.info("Found an existing " + result);
            return result;
        } else {
            Logger.info("Hit not found.");
            return null;
        }
    }
}
