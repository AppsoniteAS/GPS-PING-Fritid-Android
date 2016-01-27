package no.appsonite.gpsping.api.content.geo;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public class GeoPoint {
    private double lat;
    private double lon;
    private long timestamp;

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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
