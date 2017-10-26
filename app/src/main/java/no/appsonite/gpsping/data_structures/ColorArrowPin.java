package no.appsonite.gpsping.data_structures;

import java.util.HashMap;
import java.util.Map;

import no.appsonite.gpsping.enums.ColorPin;

import static no.appsonite.gpsping.enums.ColorPin.GREEN;
import static no.appsonite.gpsping.enums.ColorPin.ORANGE;
import static no.appsonite.gpsping.enums.ColorPin.RED;
import static no.appsonite.gpsping.enums.ColorPin.YELLOW;

/**
 * Created by taras on 10/24/17.
 */

public class ColorArrowPin {
    private Map<String, ColorPin> map;
    private ColorNumber colorNumber;

    public ColorArrowPin() {
        map = new HashMap<>();
        colorNumber = new ColorNumber();
    }

    public void add(String imei) {
        if (!map.containsKey(imei)) {
            map.put(imei, colorNumber.getState());
        }
    }

    public ColorPin get(String imei) {
        return map.get(imei);
    }

    private static class ColorNumber {
        private ColorPin colors;

        ColorNumber() {
            colors = RED;
        }

        ColorPin getState() {
            ColorPin colors = this.colors;
            incrementState();
            return colors;
        }

        private void incrementState() {
            switch (colors) {
                case RED:
                    colors = ORANGE;
                    break;
                case ORANGE:
                    colors = YELLOW;
                    break;
                case YELLOW:
                    colors = GREEN;
                    break;
                case GREEN:
                    colors = RED;
            }
        }

    }

}
