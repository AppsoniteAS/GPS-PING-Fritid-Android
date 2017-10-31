package no.appsonite.gpsping.utils;

import no.appsonite.gpsping.enums.DirectionPin;

import static no.appsonite.gpsping.enums.DirectionPin.EAST;
import static no.appsonite.gpsping.enums.DirectionPin.NORTH;
import static no.appsonite.gpsping.enums.DirectionPin.NORTHEAST;
import static no.appsonite.gpsping.enums.DirectionPin.NORTHWEST;
import static no.appsonite.gpsping.enums.DirectionPin.SOUTH;
import static no.appsonite.gpsping.enums.DirectionPin.SOUTHEAST;
import static no.appsonite.gpsping.enums.DirectionPin.SOUTHWEST;
import static no.appsonite.gpsping.enums.DirectionPin.WEST;

/**
 * Created by taras on 10/31/17.
 */

public class CalculateDirection {
    public static DirectionPin getDirection(int direction) {
        if (direction < 23 || direction >= 338) {
            return NORTH;
        } else if (direction >=23 && direction < 68) {
            return NORTHEAST;
        } else if (direction >=68 && direction < 113) {
            return EAST;
        } else if (direction >=113 && direction < 158) {
            return SOUTHEAST;
        } else if (direction >= 158 && direction < 203) {
            return SOUTH;
        } else if (direction >= 203 && direction < 248) {
           return SOUTHWEST;
        } else if (direction >= 248 && direction < 293) {
            return WEST;
        } else if (direction >= 293 && direction < 338) {
            return NORTHWEST;
        } else return NORTH;
    }
}
