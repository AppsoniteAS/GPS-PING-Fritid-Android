package no.appsonite.gpsping.api.content.geo;

import java.util.ArrayList;

import no.appsonite.gpsping.model.Friend;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public class GeoItem {
    private Friend user;
    private ArrayList<GeoDevicePoints> devices;

    public Friend getUser() {
        return user;
    }

    public void setUser(Friend user) {
        this.user = user;
    }

    public ArrayList<GeoDevicePoints> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<GeoDevicePoints> devices) {
        this.devices = devices;
    }
}
