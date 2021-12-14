package edu.upc.epsevg.damo.a08_christmas_defender;

public class Enemy {
    Point center;
    Point destination;
    Constants.EnemyStatus enemyStatus;

    double radius;
    double speedFactor;
    double health;
    int attackDamage;
    double timeToDie;
    static final double timeToDieMax = 3;

    Enemy() {
        double dispersioX = Math.random() * 40;
        double dispersioY = Math.random() * 20;
        int x = (int) (dispersioX - 70);
        int y = (int) (dispersioY - 15);
        center = new Point(x, y);
        destination = new Point(10, y);
        speedFactor = 0.1;
        radius = 50;
        health = 10;
        attackDamage = 1;
        enemyStatus = Constants.EnemyStatus.ALIVE;
    }

}