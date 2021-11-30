package edu.upc.epsevg.damo.a08_christmas_defender;

import android.os.Handler;

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

    // Things to do every tick
    public void onEveryTick(double delta) {
        for (Enemy e : list) {
            moveEnemies(e, delta);
            attack(e, delta);
        }
    }

    private void spawnEnemy() {
        Enemy enemy = new Enemy();
        list.add(enemy);
    }

    private void attack(Enemy e, double delta) {
        // If the enemy has arrived at the destination
        if (point.distance(e.center, e.destination) < 1) {
            MainActivity.takeDamage(e.attackDamage * delta);
        }
    }

    public void moveEnemies(Enemy e, double delta) {
        double traversed = e.radius * e.speedFactor * delta;
        int steps = (int) (traversed / (e.radius/2.0)) + 1;

        for (int i = 0; i < steps; i++) {
            double d = point.distance(e.center, e.destination);
            double traversedStep = e.radius * e.speedFactor * (delta/steps);

            if (d < traversedStep) {
                e.center = e.destination;
                return;
            }

            point direction = point.unitary( point.sub(e.destination, e.center) );
            e.center = point.sum(e.center,point.mul(traversedStep, direction));
        }
    }

}
