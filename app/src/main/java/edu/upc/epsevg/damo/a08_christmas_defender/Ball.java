package edu.upc.epsevg.damo.a08_christmas_defender;

public class Ball {
    Point c;
    double r;
    boolean thereIsDestination;
    Point destination;
    double speedFactor;
    boolean holdingDown;

    public Ball() {
        c = Constants.BALL_SPAWN;
        r = 1;
        speedFactor = 10;
        thereIsDestination = false;
        holdingDown = false;
    }

    public Ball(Point inc, double inr) {
        c = inc;
        r = inr;
        speedFactor = 10;
        thereIsDestination = false;
        holdingDown = false;
    }

}
