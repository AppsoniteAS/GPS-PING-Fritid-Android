package no.appsonite.gpsping.api.content.geo;

import com.google.gson.annotations.SerializedName;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public class GeoDevice {
    private String name;
    @SerializedName("last_lat")
    private double lastLat;
    @SerializedName("last_lon")
    private double lastLon;
    @SerializedName("last_time_stamp")
    private long lastTimestamp;
    @SerializedName("last_update")
    private long lastUpdate;
    @SerializedName("tracker_number")
    public String trackerNumber;
    @SerializedName("imei_number")
    public String imeiNumber;
    @SerializedName("picUrl")
    private String picUrl;
    @SerializedName("heading")
    private int direction;
    @SerializedName("speedKPH")
    private double speed;
    @SerializedName("GSM_Signal")
    private String gsmSignal;
    @SerializedName("GPS_Signal")
    private String gpsSignal;
    @SerializedName("attributes")
    private GeoAttributes attributes;
    @SerializedName("t_distance")
    private double distance;
    @SerializedName("real_dist")
    private double distanceTravelled;

    public String getTrackerNumber() {
        return trackerNumber;
    }

    public void setTrackerNumber(String trackerNumber) {
        this.trackerNumber = trackerNumber;
    }

    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLastLat() {
        return lastLat;
    }

    public void setLastLat(double lastLat) {
        this.lastLat = lastLat;
    }

    public double getLastLon() {
        return lastLon;
    }

    public void setLastLon(double lastLon) {
        this.lastLon = lastLon;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
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
}
