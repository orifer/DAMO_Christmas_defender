package edu.upc.epsevg.damo.a08_christmas_defender;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainGame extends Activity {

    int width, height, size;
    LinearLayout linlay;
    ImageView imageView;
    Handler handler;
    Canvas canvas;
    Paint paint;

    CameraManager cameramanager;
    dialogManager dialogManager;
    EnemyManager enemyManager;

    Ball ball;
    Bitmap background, slingshot, snowman;
    SharedPreferences prefs;
    point worldFinger;
    static double health;
    double delta;
    long time;
    int wave;

    enum GameStatus{RUNNING, WON, WAITING, LOST}
    GameStatus gameStatus;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        manageScreenLayout();
        paint = new Paint();
        loadBitmaps();

        // Camera
        int cameraZoom = 30;
        cameramanager = new CameraManager(width, height);
        cameramanager.center = constants.CENTER;
        cameramanager.right = new point(cameraZoom, 0);

        // Init game elements
        gameStatus = GameStatus.RUNNING;
        dialogManager = new dialogManager(MainGame.this);
        enemyManager = new EnemyManager();
        ball = new Ball();
        health = 100;
        wave = 1;
        handleMovement();

        // Dialogs
        prefs = PreferenceManager.getDefaultSharedPreferences(MainGame.this);
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
                enemyManager.onEveryTick(delta);
                ball.onEveryTick(delta);
                checkGameStatus();

                drawAll();
                handler.postDelayed(this, 33);
            }
        }, 33); // 33 ms = 30fps+-
        setContentView(linlay);
    }

    private void loadBitmaps() {
        Bitmap background_base = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(background_base, (int) (width*1.5), (int) (height*1.5), false);

        snowman = BitmapFactory.decodeResource(this.getResources(), R.drawable.evil_snowman);
        slingshot = BitmapFactory.decodeResource(this.getResources(), R.drawable.slingshoot);
    }

    private void checkGameStatus() {
        switch (gameStatus) {

            // Playing the game normally
            case RUNNING:
                // Check if the player lost the wave
                if (health == 0)
                    gameStatus = GameStatus.LOST;

                // Check if you completed the wave
                if (enemyManager.list.isEmpty() && health > 0)
                    gameStatus = GameStatus.WON;
                break;


            // You passed this wave, wait for the next one
            case WON:
                // Spawn the next wave after the countdown and puts you to wait
                gameStatus = GameStatus.WAITING;
                wave++;
                handler.postDelayed(() -> {
                    enemyManager.spawnEnemies(10 + (wave * 2));
                    gameStatus = GameStatus.RUNNING;
                }, 6000);
                break;


            // You lost haha
            case LOST:
                // Show the shame dialog
                if(!(prefs.getBoolean("isShown", false))) {
                    prefs.edit().putBoolean("isShown", true).apply();
                    dialogManager.showLosePopup();
                }
                break;


            // Waiting for the next wave to spawn
            case WAITING:
                break;
        }
    }

    private void drawAll() {
        canvas.drawColor(Color.WHITE);

        // Draw background
        int x = (int) (cameramanager.world2screen(constants.CENTER).x - ((int) (width*1.5)/2));
        int y = (int) (cameramanager.world2screen(constants.CENTER).y - ((int) (height*1.5)/2));
        canvas.drawBitmap(background, x , y, paint);

        // Draw enemies
        for (Enemy e : enemyManager.list) {
            enemyManager.drawEnemy(canvas, cameramanager, snowman, e);
        }

        drawHUD();
        drawBall();

        imageView.invalidate();
        // Proportional stroke: modulo righ - center
    }

    private void drawBall() {
        point c = cameramanager.world2screen(ball.c);
        double r = ball.r * cameramanager.world2ScreenFactor();

        // Draw slingshot base
        point p = cameramanager.world2screen(constants.BALL_SPAWN);
        Bitmap rotatedSlingshot = rotateBitmap(slingshot, (float) -point.angle(constants.BALL_SPAWN, ball.c));
        canvas.drawBitmap(rotatedSlingshot, (int) p.x - (rotatedSlingshot.getWidth()/2) , (int) p.y - (rotatedSlingshot.getHeight()/2), paint);

        // Draw strings
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        if ((point.distance(constants.BALL_SPAWN, ball.c) < 8) && (worldFinger != null)) {
            point or = cameramanager.world2screen(ball.c);
            point u = point.orthogonal(point.unitary(point.sub(worldFinger,constants.BALL_SPAWN)));
            double slingshotRadius = 2;
            point top = cameramanager.world2screen( point.sum(point.mul(slingshotRadius,u) , constants.BALL_SPAWN) );
            point bottom = cameramanager.world2screen( point.sum(point.mul(-slingshotRadius,u) , constants.BALL_SPAWN) );

            canvas.drawLine((float) or.x, (float) or.y, (float) top.x, (float) top.y, paint);
            canvas.drawLine((float) or.x, (float) or.y, (float)  bottom.x, (float) bottom.y, paint);
        } else {
            point top = cameramanager.world2screen(point.sum(constants.BALL_SPAWN, new point(0, 2.5)));
            point bottom = cameramanager.world2screen(point.sum(constants.BALL_SPAWN, new point(0, -2.5)));
            canvas.drawLine((float) bottom.x, (float) bottom.y, (float) top.x, (float) top.y, paint);
        }

        // Draw base color
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle((int) c.x, (int) c.y, (float) r, paint);

        // Draw border
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        canvas.drawCircle((int) c.x, (int) c.y, (float) r, paint);
    }

    private void drawHUD() {
        // Waves
        int textSize = 100;
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);
        canvas.drawText("WAVE " + wave, (float) width/2 - textSize, textSize, paint);

        // Health
        paint.setTextSize(70);
        canvas.drawText("Health: " + new BigDecimal(health).setScale(2, RoundingMode.HALF_UP).doubleValue(), (float) width/2 - 180, height - 10, paint);

        // Delta timing
        paint.setTextSize(30);
        paint.setColor(Color.RED);
        canvas.drawText("Δ " + delta , 10, 25, paint);
    }

    private void manageScreenLayout() {
        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Remove notification bar
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
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        imageView.setImageBitmap(bitmap);
        canvas.setBitmap(bitmap);
    }

    // Algo temporal
    static void takeDamage(double damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    private static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void handleMovement() {
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                point finger = new point(event.getX(), event.getY());
                worldFinger = cameramanager.screen2world(finger);

                // Mover bola
                if (event.getPointerCount() == 1 &&
                (event.getAction() == MotionEvent.ACTION_MOVE ||
                event.getAction() == MotionEvent.ACTION_DOWN)) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        // Move ball
                        if (point.distance(worldFinger, ball.c) < 3 * ball.r) {
                            ball.holdingDown = true;
                            ball.thereIsDestination = true;
                            ball.destination = worldFinger;

                        // Move camera
                        } else {
                            ball.thereIsDestination = false;
                            cameramanager.touch(cameramanager.screen2canonic(finger));
                        }
                        return true;
                    }

                    // Move ball
                    if (ball.thereIsDestination) {
                        ball.destination = point.sum(ball.c, point.mul(10, point.sub(constants.BALL_SPAWN, ball.c)));
                        ball.speedFactor = point.distance(ball.c, ball.destination) * 0.5;

                        // Max drag distance of the ball
                        int dist = 7;
                        if (point.distance(constants.BALL_SPAWN, worldFinger) < dist)
                            ball.c = worldFinger;
                        else
                            ball.c = point.sum(constants.BALL_SPAWN, point.mul(dist,point.unitary(point.sub(worldFinger,constants.BALL_SPAWN))));

                    // Move camera
                    } else {
                        cameramanager.touch(cameramanager.screen2canonic(finger));
                    }

                    return true;
                }

                ball.holdingDown = false;

                // Move camera and zoom
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