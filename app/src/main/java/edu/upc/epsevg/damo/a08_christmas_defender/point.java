package edu.upc.epsevg.damo.a08_christmas_defender;

import android.graphics.Matrix;

public class point {
    double x,y;

    point() {
        x=y=0;
    }

    point(double inx, double iny) {
        x=inx;
        y=iny;
    }

    public static point sum(point p1, point p2) {
        return new point(p1.x + p2.x, p1.y + p2.y);
    }

    public static point sub(point p1, point p2) {
        return new point(p1.x - p2.x, p1.y - p2.y);
    }

    public static point mul(double a, point p) {
        return new point(a * p.x, a * p.y);
    }

    public static point div(point p, double a) {
        return new point(p.x / a, p.y / a);
    }

    public static double abs(point p) {
        return Math.sqrt(p.x * p.x + p.y * p.y);
    }

    public static double norm(point p) {
        return p.x * p.x + p.y * p.y;
    }

    public static point polar(double r, double a) {
        return new point(r * Math.cos(a), r * Math.sin(a));
    }

    public static double scalarProd(point p1, point p2) {
        return p1.x * p2.x + p1.y * p2.y;
    }

    public static point unitary(point p) {
        return point.div(p, point.abs(p));
    }

    public static point orthogonal(point p) {
        return new point(-p.y, p.x);
    }

    public static point world2reference(point center, point right, point p) {
        point cp = point.sub(p,center); // Center to point
        point cr = point.sub(right,center); // Center to right
        point ct = point.orthogonal(cr); // Center to top, Orthogonal of CR
        return new point(scalarProd(cp,cr) / norm(cr), // Componente X
                scalarProd(cp,ct) / norm(ct));        // Componente Y
    }

    public static point reference2world(point center, point right, point p) {
        point cr = point.sub(right,center);
        point ct = point.orthogonal(cr);
        return point.sum(center,
                point.sum(point.mul(p.x,cr), point.mul(p.y,ct)));
    }

    public static double distance(point p1, point p2) {
        return abs(sub(p1,p2));
    }

    public static double angle(point p1, point p2) {
        return Math.atan2(p2.y - p1.y, p2.x - p1.x) * 180 / Math.PI;
    }
}
