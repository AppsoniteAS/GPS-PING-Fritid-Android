package no.appsonite.gpsping.api.content.geo;

import java.util.ArrayList;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public class GeoDevicePoints {
    private GeoDevice device;
    private ArrayList<GeoPoint> points;

    public GeoDevice getDevice() {
        return device;
    }

    public void setDevice(GeoDevice device) {
        this.device = device;
    }

    public ArrayList<GeoPoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<GeoPoint> points) {
        this.points = points;
    }
}
