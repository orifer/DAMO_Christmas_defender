package edu.upc.epsevg.damo.a08_christmas_defender;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int width, height, size;
    LinearLayout linlay;
    ImageView imageView;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    CameraManager cameramanager;
    Ball ball;
    SegmentsManager segmentsManager;
    Handler handler;
    long time;
    final int n = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        linlay = new LinearLayout(this);
        linlay.setOrientation(LinearLayout.VERTICAL);
        linlay.setGravity(Gravity.CENTER_HORIZONTAL);
        imageView = new ImageView(this);
        linlay.addView(imageView);

        Point myPoint = new Point();
        getWindowManager().getDefaultDisplay().getSize(myPoint);
        width = myPoint.x;
        height = myPoint.y;
        size = (int) (width * 0.9);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        imageView.setImageBitmap(bitmap);
        canvas.setBitmap(bitmap);

        paint = new Paint();

        cameramanager = new CameraManager();
        cameramanager.center = new point(n/2.0, n/2.0);
        cameramanager.right = new point(n, n/2.0);

        ball = new Ball(new point(0.5,0.5),0.3);
        segmentsManager = new SegmentsManager(n);

        Button bt = new Button(this);
        bt.setText("New Polygonal");
        bt.setTextSize(20);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Poligonal polygonal = new Poligonal();
                //poligonals.add(polygonal);
                drawAll();
            }
        });
        linlay.addView(bt);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Log.i("TOUCH", event.toString());
                point finger = new point(event.getX(), event.getY());
                point worldFinger = screen2world(finger);

                if (event.getPointerCount() == 1 &&
                        (event.getAction() == MotionEvent.ACTION_MOVE ||
                                event.getAction() == MotionEvent.ACTION_DOWN)) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (point.distance(worldFinger, ball.c) < 3*ball.r) {
                            ball.thereIsDestination = true;
                            ball.destination = worldFinger;
                        } else {
                            ball.thereIsDestination = false;
                            cameramanager.touch(screen2canonic(finger));
                        }
                        return true;
                    }

                    if (ball.thereIsDestination) {
                        ball.destination = worldFinger;
                    } else {
                        cameramanager.touch(screen2canonic(finger));
                    }

                    return true;
                }

                ball.thereIsDestination = false;

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
                double delta = (newTime - time)/1000.0;
                time = newTime;
                ball.move(delta, segmentsManager);
                drawAll();
                handler.postDelayed(this, 50);
            }
        }, 50);

        setContentView(linlay);
        drawAll();

    }

    private point canonic2screen(point p) {
        return new point(
                p.x * size/2 + size/2,
                size/2 - p.y * size/2
        );
    }

    private point screen2canonic(point p) {
        return new point(
                (p.x - size/2) / size * 2,
                -(p.y - size/2) / size * 2
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
                world2screen(new point(0,0)),
                world2screen(new point(1 ,0))
        );
    }

//    private void drawPolygonal(Poligonal poligonal) {
//        Path path = new Path();
//        point[] list = poligonal.list;
//        point p = canonic2screen(cameramanager.world2camera(list[0]));
//        path.moveTo((int) p.x, (int) p.y);
//
//        for (int i = 1; i < list.length; i++) {
//            p = canonic2screen(cameramanager.world2camera(list[i]));
//            path.lineTo((int) p.x, (int) p.y);
//        }
//        path.close();
//
//        paint.setColor(poligonal.interiorColor);
//        paint.setStyle(Paint.Style.FILL);
//        canvas.drawPath(path, paint);
//
//        paint.setColor(poligonal.strokeColor);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(20);
//        canvas.drawPath(path, paint);
//    }

    private void drawSegment(Segment segment) {
        point p1 = world2screen(segment.p1);
        point p2 = world2screen(segment.p2);
        Path path = new Path();
        path.moveTo( (float)p1.x, (float)p1.y );
        path.lineTo( (float)p2.x, (float)p2.y );
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawPath(path, paint);
    }

    private void drawAll() {
        canvas.drawColor(Color.BLACK);

        point c = world2screen(ball.c);
        double r = ball.r * world2ScreenFactor();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle((int) c.x, (int) c.y, (float) r, paint);

        // Segments
        ArrayList<Segment> list = segmentsManager.list;
        for (Segment s : list) {
            drawSegment(s);
        }

        // Linea uwu
        if (ball.thereIsDestination) {
            point dest = world2screen( point.sum(ball.c, point.mul(ball.r, point.unitary(point.sub(ball.destination, ball.c)))) );
            point or = world2screen(ball.c);

            paint.setColor(Color.LTGRAY);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(5);
            canvas.drawLine( (float) or.x, (float) or.y, (float) dest.x, (float) dest.y, paint);
        }

//        for (Poligonal p : poligonals) {
//            drawPolygonal(p);
//        }
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