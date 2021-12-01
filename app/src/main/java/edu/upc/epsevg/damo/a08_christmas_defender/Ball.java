package edu.upc.epsevg.damo.a08_christmas_defender;

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

    public void pushAside(Enemy polygon) {
        point p1 = polygon.center;
        point p2 = point.mul(0.9,polygon.center); // Para controlar hitbox
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

    // Todo: Mover a physicsManager
    public void pushAside(EnemyManager enemyManager) {
        ArrayList<Enemy> list = enemyManager.list;
        Enemy p = null;

        for (Enemy e : enemyManager.list) {
            if (point.distance(e.center, this.c) < this.r*2.5) {
                hit(e);
                p = e;
            }
        }

        if (p != null)
            enemyManager.list.remove(p);
    }

    public void hit(Enemy e) {

        // Reinicia bola
        c = new point(22.5, 0);
        thereIsDestination = false;


    }

    public void move(double delta, EnemyManager enemyManager) {
        // Evitar wall tunneling. Nos aseguramos de que no nos movemos mas de r/2
        double traversed = r * speedFactor * delta;
        int steps = (int) (traversed / (r/2.0)) + 1;
        for (int i = 0; i < steps; i++) {
            move(delta/steps);
            pushAside(enemyManager);
        }
    }

}
