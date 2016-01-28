package no.appsonite.gpsping.model;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public class MapPoint {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private Friend user;
    private double lat;
    private double lon;
    private boolean last;
    private boolean belongsToUser = false;
    private String name;
    private String imeiNumber;
    private String trackerNumber;
    private long logTime;

    public String getImeiNumber() {
        if(imeiNumber == null)
            return "";
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

    public String getTrackerNumber() {
        if(trackerNumber == null)
            return "";
        return trackerNumber;
    }

    public void setTrackerNumber(String trackerNumber) {
        this.trackerNumber = trackerNumber;
    }

    public String getGRSM() {
        return "";
    }

    public String getLogTime() {
        return simpleDateFormat.format(new Date(logTime * 1000l));
    }

    public void setLogTime(long logTime) {
        this.logTime = logTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public MapPoint(Friend user, double lat, double lon, String name, String imeiNumber, String trackerNumber, long logTime) {
        this.user = user;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.imeiNumber = imeiNumber;
        this.trackerNumber = trackerNumber;
        this.logTime = logTime;
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
