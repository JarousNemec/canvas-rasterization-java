package rasterize;

public interface Rasterizable {
    void rasterize(RasterizationContext context);
    void rasterize(RasterizationContext context, RasterizationMode mode);
}
