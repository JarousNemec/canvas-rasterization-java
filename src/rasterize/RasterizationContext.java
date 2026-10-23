package rasterize;

import fill.SeedFiller;

public class RasterizationContext {
    private LineRasterizer lineRasterizer;
    private PolygonRasterizer polygonRasterizer;
    private SeedFiller seedFiller;
    public RasterizationContext(LineRasterizer lineRasterizer, PolygonRasterizer polygonRasterizer, SeedFiller seedFiller) {
        this.lineRasterizer = lineRasterizer;
        this.polygonRasterizer = polygonRasterizer;
        this.seedFiller = seedFiller;
    }

    public LineRasterizer getLineRasterizer() {
        return lineRasterizer;
    }

    public void setLineRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public PolygonRasterizer getPolygonRasterizer() {
        return polygonRasterizer;
    }

    public void setPolygonRasterizer(PolygonRasterizer polygonRasterizer) {
        this.polygonRasterizer = polygonRasterizer;
    }

    public SeedFiller getSeedFiller() {
        return seedFiller;
    }

    public void setSeedFiller(SeedFiller seedFiller) {
        this.seedFiller = seedFiller;
    }
}
