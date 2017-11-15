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
    @SerializedName("power")
    private int power;
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

    private int getPowerValidate() {
        int bat = 0;

        if (power >=0 && power <= 6) {
            bat = power;
        } else {
            return bat;
        }

        switch (bat) {
            case 0:
                bat = 0;
                break;
            case 1:
                bat = 15;
                break;
            case 2:
                bat = 30;
                break;
            case 3:
                bat = 45;
                break;
            case 4:
                bat = 60;
                break;
            case 5:
                bat = 80;
                break;
            case 6:
                bat = 100;
                break;
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

//    public int getChargeLevel() {
//        int chargeLevel;
//        if (battery != null) {
//            chargeLevel = getBatteryValidate();
//        } else {
//            chargeLevel = getPowerValidate();
//        }
//        return chargeLevel;
//    }

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
