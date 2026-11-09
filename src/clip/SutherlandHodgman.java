package clip;

import model.Point2D;
import utils.GeometryUtil;
import utils.Vec2;

import java.util.ArrayList;
import java.util.List;

public class SutherlandHodgman {
    private SutherlandHodgman() {
    }

    //clips the subject by every edge of the convex clipper one by one,
    //the result of one edge is the input for the next one
    public static List<Point2D> clip(List<Point2D> subject, List<Point2D> clipper) {
        //check if there are enough points in subject and clipper
        if (subject.isEmpty() || clipper.size() < 3)
            return new ArrayList<>(subject);

        //orientate clip polygon CCW
        List<Vec2> clip = Vec2.of(GeometryUtil.orientatePolygonCCW(clipper));
        List<Vec2> output = Vec2.of(subject);

        int n = clip.size();
        for (int i = 0; i < n; i++) {

            //current clip line
            Vec2 a = clip.get(i);
            Vec2 b = clip.get((i + 1) % n);

            List<Vec2> input = output;
            output = new ArrayList<>();

            //everything is already clipped away
            if (input.isEmpty())
                break;

            //clip all input lines by current clip line of the clip polygon
            //edge S -> E, starting with the closing edge (last -> first)
            //only E is added, S was added as E in the previous step
            //  in  -> in  : E
            //  in  -> out : intersection (polygon leaves the area)
            //  out -> in  : intersection, then E (polygon comes back)
            //  out -> out : nothing
            Vec2 s = input.getLast();
            for (Vec2 e : input) {
                boolean sInside = GeometryUtil.isPointInsideCCWEdge(a, b, s);
                boolean eInside = GeometryUtil.isPointInsideCCWEdge(a, b, e);
                if (eInside) {
                    if (!sInside) {
                        output.add(GeometryUtil.lineIntersection(s, e, a, b));
                    }
                    output.add(e);
                } else if (sInside) {
                    output.add(GeometryUtil.lineIntersection(s, e, a, b));
                }
                s = e;
            }
        }
        //round only at the very end
        return Vec2.toPoint2D(output);
    }
}
