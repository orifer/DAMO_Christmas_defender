package edu.upc.epsevg.damo.a08_christmas_defender;

public class CameraManager {
    int width, height;
    Point center, right;
    int numFingers;
    Point worldFinger;
    Point worldFinger1;
    Point worldFinger2;

    public CameraManager(int width, int height) {
        center = new Point(0,0);
        right = new Point(1,0);
        this.width = width;
        this.height = height;
    }

    public Point camera2world(Point p) {
        return Point.reference2world(center, right, p);
    }

    public Point world2camera(Point p) {
        return Point.world2reference(center, right, p);
    }

    public void touch() {
        numFingers = 0;
    }

    // Move camera
    public void touch(Point cameraFinger) {
        if (numFingers != 1) {
            numFingers = 1;
            worldFinger = camera2world(cameraFinger);
            return;
        }

        Point worldFingerBis = camera2world(cameraFinger);
        Point move = Point.sub(worldFinger, worldFingerBis);


        // Restrict camera movement
        Point aux = Point.sum(center, move);
        double movementRangeX = 15;
        double movementRangeY = 8.5;
        if ((aux.x < movementRangeX && aux.y < movementRangeY) && (aux.x > -movementRangeX && aux.y > -movementRangeY)) {
            center = aux;
            right = Point.sum(right, move);
        }

    }

    // Zoom camera
    public void touch(Point camerafinger1, Point camerafinger2) {
//        if (numFingers != 2) {
//            numFingers = 2;
//            worldFinger1 = camera2world(camerafinger1);
//            worldFinger2 = camera2world(camerafinger2);
//            return;
//        }
//
//        point centerbis = point.world2reference(camerafinger1, camerafinger2, new point(0,0));
//        point rightbis = point.world2reference(camerafinger1, camerafinger2, new point(1,0));
//
//        // Restrict camera movement
//        point auxC = point.reference2world(worldFinger1,worldFinger2, centerbis);
//        point auxR = point.reference2world(worldFinger1,worldFinger2, rightbis);
//        double distance = point.distance(auxC, auxR);
//        double max = 30;
//        double min = -30;
//
//        if (distance < max && distance > min) {
//            center = auxC;
//            right = auxR;
//        }
    }

    public Point canonic2screen(Point p) {
        return new Point(
                p.x * width / 2 + width / 2,
                height / 2 - p.y * width / 2
        );
    }

    public Point screen2canonic(Point p) {
        return new Point(
                (p.x - width / 2) / width * 2,
                -(p.y - height / 2) / width * 2
        );
    }

    public Point world2screen(Point p) {
        return canonic2screen(world2camera(p));
    }

    public Point screen2world(Point p) {
        return camera2world(screen2canonic(p));
    }

    public double world2ScreenFactor() {
        return Point.distance(
                world2screen(new Point(0, 0)),
                world2screen(new Point(1, 0))
        );
    }


}
