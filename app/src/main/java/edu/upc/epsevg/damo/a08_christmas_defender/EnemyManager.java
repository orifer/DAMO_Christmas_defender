package edu.upc.epsevg.damo.a08_christmas_defender;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.ArrayList;

public class EnemyManager {

    ArrayList<Enemy> list;

    public EnemyManager() {
        this.list = new ArrayList<>();
        spawnEnemies(5);
    }

    // Things to do every tick
    public void onEveryTick(double delta) {
        for (Enemy e : list) {
            moveEnemies(e, delta);
            attack(e, delta);
        }
    }

    public void spawnEnemies(int enemies) {
        for (int i = 0; i < enemies; i++)
            list.add(new Enemy());
    }

    public void drawEnemy(Canvas canvas, CameraManager cameramanager, Bitmap bitmap, Enemy enemy) {
        int x = (int) cameramanager.world2screen(enemy.center).x;
        int y = (int) cameramanager.world2screen(enemy.center).y;
        int size = 100;
        canvas.drawBitmap(bitmap, null, new RectF(x-size,y-size, x+size, y+size), null);
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
