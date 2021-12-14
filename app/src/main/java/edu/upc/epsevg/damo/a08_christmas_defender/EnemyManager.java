package edu.upc.epsevg.damo.a08_christmas_defender;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;

public class EnemyManager {

    MainGame mainGame;
    ArrayList<Enemy> list;
    int remaining;
    int enemiesOnScreen;
    int maxEnemiesOnScreen = 8;

    public EnemyManager(MainGame mainGame) {
        this.mainGame = mainGame;
        this.list = new ArrayList<>();
        spawnEnemies(5);
    }

    // Things to do every tick
    public void onEveryTick(double delta) {
        for (Enemy e : list) {
            if (e.enemyStatus.equals(Constants.EnemyStatus.ALIVE)) {
                moveEnemy(e, delta);
                attack(e, delta);

            } else if (e.enemyStatus.equals(Constants.EnemyStatus.DYING)) {
                animateDeath(e, delta);
            }
        }
        checkHitEnemies();
        spawnEnemies();
    }

    public void checkHitEnemies() {
        Ball ball = mainGame.ballManager.getBall();

        Enemy p = null;

        for (Enemy e : list) {
            if (Point.distance(e.center, ball.c) < ball.r*1) {
                mainGame.ballManager.restart();
                p = e;
            }
        }

        if (p != null) {
            p.enemyStatus = Constants.EnemyStatus.DYING;
            // list.remove(p);
        }
    }

    private void animateDeath(Enemy e, double delta) {
        e.timeToDie += delta;
        int value = (int) e.timeToDie;
        if (value <= Enemy.timeToDieMax) {
            Log.i("DEBUG", "Enemy ded " + value);
        }
    }

    private void spawnEnemies() {
        enemiesOnScreen = list.size();

        if (remaining > 0 && enemiesOnScreen < maxEnemiesOnScreen) {
            list.add(new Enemy());
            remaining--;
        }
    }

    public void spawnEnemies(int enemies) {
        remaining = enemies;
    }

    public void drawEnemy(Canvas canvas, CameraManager cameramanager, Bitmap bitmap, Enemy enemy) {
        int x = (int) cameramanager.world2screen(enemy.center).x;
        int y = (int) cameramanager.world2screen(enemy.center).y;
        int size = 100;
        canvas.drawBitmap(bitmap, null, new RectF(x-size,y-size, x+size, y+size), null);
    }

    private void attack(Enemy e, double delta) {
        // If the enemy has arrived at the destination
        if (Point.distance(e.center, e.destination) < 1) {
            MainGame.takeDamage(e.attackDamage * delta);
        }
    }

    public void moveEnemy(Enemy e, double delta) {
        double traversed = e.radius * e.speedFactor * delta;
        int steps = (int) (traversed / (e.radius/2.0)) + 1;

        for (int i = 0; i < steps; i++) {
            double d = Point.distance(e.center, e.destination);
            double traversedStep = e.radius * e.speedFactor * (delta/steps);

            if (d < traversedStep) {
                e.center = e.destination;
                return;
            }

            Point direction = Point.unitary( Point.sub(e.destination, e.center) );
            e.center = Point.sum(e.center, Point.mul(traversedStep, direction));
        }
    }

}
