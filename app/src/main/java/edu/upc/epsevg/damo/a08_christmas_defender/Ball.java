package edu.upc.epsevg.damo.a08_christmas_defender;

import android.util.Log;

import java.util.ArrayList;

public class Ball {
    point c;
    double r;
    boolean thereIsDestination;
    point destination;
    double speedFactor;
    boolean holdingDown;

    public Ball(point inc, double inr) {
        c = inc;
        r = inr;
        thereIsDestination = false;
        speedFactor = 10;
        holdingDown = false;
    }

    public void move(double delta) {
        if (holdingDown || !thereIsDestination) return;
        double d = point.distance(c, destination);
        double traversed = r*speedFactor*delta;

        if (d < traversed) {
            c = destination;
            return;
        }

        point direction = point.unitary( point.sub(destination, c) );
        c = point.sum(c,point.mul(traversed, direction));
    }

    public void pushAside(point p) {
        point pc = point.sub(c,p);
        double distance = point.abs(pc);
        if (distance >= r) return;
        if (distance < 0.0001) return;
        c = point.sum(p, point.mul(r, point.unitary(pc)));
    }

    public void pushAside(Segment segment) {
        point p1 = segment.p1;
        point p2 = segment.p2;
        pushAside(p1);
        pushAside(p2);
        point p1p2 = point.sub(p2,p1);
        point p1c = point.sub(c, p1);
        double scalarProd = point.scalarProd(p1p2, p1c);

        // Hacer que no sean segmentos extendidos hasta infinito
        if (scalarProd <= 0) return;
        if (scalarProd >= point.norm(p1p2)) return;
        point projection = point.sum(p1, point.mul(scalarProd/point.norm(p1p2),p1p2));
        pushAside(projection);
    }

    public void pushAside(SegmentsManager segmentsManager) {
        ArrayList<Segment> list = segmentsManager.list;
        for (Segment s : list) {
            pushAside(s);
        }
    }

    public void pushAside(Polygonal polygon) {
        point p1 = polygon.center;
        point p2 = point.mul(0.9,polygon.center);
        pushAside(p1);
        pushAside(p2);
        point p1p2 = point.sub(p2,p1);
        point p1c = point.sub(c, p1);
        double scalarProd = point.scalarProd(p1p2, p1c);

        // Hacer que no sean segmentos extendidos hasta infinito
        if (scalarProd <= 0) return;
        if (scalarProd >= point.norm(p1p2)) return;
        point projection = point.sum(p1, point.mul(scalarProd/point.norm(p1p2),p1p2));
        pushAside(projection);
    }

    public void pushAside(PolygonalManager polygonsManager) {
        ArrayList<Polygonal> list = polygonsManager.list;
        for (Polygonal s : list) {
            pushAside(s);
        }
    }

    public void move(double delta, SegmentsManager segmentsManager) {
        // Evitar wall tunneling. Nos aseguramos de que no nos movemos mas de r/2
        double traversed = r * speedFactor * delta;
        int steps = (int) (traversed / (r/2.0)) + 1;
        for (int i = 0; i < steps; i++) {
            move(delta/steps);
            pushAside(segmentsManager);
        }
    }

    public void move(double delta, PolygonalManager polygonalManager) {
        // Evitar wall tunneling. Nos aseguramos de que no nos movemos mas de r/2
        double traversed = r * speedFactor * delta;
        int steps = (int) (traversed / (r/2.0)) + 1;
        for (int i = 0; i < steps; i++) {
            move(delta/steps);
            pushAside(polygonalManager);
        }
    }

}
