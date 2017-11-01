package no.appsonite.gpsping.api.content.geo;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

/**
 * Created by taras on 10/31/17.
 */

public class GeoAttributes {
    private static DecimalFormat decimalFormat = new DecimalFormat("#.#");
    @SerializedName("battery")
    private String battery;
    @SerializedName("ip")
    private String ip;
    @SerializedName("distance")
    private double distance;
    @SerializedName("totalDistance")
    private double totalDistance;

    public String getBattery() {
        return battery;
    }

    public int getBatteryValidate() {
        if (battery == null) {
            return 0;
        }
        int bat = 0;
        try {
            bat = Integer.parseInt(battery);
        } catch (Exception e) {

        }
        if (bat < 0 || bat > 100) {
            return 0;
        }
        return bat;
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

    public double getDistance() {
        return distance;
    }

    public String getDistanceStr() {
        return decimalFormat.format(distance);
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public String getTotalDistanceStr() {
        return decimalFormat.format(totalDistance);
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }
}
