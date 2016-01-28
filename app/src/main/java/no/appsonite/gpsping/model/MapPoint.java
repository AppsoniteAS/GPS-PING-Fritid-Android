package no.appsonite.gpsping.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public class MapPoint {
    private Friend user;
    private double lat;
    private double lon;
    private boolean last;
    private boolean belongsToUser = false;

    public boolean isBelongsToUser() {
        return belongsToUser;
    }

    public void setBelongsToUser(boolean belongsToUser) {
        this.belongsToUser = belongsToUser;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public MapPoint(Friend user, double lat, double lon) {
        this.user = user;
        this.lat = lat;
        this.lon = lon;
    }

    public Friend getUser() {
        return user;
    }

    public void setUser(Friend user) {
        this.user = user;
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

    public LatLng getLatLng() {
        return new LatLng(lat, lon);
    }
}
