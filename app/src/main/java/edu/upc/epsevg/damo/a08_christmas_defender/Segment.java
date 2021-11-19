package edu.upc.epsevg.damo.a08_christmas_defender;

public class Segment {

    point p1, p2;

    public Segment() { }

    public Segment(point inp1, point inp2) {
        p1 = inp1;
        p2 = inp2;
    }

    public Segment(double x1, double y1, double x2, double y2) {
        p1 = new point(x1,y1);
        p2 = new point(x2,y2);
    }

}
