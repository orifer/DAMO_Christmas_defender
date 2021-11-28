package edu.upc.epsevg.damo.a08_christmas_defender;

public class Enemy {
    point center;
    point destination;
    double radius;
    double speedFactor;

    Enemy() {
        int x = (int) (Math.random() * 20 - 10);
        int y = (int) (Math.random() * 20 + 20);
        center = new point(x, y);
        destination = new point(x, -15);
        speedFactor = 0.05;
        radius = 50;
    }

}