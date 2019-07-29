package no.appsonite.gpsping.api.content.geo;

import com.google.gson.annotations.SerializedName;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public class GeoPoint {
    private double lat;
    private double lon;
    private long timestamp;
    @SerializedName("heading")
    private int direction;
    @SerializedName("speedKPH")
    private double speed;
    @SerializedName("t_distance")
    private double distanceTravelled;
    @SerializedName("real_dist")
    private double distance;
    @SerializedName("GSM_Signal")
    private String gsmSignal;
    @SerializedName("GPS_Signal")
    private String gpsSignal;
    @SerializedName("attributes")
    private GeoAttributes attributes;
    @SerializedName("daily_track")
    private double dailyTrack;

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

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(double distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public String getGsmSignal() {
        return gsmSignal;
    }

    public void setGsmSignal(String gsmSignal) {
        this.gsmSignal = gsmSignal;
    }

    public String getGpsSignal() {
        return gpsSignal;
    }

    public void setGpsSignal(String gpsSignal) {
        this.gpsSignal = gpsSignal;
    }

    public GeoAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(GeoAttributes attributes) {
        this.attributes = attributes;
    }

    public double getDailyTrack() {
        return dailyTrack;
    }

    public void setDailyTrack(double dailyTrack) {
        this.dailyTrack = dailyTrack;
    }
}
