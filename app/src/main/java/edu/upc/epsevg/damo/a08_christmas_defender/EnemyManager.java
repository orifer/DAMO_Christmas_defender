package edu.upc.epsevg.damo.a08_christmas_defender;

import java.util.ArrayList;

public class EnemyManager {

    ArrayList<Enemy> list;

    public EnemyManager() {
        this.list = new ArrayList<>();
        spawnEnemy();
        spawnEnemy();
        spawnEnemy();
        spawnEnemy();
    }

    private void spawnEnemy() {
        Enemy enemy = new Enemy();
        list.add(enemy);
    }

    public void moveEnemies(double delta) {
        for (Enemy p : list) {
            double traversed = p.radius * p.speedFactor * delta;
            int steps = (int) (traversed / (p.radius/2.0)) + 1;

            for (int i = 0; i < steps; i++) {
                move(p, delta/steps);
            }
        }
    }

    public void move(Enemy p, double delta) {
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
