package edu.upc.epsevg.damo.a08_christmas_defender;

import android.graphics.Bitmap;

public class Enemy {
    Point center;
    Point destination;
    Constants.EnemyStatus enemyStatus;

    double radius;
    double speedFactor;
    double health;
    Bitmap bitmap;
    int attackDamage;
    double timeToDie;
    static final double timeToDieMax = 13; // Number of frames of the death animation

    Enemy(Bitmap bitmap) {
        double dispersioX = Math.random() * 40;
        double dispersioY = Math.random() * 20;
        int x = (int) (dispersioX - 70);
        int y = (int) (dispersioY - 15);

        this.center = new Point(x, y);
        this.destination = new Point(10, y);
        this.speedFactor = 0.1;
        this.radius = 50;
        this.health = 10;
        this.attackDamage = 1;
        this.enemyStatus = Constants.EnemyStatus.ALIVE;
        this.bitmap = bitmap;
    }

}