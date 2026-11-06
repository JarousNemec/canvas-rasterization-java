package clip;

import rasterize.Rasterizable;

public interface Clippable {
    Rasterizable clip (ClipContext context);
}
