package edu.upc.epsevg.damo.a08_christmas_defender;

import java.util.ArrayList;

public class PolygonalManager {

    ArrayList<Polygonal> list;

    public PolygonalManager() {
        this.list = new ArrayList<>();
        generatePolygon();
        generatePolygon();
        generatePolygon();
        generatePolygon();
    }

    private void generatePolygon() {
        Polygonal polygonal = new Polygonal();
        list.add(polygonal);
    }
}
