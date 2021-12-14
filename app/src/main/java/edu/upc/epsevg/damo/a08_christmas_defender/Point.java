package edu.upc.epsevg.damo.a08_christmas_defender;

public class Point {
    double x,y;

    Point() {
        x=y=0;
    }

    Point(double inx, double iny) {
        x=inx;
        y=iny;
    }

    public static Point sum(Point p1, Point p2) {
        return new Point(p1.x + p2.x, p1.y + p2.y);
    }

    public static Point sub(Point p1, Point p2) {
        return new Point(p1.x - p2.x, p1.y - p2.y);
    }

    public static Point mul(double a, Point p) {
        return new Point(a * p.x, a * p.y);
    }

    public static Point div(Point p, double a) {
        return new Point(p.x / a, p.y / a);
    }

    public static double abs(Point p) {
        return Math.sqrt(p.x * p.x + p.y * p.y);
    }

    public static double norm(Point p) {
        return p.x * p.x + p.y * p.y;
    }

    public static Point polar(double r, double a) {
        return new Point(r * Math.cos(a), r * Math.sin(a));
    }

    public static double scalarProd(Point p1, Point p2) {
        return p1.x * p2.x + p1.y * p2.y;
    }

    public static Point unitary(Point p) {
        return Point.div(p, Point.abs(p));
    }

    public static Point orthogonal(Point p) {
        return new Point(-p.y, p.x);
    }

    public static Point world2reference(Point center, Point right, Point p) {
        Point cp = Point.sub(p,center); // Center to point
        Point cr = Point.sub(right,center); // Center to right
        Point ct = Point.orthogonal(cr); // Center to top, Orthogonal of CR
        return new Point(scalarProd(cp,cr) / norm(cr), // Componente X
                scalarProd(cp,ct) / norm(ct));        // Componente Y
    }

    public static Point reference2world(Point center, Point right, Point p) {
        Point cr = Point.sub(right,center);
        Point ct = Point.orthogonal(cr);
        return Point.sum(center,
                Point.sum(Point.mul(p.x,cr), Point.mul(p.y,ct)));
    }

    public static double distance(Point p1, Point p2) {
        return abs(sub(p1,p2));
    }

    public static double angle(Point p1, Point p2) {
        return Math.atan2(p2.y - p1.y, p2.x - p1.x) * 180 / Math.PI;
    }
}
