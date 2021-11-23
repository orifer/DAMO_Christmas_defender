package edu.upc.epsevg.damo.a08_christmas_defender;

import android.graphics.Color;

public class Polygonal {
    point center;
    double radius;
    point[] list;
    int interiorColor;
    int strokeColor;

    Polygonal() {
        int n = 3 + 1 * (int) Math.floor(Math.random() * 8);
        list = new point[n];
        center = new point(Math.random() * 10, Math.random() * 10);
        radius = 0.1 + Math.random() * 0.2;

        for (int i = 0; i < n; i++) {
            list[i] = point.sum(center, point.polar(radius, 2 * Math.PI * i / n));
        }

        interiorColor = Color.argb(255, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
        strokeColor = Color.argb(255, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
    }
}