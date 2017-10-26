package no.appsonite.gpsping.data_structures;

import no.appsonite.gpsping.enums.DirectionPin;

/**
 * Created by taras on 10/24/17.
 */

public class ArrowLocationPin {
    private float left;
    private float top;
    private float rotate;

    public ArrowLocationPin(DirectionPin direction) {
        switch (direction) {
            case NORTH:
                left = 20;
                top = -4;
                rotate = 0;
                break;
            case SOUTH:
                left = 20;
                top = 44;
                rotate = 180;
                break;
            case WEST:
                left = -4;
                top = 20;
                rotate = 270;
                break;
            case EAST:
                left = 44;
                top = 20;
                rotate = 90;
                break;
            case NORTHWEST:
                left = 0;
                top = 0;
                rotate = 315;
                break;
            case NORTHEAST:
                left = 32;
                top = -4;
                rotate = 45;
                break;
            case SOUTHWEST:
                left = 0;
                top = 32;
                rotate = 225;
                break;
            case SOUTHEAST:
                left = 32;
                top = 32;
                rotate = 135;
                break;
        }
    }

    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    public float getRotate() {
        return rotate;
    }
}
