package no.appsonite.gpsping.api.content.geo;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;

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

    private int getBatteryValidate() {
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

    public int getChargeLevel() {
        if (battery == null) {
            return 0;
        } else {
            return getBatteryValidate();
        }
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
        if (distance > 1000) {
            return decimalFormat.format(distance / 1000) + Application.getContext().getResources().getString(R.string.kilometers);
        }
        return decimalFormat.format(distance) + Application.getContext().getResources().getString(R.string.meters);
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public String getTotalDistanceStr() {
        if (totalDistance > 1000) {
            return decimalFormat.format(totalDistance / 1000) + Application.getContext().getResources().getString(R.string.kilometers);
        }
        return decimalFormat.format(totalDistance) + Application.getContext().getResources().getString(R.string.meters);
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }
}
