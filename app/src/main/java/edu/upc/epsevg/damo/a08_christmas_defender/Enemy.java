package edu.upc.epsevg.damo.a08_christmas_defender;

public class Enemy {
    point center;
    point destination;
    double radius;
    double speedFactor;

    Enemy() {
        double dispersioX = Math.random() * 40;
        double dispersioY = Math.random() * 20;
        int x = (int) (dispersioX - 70);
        int y = (int) (dispersioY - 15);
        center = new point(x, y);
        destination = new point(20, y);
        speedFactor = 0.05;
        radius = 50;
    }

}