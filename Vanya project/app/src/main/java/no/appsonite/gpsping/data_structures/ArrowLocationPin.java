package no.appsonite.gpsping.data_structures;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.enums.DirectionPin;

/**
 * Created by taras on 10/24/17.
 */

public class ArrowLocationPin {
    private int d16;
    private int d24;
    private int d32;
    private float left;
    private float top;
    private float rotate;

    {
        d16 = getDimenSize(R.dimen.d16);
        d24 = getDimenSize(R.dimen.d24);
        d32 = getDimenSize(R.dimen.d32);
    }

    private int getDimenSize(int dimenId) {
        return Application.getContext().getResources().getDimensionPixelSize(dimenId);
    }

    public ArrowLocationPin(DirectionPin direction) {
        switch (direction) {
            case NORTH:
                left = d16;
                top = 0;
                rotate = 0;
                break;
            case SOUTH:
                left = d16;
                top = d32;
                rotate = 180;
                break;
            case WEST:
                left = 0;
                top = d16;
                rotate = 270;
                break;
            case EAST:
                left = d32;
                top = d16;
                rotate = 90;
                break;
            case NORTHWEST:
                left = 0;
                top = 0;
                rotate = 315;
                break;
            case NORTHEAST:
                left = d24;
                top = 0;
                rotate = 45;
                break;
            case SOUTHWEST:
                left = 0;
                top = d24;
                rotate = 225;
                break;
            case SOUTHEAST:
                left = d24;
                top = d24;
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
