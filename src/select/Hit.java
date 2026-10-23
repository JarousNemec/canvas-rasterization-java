package select;

public record Hit(Selectable target, HitType hitType, int index, double distanceSq) {

    //e.g. "Hit{EDGE 3 of Polygon with 4 points., distance=4.00 px}"
    @Override
    public String toString() {
        return String.format("Hit{%s %d of %s, distance=%.2f px}", hitType, index, target, Math.sqrt(distanceSq));
    }
}
