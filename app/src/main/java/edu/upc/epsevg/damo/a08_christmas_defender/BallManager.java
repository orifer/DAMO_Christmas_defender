package edu.upc.epsevg.damo.a08_christmas_defender;

import java.util.ArrayList;

public class BallManager {

    MainGame mainGame;
    private Ball ball;

    public BallManager(MainGame mainGame) {
        this.mainGame = mainGame;
        this.ball = new Ball();
    }

    // Things to do every tick
    public void onEveryTick(double delta) {
        // Height animation
        if (ball.thereIsDestination && !ball.holdingDown)
            if (Point.distance(ball.c, ball.destination) < Point.distance(ball.c, Constants.BALL_SPAWN))
                ball.r -= 0.05;
            else
                ball.r += 0.05;
    }

    public void move(double delta) {
        if (ball.holdingDown || !ball.thereIsDestination) return;
        double d = Point.distance(ball.c, ball.destination);
        double traversed = ball.r*ball.speedFactor*delta;

        if (d < traversed) {
            ball.c = ball.destination;
            restart();
            return;
        }

        Point direction = Point.unitary( Point.sub(ball.destination, ball.c) );
        ball.c = Point.sum(ball.c, Point.mul(traversed, direction));
    }

    public void pushAside(Point p) {
        Point pc = Point.sub(ball.c,p);
        double distance = Point.abs(pc);
        if (distance >= ball.r) return;
        if (distance < 0.0001) return;
        ball.c = Point.sum(p, Point.mul(ball.r, Point.unitary(pc)));
    }

    public void pushAside(Enemy polygon) {
        Point p1 = polygon.center;
        Point p2 = Point.mul(0.9,polygon.center); // Para controlar hitbox
        pushAside(p1);
        pushAside(p2);
        Point p1p2 = Point.sub(p2,p1);
        Point p1c = Point.sub(ball.c, p1);
        double scalarProd = Point.scalarProd(p1p2, p1c);

        // Hacer que no sean segmentos extendidos hasta infinito
        if (scalarProd <= 0) return;
        if (scalarProd >= Point.norm(p1p2)) return;
        Point projection = Point.sum(p1, Point.mul(scalarProd/ Point.norm(p1p2),p1p2));
        pushAside(projection);
    }

    // Return the ball to the start
    public void restart() {
        ball.c = Constants.BALL_SPAWN;
        ball.r = 1;
        ball.thereIsDestination = false;
    }

    public void move(double delta, EnemyManager enemyManager) {
        // Evitar wall tunneling. Nos aseguramos de que no nos movemos mas de r/2
        double traversed = ball.r * ball.speedFactor * delta;
        int steps = (int) (traversed / (ball.r/2.0)) + 1;
        for (int i = 0; i < steps; i++) {
            move(delta/steps);
        }
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

}
