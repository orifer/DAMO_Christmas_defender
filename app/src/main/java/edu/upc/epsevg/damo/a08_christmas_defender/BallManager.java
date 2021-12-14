package edu.upc.epsevg.damo.a08_christmas_defender;

import java.util.ArrayList;

public class BallManager {

    private Ball ball;

    public BallManager() {
        this.ball = new Ball();
    }

    // Things to do every tick
    public void onEveryTick(double delta) {
        // Height animation
        if (ball.thereIsDestination && !ball.holdingDown)
            if (point.distance(ball.c, ball.destination) < point.distance(ball.c,constants.BALL_SPAWN))
                ball.r -= 0.05;
            else
                ball.r += 0.05;
    }

    public void move(double delta) {
        if (ball.holdingDown || !ball.thereIsDestination) return;
        double d = point.distance(ball.c, ball.destination);
        double traversed = ball.r*ball.speedFactor*delta;

        if (d < traversed) {
            ball.c = ball.destination;
            restart();
            return;
        }

        point direction = point.unitary( point.sub(ball.destination, ball.c) );
        ball.c = point.sum(ball.c,point.mul(traversed, direction));
    }

    public void pushAside(point p) {
        point pc = point.sub(ball.c,p);
        double distance = point.abs(pc);
        if (distance >= ball.r) return;
        if (distance < 0.0001) return;
        ball.c = point.sum(p, point.mul(ball.r, point.unitary(pc)));
    }

    public void pushAside(Enemy polygon) {
        point p1 = polygon.center;
        point p2 = point.mul(0.9,polygon.center); // Para controlar hitbox
        pushAside(p1);
        pushAside(p2);
        point p1p2 = point.sub(p2,p1);
        point p1c = point.sub(ball.c, p1);
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
            if (point.distance(e.center, ball.c) < ball.r*1) {
                restart();
                p = e;
            }
        }

        if (p != null)
            enemyManager.list.remove(p);
    }

    // Return the ball to the start
    public void restart() {
        ball.c = constants.BALL_SPAWN;
        ball.r = 1;
        ball.thereIsDestination = false;
    }

    public void move(double delta, EnemyManager enemyManager) {
        // Evitar wall tunneling. Nos aseguramos de que no nos movemos mas de r/2
        double traversed = ball.r * ball.speedFactor * delta;
        int steps = (int) (traversed / (ball.r/2.0)) + 1;
        for (int i = 0; i < steps; i++) {
            move(delta/steps);
            pushAside(enemyManager);
        }
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

}
