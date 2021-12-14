package edu.upc.epsevg.damo.a08_christmas_defender;

public class Constants {
    public static final Point CENTER = new Point(0,0);
    public static final Point BALL_SPAWN = new Point(19.0, 0.3);
    public enum GameStatus{RUNNING, WON, WAITING, LOST}
    public enum EnemyStatus{ALIVE, DYING}
}
