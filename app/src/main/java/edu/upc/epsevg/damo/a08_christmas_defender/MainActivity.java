package edu.upc.epsevg.damo.a08_christmas_defender;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends Activity {

    int width, height, size;
    LinearLayout linlay;
    ImageView imageView;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    CameraManager cameramanager;
    Ball ball;
    PolygonalManager polygonalManager;
    Handler handler;
    long time;

    Bitmap background;
    Bitmap tree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Main layout
        linlay = new LinearLayout(this);
        linlay.setOrientation(LinearLayout.VERTICAL);
        linlay.setGravity(Gravity.CENTER_HORIZONTAL);
        imageView = new ImageView(this);
        linlay.addView(imageView);

        background = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);
        tree = BitmapFactory.decodeResource(this.getResources(), R.drawable.tree);

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

        paint = new Paint();

        // Camara
        cameramanager = new CameraManager();
        cameramanager.center = new point(0, 0);
        cameramanager.right = new point(10, 0);

        ball = new Ball(new point(0, 0), 1);

        polygonalManager = new PolygonalManager();

        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                //Log.i("TOUCH", event.toString());
                point finger = new point(event.getX(), event.getY());
                point worldFinger = screen2world(finger);

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
                            cameramanager.touch(screen2canonic(finger));
                        }
                        return true;
                    }

                    if (ball.thereIsDestination) {
                        ball.destination = point.sum(ball.c, point.mul(-2, point.sub(worldFinger, ball.c)));
                        ball.speedFactor = point.distance(ball.c, ball.destination) * 2;
                        Log.i("speed: ", Double.toString(ball.speedFactor));
                    } else {
                        cameramanager.touch(screen2canonic(finger));
                    }

                    return true;
                }

                ball.holdingDown = false;

                // Mover camara
                if ((event.getPointerCount() != 1 && event.getPointerCount() != 2) || event.getAction() != MotionEvent.ACTION_MOVE) {
                    cameramanager.touch();
                } else if (event.getPointerCount() == 1) {
                    cameramanager.touch(screen2canonic(finger));
                    imageView.invalidate();
                } else {
                    point finger1 = new point(event.getX(0), event.getY(0));
                    point finger2 = new point(event.getX(1), event.getY(1));
                    cameramanager.touch(screen2canonic(finger1), screen2canonic(finger2));
                }

                return true;
            }
        });

        handler = new Handler();
        time = System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long newTime = System.currentTimeMillis();
                double delta = (newTime - time) / 1000.0;
                time = newTime;
                ball.move(delta, polygonalManager);
                drawAll();
                handler.postDelayed(this, 33);
            }
        }, 33); // 33 ms = 30fps+-

        setContentView(linlay);
        drawAll();

    }

    private point canonic2screen(point p) {
        return new point(
                p.x * width / 2 + width / 2,
                height / 2 - p.y * width / 2
        );
    }

    private point screen2canonic(point p) {
        return new point(
                (p.x - width / 2) / width * 2,
                -(p.y - height / 2) / width * 2
        );
    }

    private point world2screen(point p) {
        return canonic2screen(cameramanager.world2camera(p));
    }

    private point screen2world(point p) {
        return cameramanager.camera2world(screen2canonic(p));
    }

    private double world2ScreenFactor() {
        return point.distance(
                world2screen(new point(0, 0)),
                world2screen(new point(1, 0))
        );
    }

    private void drawPolygonal(Polygonal polygonal) {
        Path path = new Path();
        point[] list = polygonal.list;
        point p = canonic2screen(cameramanager.world2camera(list[0]));
        path.moveTo((int) p.x, (int) p.y);

        for (int i = 1; i < list.length; i++) {
            p = canonic2screen(cameramanager.world2camera(list[i]));
            path.lineTo((int) p.x, (int) p.y);
        }
        path.close();

        paint.setColor(polygonal.interiorColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);

        paint.setColor(polygonal.strokeColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        canvas.drawPath(path, paint);
    }

    private void drawSegment(Segment segment) {
        point p1 = world2screen(segment.p1);
        point p2 = world2screen(segment.p2);
        Path path = new Path();
        path.moveTo((float) p1.x, (float) p1.y);
        path.lineTo((float) p2.x, (float) p2.y);
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawPath(path, paint);
    }

    private void drawAll() {
        canvas.drawColor(Color.WHITE);

        // Draw background
        point center = new point(0,0);
        int x = (int) world2screen(center).x - (background.getWidth()/2);
        int y = (int) world2screen(center).y - (background.getHeight()/2);
        canvas.drawBitmap(background, x , y, paint);

        // Draw stuff
        x = 0;
        y = 1;
        center = new point(x,y);
        x = (int) world2screen(center).x;
        y = (int) world2screen(center).y;
        canvas.drawBitmap(tree, null, new RectF(0, 0, x, y ), null);


        // Draw ball
        point c = world2screen(ball.c);
        double r = ball.r * world2ScreenFactor();

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
            point dest = world2screen(point.sum(ball.c, point.mul(0.3, point.sub(ball.destination, ball.c))));
            point or = world2screen(ball.c);

            paint.setColor(Color.LTGRAY);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(5);
            canvas.drawLine((float) or.x, (float) or.y, (float) dest.x, (float) dest.y, paint);
        }

        // Segments
//        ArrayList<Segment> list = segmentsManager.list;
//        for (Segment s : list) {
//            drawSegment(s);
//        }

        for (Polygonal p : polygonalManager.list) {
            drawPolygonal(p);
        }
//        paint.setColor(Color.YELLOW);
//        paint.setStyle(Paint.Style.FILL);
//        canvas.drawCircle(size/2, size/2, 10, paint);

        imageView.invalidate();

        // Proportional stroke: modulo righ - center

        // Rejilla central
//        paint.setColor(Color.BLACK);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(5);
//
//        point p1 = canonic2screen(cameramanager.world2camera( new point(0,-10) ));
//        point p2 = canonic2screen(cameramanager.world2camera( new point(0, 10) ));
//
//        canvas.drawLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y, paint);
//
//        p1 = canonic2screen(cameramanager.world2camera( new point(-10,0) ));
//        p2 = canonic2screen(cameramanager.world2camera( new point(10, 0) ));
//
//        canvas.drawLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y, paint);
    }

}