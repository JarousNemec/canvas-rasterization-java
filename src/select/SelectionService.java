package select;

import model.CanvasModel;

public class SelectionService {
    private final CanvasModel canvasModel;
    private static final int TOLERANCE = 5;

    public SelectionService(CanvasModel canvasModel) {
        this.canvasModel = canvasModel;
    }

    public Hit findHit(int x, int y) {
        Hit bestHit = null;

        //.reversed() is equivalent to for(int i = array.length - 1; i >= 0; i--){var item = array[i]}
        for (var rasterizable : canvasModel.getRasterizables().reversed()) {
            if (!(rasterizable instanceof Selectable selectable))
                continue;

            Hit hit = selectable.hitTest(x, y, TOLERANCE);
            if (hit != null && isBetter(hit, bestHit)) {
                bestHit = hit;
            }
        }
        return bestHit;
    }

    private boolean isBetter(Hit candidate, Hit best) {
        if (best == null) return true;
        if (candidate.hitType() != best.hitType()) return candidate.hitType() == HitType.VERTEX; //vertex beats edge in selection
        return candidate.distanceSq() < best.distanceSq();
    }
}
