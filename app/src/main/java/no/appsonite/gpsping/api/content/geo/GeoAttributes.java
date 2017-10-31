package no.appsonite.gpsping.api.content.geo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by taras on 10/31/17.
 */

public class GeoAttributes {
    @SerializedName("battery")
    private String battery;
    @SerializedName("ip")
    private String ip;
    @SerializedName("distance")
    private int distance;
    @SerializedName("totalDistance")
    private double totalDistance;

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }
}
