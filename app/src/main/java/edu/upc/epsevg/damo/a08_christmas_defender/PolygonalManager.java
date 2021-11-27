package edu.upc.epsevg.damo.a08_christmas_defender;

import android.util.Log;

import java.util.ArrayList;

public class PolygonalManager {

    ArrayList<Polygonal> list;

    public PolygonalManager() {
        this.list = new ArrayList<>();
        generatePolygon();
        generatePolygon();
        generatePolygon();
        generatePolygon();
    }

    private void generatePolygon() {
        Polygonal polygonal = new Polygonal();
        list.add(polygonal);
    }

    public void movePolygonals(double delta) {
        for (Polygonal p : list) {
            double traversed = p.radius * p.speedFactor * delta;
            int steps = (int) (traversed / (p.radius/2.0)) + 1;

            for (int i = 0; i < steps; i++) {
                move(p, delta/steps);
            }
        }
    }

    public void move(Polygonal p, double delta) {
        double d = point.distance(p.center, p.destination);
        double traversed = p.radius*p.speedFactor*delta;

        if (d < traversed) {
            p.center = p.destination;
            return;
        }

        point direction = point.unitary( point.sub(p.destination, p.center) );
        p.center = point.sum(p.center,point.mul(traversed, direction));
    }
}
