package rasterize;

import fill.ScanLineFiller;
import fill.SeedFillerStackImpl;

public class RasterizationContext {
    private LineRasterizer lineRasterizer;
    private PolygonRasterizer polygonRasterizer;
    private SeedFillerStackImpl seedFiller;
    private ScanLineFiller scanLineFiller;
    public RasterizationContext(LineRasterizer lineRasterizer, PolygonRasterizer polygonRasterizer, SeedFillerStackImpl seedFiller, ScanLineFiller scanLineFiller) {
        this.lineRasterizer = lineRasterizer;
        this.polygonRasterizer = polygonRasterizer;
        this.seedFiller = seedFiller;
        this.scanLineFiller = scanLineFiller;
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

    public SeedFillerStackImpl getSeedFiller() {
        return seedFiller;
    }

    public void setSeedFiller(SeedFillerStackImpl seedFiller) {
        this.seedFiller = seedFiller;
    }
}
