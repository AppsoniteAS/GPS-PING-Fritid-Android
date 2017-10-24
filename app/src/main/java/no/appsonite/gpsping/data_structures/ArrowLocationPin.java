package no.appsonite.gpsping.data_structures;

/**
 * Created by taras on 10/24/17.
 */

public class ArrowLocationPin {
    private float left;
    private float top;
    private float rotate;

    public ArrowLocationPin(Direction direction) {
        switch (direction) {
            case NORTH:
                left = 22;
                top = 0;
                rotate = 0;
                break;
            case SOUTH:
                left = 22;
                top = 44;
                rotate = 180;
                break;
            case WEST:
                left = 0;
                top = 22;
                rotate = 270;
                break;
            case EAST:
                left = 44;
                top = 22;
                rotate = 90;
                break;
            case NORTHWEST:
                left = 4;
                top = 4;
                rotate = 315;
                break;
            case NORTHEAST:
                left = 40;
                top = 4;
                rotate = 45;
                break;
            case SOUTHWEST:
                left = 4;
                top = 40;
                rotate = 225;
                break;
            case SOUTHEAST:
                left = 40;
                top = 40;
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

    public enum Direction {
        NORTH, SOUTH, WEST, EAST, NORTHWEST, NORTHEAST, SOUTHWEST, SOUTHEAST
    }
}
