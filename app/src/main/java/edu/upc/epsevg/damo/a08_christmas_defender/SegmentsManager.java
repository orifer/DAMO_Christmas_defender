package edu.upc.epsevg.damo.a08_christmas_defender;

import java.util.ArrayList;

public class SegmentsManager {

    ArrayList<Segment> list;
    int n;

    private class Cell {
        int i,j;
        Cell identifier;
        boolean right, top;
        public Cell() {}
        public Cell(int ini, int inj) {
            identifier = this;
            i = ini;
            j = inj;
            right = true;
            top = true;
        }
    }

    private void recomputeIdentifier(Cell  cell) {
        if (cell.identifier == cell) return;
        recomputeIdentifier(cell.identifier);
        cell.identifier = cell.identifier.identifier;
    }

    private class Wall {
        Cell cell;
        int side; // 0 means right, 1 means top.

        public Wall(Cell incell, int inside) {
            cell = incell;
            side = inside;
        }
    }

    Cell[][] table;

    private void generateCells() {
        table = new Cell[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                table[i][j] = new Cell(i,j);
            }
        }

        ArrayList<Wall> listWalls = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i<n-1) listWalls.add(new Wall(table[i][j],0)); // Add right wall
                if (j<n-1) listWalls.add(new Wall(table[i][j],1)); // Add top wall
            }
        }

        for (int iwall = 1; iwall < listWalls.size(); iwall++) {
            int iOtherWall = (int) (Math.random()*(iwall+1));
            Wall wall = listWalls.get(iwall);
            Wall otherWall = listWalls.get(iOtherWall);
            listWalls.set(iwall, otherWall);
            listWalls.set(iOtherWall, wall);
        }

        for (int iwall = 0; iwall < listWalls.size(); iwall++) {
            Wall wall = listWalls.get(iwall);
            Cell cell = wall.cell;
            int side = wall.side;
            int i = cell.i;
            int j = cell.j;
            Cell neightborCell;
            if (side==0) neightborCell = table[i+1][j];
            else neightborCell = table[i][j+1];

            recomputeIdentifier(cell);
            recomputeIdentifier(neightborCell);
            Cell idCell = cell.identifier;
            Cell idNeightborCell = neightborCell.identifier;

            if (idCell == idNeightborCell) continue;
            idCell.identifier = idNeightborCell;
            if (side == 0) cell.right = false;
            else cell.top = false;
        }
    }

    private void generateSegments() {
        list = new ArrayList<>();
        list.add(new Segment(0,0,n,0));
        list.add(new Segment(0,0,0,n));

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Cell cell = table[i][j];
                if (cell.right)
                    list.add(new Segment(i+1, j, i+1, j+1));
                if (cell.top)
                    list.add(new Segment(i, j+1, i+1, j+1));
            }
        }
    }

    public SegmentsManager() {
        list = new ArrayList<>();
        list.add( new Segment( new point(0.5,0), new point(0.5,0.5)));
        list.add( new Segment( new point(-0.5,0), new point(0,0.5)));
    }

    public SegmentsManager(int inn) {
        n = inn;
        generateCells();
        generateSegments();
    }

}
