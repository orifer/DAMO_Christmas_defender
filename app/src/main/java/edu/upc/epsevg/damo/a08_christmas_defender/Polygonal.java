package edu.upc.epsevg.damo.a08_christmas_defender;

import android.graphics.Color;
import android.graphics.RectF;

public class Polygonal {
    point center;
    double radius;
//    point[] list;
//    int interiorColor;
//    int strokeColor;
    point destination;
    double speedFactor;

    Polygonal() {
        int x = (int) (Math.random() * 20 - 10);
        int y = (int) (Math.random() * 20 + 20);
        center = new point(x,y);
        destination = new point(x,-15);
        speedFactor = 0.05;
        radius = 50;

//        int n = 3 + 1 * (int) Math.floor(Math.random() * 8);
//        list = new point[n];
//        radius = 1 + Math.random() * 0.2;
//        for (int i = 0; i < n; i++) {
//            list[i] = point.sum(center, point.polar(radius, 2 * Math.PI * i / n));
//        }

//        interiorColor = Color.argb(255, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
//        strokeColor = Color.argb(255, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
    }
}