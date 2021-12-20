package edu.upc.epsevg.damo.a08_christmas_defender;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class EnemyManager {

    MainGame mainGame;
    ArrayList<Enemy> list;
    int remaining;
    int enemiesOnScreen;
    int maxEnemiesOnScreen = 8;

    Bitmap snowman;
    Bitmap[] snowman_death;

    public EnemyManager(MainGame mainGame) {
        this.mainGame = mainGame;
        this.list = new ArrayList<>();
        loadBitmaps();
    }

    // Things to do every tick
    public void onEveryTick(double delta) {
        ArrayList<Enemy> enemies = new ArrayList<>(list); // Cloned list
        for (Enemy e : enemies) {
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
        ArrayList<Enemy> enemies = new ArrayList<>(list); // Cloned list

        for (Enemy e : enemies) {
            if ( (Point.distance(e.center, ball.c) < ball.r * 1.5) && (e.enemyStatus == Constants.EnemyStatus.ALIVE) ) {
                mainGame.ballManager.restart();
                e.enemyStatus = Constants.EnemyStatus.DYING;
            }
        }
    }

    private void animateDeath(Enemy e, double delta) {
        e.timeToDie += delta*10;
        int value = (int) e.timeToDie;
        if (value < Enemy.timeToDieMax && value > 0) {
            e.bitmap = snowman_death[value];
        }

        // The animation is over, delete the entity
        else if (value > Enemy.timeToDieMax)
            list.remove(e);
    }

    private void spawnEnemies() {
        enemiesOnScreen = list.size();

        if ( (remaining > 0) && (enemiesOnScreen < maxEnemiesOnScreen) ) {
            list.add(new Enemy(snowman));
            remaining--;
        }
    }

    public void spawnEnemies(int enemies) {
        remaining = enemies;
    }

    public void drawEnemy(Canvas canvas, CameraManager cameramanager, Enemy enemy) {
        int x = (int) cameramanager.world2screen(enemy.center).x;
        int y = (int) cameramanager.world2screen(enemy.center).y;
        int size = 80;
        canvas.drawBitmap(enemy.bitmap, null, new RectF(x-size,y-size, x+size, y+size), null);

        Paint paint = new Paint();

        // Health bar
//        int value = (int) (-enemy.health * 7.65) + 965; // Spaguetti but working :)
//        int green = (int) (255 * ((float) (enemy.health/100)));
//        int red = 255 - green;
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.rgb(red, green, 0));
//        canvas.drawRect(value,mainGame.height - 60,mainGame.height - value, mainGame.height-10, paint);

        // Health bar border
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.BLACK);
//        paint.setStrokeWidth(5);
//        canvas.drawRect(x-size,y-size,x+size, y, paint);

        // Health number
//        paint.setColor(Color.BLACK);
//        paint.setStyle(Paint.Style.FILL);
//        paint.setTextSize(10);
//        canvas.drawText("Health: " + new BigDecimal(enemy.health).setScale(2, RoundingMode.HALF_UP).doubleValue(), (float) width/2 - 150, height - 18, paint);
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

    private void loadBitmaps() {
        snowman = BitmapFactory.decodeResource(mainGame.getResources(), R.drawable.snowman);

        // Death animation
        snowman_death = new Bitmap[13];
        snowman_death[0] = BitmapFactory.decodeResource(mainGame.getResources(), R.drawable.snowman_1);
        snowman_death[1] = BitmapFactory.decodeResource(mainGame.getResources(), R.drawable.snowman_2);
        snowman_death[2] = BitmapFactory.decodeResource(mainGame.getResources(), R.drawable.snowman_3);
        snowman_death[3] = BitmapFactory.decodeResource(mainGame.getResources(), R.drawable.snowman_4);
        snowman_death[4] = BitmapFactory.decodeResource(mainGame.getResources(), R.drawable.snowman_5);
        snowman_death[5] = BitmapFactory.decodeResource(mainGame.getResources(), R.drawable.snowman_6);
        snowman_death[6] = BitmapFactory.decodeResource(mainGame.getResources(), R.drawable.snowman_7);
        snowman_death[7] = BitmapFactory.decodeResource(mainGame.getResources(), R.drawable.snowman_8);
        snowman_death[8] = BitmapFactory.decodeResource(mainGame.getResources(), R.drawable.snowman_9);
        snowman_death[9] = BitmapFactory.decodeResource(mainGame.getResources(), R.drawable.snowman_10);
        snowman_death[10] = BitmapFactory.decodeResource(mainGame.getResources(), R.drawable.snowman_11);
        snowman_death[11] = BitmapFactory.decodeResource(mainGame.getResources(), R.drawable.snowman_12);
        snowman_death[12] = BitmapFactory.decodeResource(mainGame.getResources(), R.drawable.snowman_13);
    }

}
