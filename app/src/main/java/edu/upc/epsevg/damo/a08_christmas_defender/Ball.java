package edu.upc.epsevg.damo.a08_christmas_defender;

import java.util.ArrayList;

public class Ball {
    point c;
    double r;
    boolean thereIsDestination;
    point destination;
    double speedFactor;
    boolean holdingDown;

    public Ball() {
        c = constants.BALL_SPAWN;
        r = 1;
        speedFactor = 10;
        thereIsDestination = false;
        holdingDown = false;
    }

    public Ball(point inc, double inr) {
        c = inc;
        r = inr;
        speedFactor = 10;
        thereIsDestination = false;
        holdingDown = false;
    }

}
