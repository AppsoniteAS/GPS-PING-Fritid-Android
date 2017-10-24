package no.appsonite.gpsping.data_structures;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by taras on 10/24/17.
 */

public class ColorData {
    private Map<String, Colors> map;
    private ColorNumber colorNumber;

    public ColorData() {
        map = new HashMap<>();
        colorNumber = new ColorNumber();
    }

    public void add(String imei) {
        if (!map.containsKey(imei)) {
            map.put(imei, colorNumber.getState());
        }
    }

    public Colors get(String imei) {
        return map.get(imei);
    }

    public enum Colors {
        RED, ORANGE, YELLOW, GREEN
    }

    private static class ColorNumber {
        private Colors colors;

        ColorNumber() {
            colors = Colors.RED;
        }

        Colors getState() {
            Colors colors = this.colors;
            incrementState();
            return colors;
        }

        private void incrementState() {
            switch (colors) {
                case RED:
                    colors = Colors.ORANGE;
                    break;
                case ORANGE:
                    colors = Colors.YELLOW;
                    break;
                case YELLOW:
                    colors = Colors.GREEN;
                    break;
                case GREEN:
                    colors = Colors.RED;
            }
        }

    }

}
