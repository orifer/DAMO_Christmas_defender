package edu.upc.epsevg.damo.a08_christmas_defender;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    int width, height, size;
    LinearLayout linlay;
    ImageView imageView;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    CameraManager cameramanager;
    dialogManager dialogManager;
    Ball ball;
    EnemyManager enemyManager;
    Handler handler;
    long time;

    Bitmap background;
    Bitmap tree;
    Bitmap snowman;
    int health;
    double delta;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        manageScreenLayout();
        paint = new Paint();

        // Load Bitmaps
        background = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);
        tree = BitmapFactory.decodeResource(this.getResources(), R.drawable.tree);
        snowman = BitmapFactory.decodeResource(this.getResources(), R.drawable.evil_snowman);

        // Camera
        int cameraZoom = 30;
        cameramanager = new CameraManager(width, height);
        cameramanager.center = new point(0, 0);
        cameramanager.right = new point(cameraZoom, 0);

        // Init game elements
        dialogManager = new dialogManager(MainActivity.this);
        enemyManager = new EnemyManager();
        ball = new Ball(new point(20, 0), 1);
        health = 100;
        handleMovement();

        // Dialogs
        prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        prefs.edit().putBoolean("isShown", false).apply();

        // Timing
        handler = new Handler();
        time = System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long newTime = System.currentTimeMillis();
                delta = (newTime - time) / 1000.0;
                time = newTime;

                ball.move(delta, enemyManager);
                enemyManager.moveEnemies(delta);
                checkGameStatus();

                drawAll();
                handler.postDelayed(this, 33);
            }
        }, 33); // 33 ms = 30fps+-
        setContentView(linlay);
        drawAll();
    }

    private void checkGameStatus() {
        if(!(prefs.getBoolean("isShown", false))) {

            // LOST
            if (health == 0) {
                prefs.edit().putBoolean("isShown", true).apply();
                dialogManager.showLosePopup();
            }

            // WIN
            else if (enemyManager.list.isEmpty() && health > 0) {
                prefs.edit().putBoolean("isShown", true).apply();
                dialogManager.showWinPopup();
            }
        }
    }

    private void drawAll() {
        canvas.drawColor(Color.WHITE);

        // Draw background
        point center = new point(0,0);
        int x = (int) cameramanager.world2screen(center).x - (background.getWidth()/2);
        int y = (int) cameramanager.world2screen(center).y - (background.getHeight()/2);
        canvas.drawBitmap(background, x , y, paint);

        // Draw enemies
        for (Enemy p : enemyManager.list) {
            drawEnemy(p);
        }

        drawHUD();
        drawBall();

        imageView.invalidate();
        // Proportional stroke: modulo righ - center
    }

    private void drawBall() {
        point c = cameramanager.world2screen(ball.c);
        double r = ball.r * cameramanager.world2ScreenFactor();

        // Draw base color
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle((int) c.x, (int) c.y, (float) r, paint);

        // Draw border
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        canvas.drawCircle((int) c.x, (int) c.y, (float) r, paint);

        // Linea de disparo
        if (ball.holdingDown) {
            point dest = cameramanager.world2screen(point.sum(ball.c, point.mul(0.3, point.sub(ball.destination, ball.c))));
            point or = cameramanager.world2screen(ball.c);

            paint.setColor(Color.LTGRAY);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(5);
            canvas.drawLine((float) or.x, (float) or.y, (float) dest.x, (float) dest.y, paint);
        }
    }

    private void drawEnemy(Enemy enemy) {
        int x = (int) cameramanager.world2screen(enemy.center).x;
        int y = (int) cameramanager.world2screen(enemy.center).y;
        int size = 100;
        canvas.drawBitmap(snowman, null, new RectF(x-size,y-size, x+size, y+size), null);
        //canvas.drawCircle((int) x, (int) y, (float) polygonal.radius, paint);
    }

    private void drawHUD() {
        // Waves
        int textSize = 100;
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);
        canvas.drawText("WAVE 1", (float) width/2 - textSize, textSize, paint);

        // Health
        paint.setTextSize(70);
        canvas.drawText("Health: " + health, (float) width/2 - 180, height - 10, paint);

        // Delta timing
        paint.setTextSize(30);
        paint.setColor(Color.RED);
        canvas.drawText("Δ " + delta , 10, 25, paint);
    }

    private void manageScreenLayout() {
        // Fullscreen landscape
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Main layout
        linlay = new LinearLayout(this);
        linlay.setOrientation(LinearLayout.VERTICAL);
        linlay.setGravity(Gravity.CENTER_HORIZONTAL);
        imageView = new ImageView(this);
        linlay.addView(imageView);

        // Screen
        Point myPoint = new Point();
        getWindowManager().getDefaultDisplay().getSize(myPoint);
        width = myPoint.x;
        height = myPoint.y;
        size = width;
        imageView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        imageView.setImageBitmap(bitmap);
        canvas.setBitmap(bitmap);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void handleMovement() {
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                point finger = new point(event.getX(), event.getY());
                point worldFinger = cameramanager.screen2world(finger);

                // Mover bola
                if (event.getPointerCount() == 1 &&
                (event.getAction() == MotionEvent.ACTION_MOVE ||
                event.getAction() == MotionEvent.ACTION_DOWN)) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (point.distance(worldFinger, ball.c) < 3 * ball.r) {
                            ball.holdingDown = true;
                            ball.thereIsDestination = true;
                            ball.destination = worldFinger;
                        } else {
                            ball.thereIsDestination = false;
                            cameramanager.touch(cameramanager.screen2canonic(finger));
                        }
                        return true;
                    }

                    if (ball.thereIsDestination) {
                        ball.destination = point.sum(ball.c, point.mul(-2, point.sub(worldFinger, ball.c)));
                        ball.speedFactor = point.distance(ball.c, ball.destination) * 2;
                    } else {
                        cameramanager.touch(cameramanager.screen2canonic(finger));
                    }

                    return true;
                }

                ball.holdingDown = false;

                // Mover camara
                if ((event.getPointerCount() != 1 && event.getPointerCount() != 2) || event.getAction() != MotionEvent.ACTION_MOVE) {
                    cameramanager.touch();
                } else if (event.getPointerCount() == 1) {
                    cameramanager.touch(cameramanager.screen2canonic(finger));
                    imageView.invalidate();
                } else {
                    point finger1 = new point(event.getX(0), event.getY(0));
                    point finger2 = new point(event.getX(1), event.getY(1));
                    cameramanager.touch(cameramanager.screen2canonic(finger1), cameramanager.screen2canonic(finger2));
                }

                return true;
            }
        });
    }

}