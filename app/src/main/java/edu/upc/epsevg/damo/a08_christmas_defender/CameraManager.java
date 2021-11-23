package edu.upc.epsevg.damo.a08_christmas_defender;

public class CameraManager {
    point center, right;
    int numFingers;
    point worldFinger;
    point worldFinger1;
    point worldFinger2;

    public CameraManager() {
        center = new point(0,0);
        right = new point(1,0);
    }

    public point camera2world(point p) {
        return point.reference2world(center, right, p);
    }

    public point world2camera(point p) {
        return point.world2reference(center, right, p);
    }

    public void touch() {
        numFingers = 0;
    }

    public void touch(point cameraFinger) {
        if (numFingers != 1) {
            numFingers = 1;
            worldFinger = camera2world(cameraFinger);
            return;
        }

        point worldFingerBis = camera2world(cameraFinger);
        point move = point.sub(worldFinger, worldFingerBis);


        // Restrict camera movement
        point aux = point.sum(center, move);
        int movementRange = 10;
        if ((aux.x < movementRange && aux.y < movementRange) && (aux.x > -movementRange && aux.y > -movementRange)) {
            center = aux;
            right = point.sum(right, move);
        }

    }

    public void touch(point camerafinger1, point camerafinger2) {
        if (numFingers != 2) {
            numFingers = 2;
            worldFinger1 = camera2world(camerafinger1);
            worldFinger2 = camera2world(camerafinger2);
            return;
        }

        point centerbis = point.world2reference(camerafinger1, camerafinger2, new point(0,0));
        point rightbis = point.world2reference(camerafinger1, camerafinger2, new point(1,0));

        // Restrict camera movement
        point auxC = point.reference2world(worldFinger1,worldFinger2, centerbis);
        point auxR = point.reference2world(worldFinger1,worldFinger2, rightbis);
        double distance = point.distance(auxC, auxR);
        double max = 15;
        double min = 8;

        if (distance < max && distance > min) {
            center = auxC;
            right = auxR;
        }
    }


}
