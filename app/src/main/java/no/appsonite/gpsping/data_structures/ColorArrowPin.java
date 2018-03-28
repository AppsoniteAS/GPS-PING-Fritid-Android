package no.appsonite.gpsping.data_structures;

import java.util.HashMap;
import java.util.Map;

import no.appsonite.gpsping.enums.ColorPin;

import static no.appsonite.gpsping.enums.ColorPin.ORANGE;
import static no.appsonite.gpsping.enums.ColorPin.RED;

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

    public void add(String name) {
        if (!map.containsKey(name)) {
            map.put(name, colorNumber.getState());
        }
    }

    public ColorPin get(String imei) {
        return map.get(imei);
    }

    private static class ColorNumber {
        private ColorPin colorPin;

        ColorNumber() {
            colorPin = RED;
        }

        ColorPin getState() {
            ColorPin colors = this.colorPin;
            incrementState();
            return colors;
        }

        private void incrementState() {
            if (colorPin == RED) {
                colorPin = ORANGE;
            } else {
                colorPin = RED;
            }
        }
    }

}
