package model;

public class Edge {
    private int x1, y1, x2, y2;

    public Edge(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public boolean isHorizontal() {
        return y1 == y2;
    }

    public void orientate() {
        if (y2 < y1) {
            int tmp = y1;
            y1 = y2;
            y2 = tmp;

            tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
    }

    public boolean isIntersection(int y) {
        return y <= y1 && y > y2;
    }

    public int getIntersectionX(int y) {
        int x = 0;
        //Distances between points = lengths of triangle arms
        float dx = x2 - x1;
        float dy = y2 - y1;

        //Check for dividing by zero
        if (dx == 0 && dy == 0) {
            return -1;
        }

        //Tangent is the opposite side over the hypotenuse. For example dy / dx depending on used quadrant.
        if (Math.abs(dx) >= Math.abs(dy)) {

            //Swap points if one is bigger than the other
            if (x1 > x2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }

            //Calculate the slopes for the slope-intercept form of the equation of a line segment
            float k = dy / dx;
            float q = y1 - k * x1; //y = kx + q -> q = y - kx

            return Math.round(k * x + q);
        } else {
            //Swap points if one is bigger than the other
            if (y1 > y2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }

            //Calculate the slopes for the slope-intercept form of the equation of a line segment
            float k = dx / dy;
            float q = x1 - k * y1;
            return Math.round(k * y + q);
        }
    }
}
