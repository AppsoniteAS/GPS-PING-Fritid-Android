package no.appsonite.gpsping.data_structures;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by taras on 10/23/17.
 */

public class LatLonData {
    private Set<LatLon> latLonSet;

    public LatLonData() {
        latLonSet = new HashSet<>();
    }

    public void clear() {
        latLonSet.clear();
    }

    public boolean contains(double lat, double lon) {
        return latLonSet.contains(new LatLon(lat, lon));
    }

    public void add(double lat, double lon) {
        latLonSet.add(new LatLon(lat, lon));
    }

    private static class LatLon {
        private double lat;
        private double lon;

        LatLon(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (!(obj instanceof LatLon)) return false;

            LatLon latLon = (LatLon) obj;
            return lat == latLon.getLat() && lon == latLon.getLon();
        }

        @Override
        public int hashCode() {
            return (int) ((int) lat * lon);
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }
    }
}
