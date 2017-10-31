package no.appsonite.gpsping.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.MGRSCoord;
import no.appsonite.gpsping.api.content.geo.GeoAttributes;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public class MapPoint {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Friend user;
    private double lat;
    private double lon;
    private boolean last;
    private boolean belongsToUser = false;
    private String name;
    private String imeiNumber;
    private String trackerNumber;
    private long logTime;

    private boolean mainAvatar;

    private String picUrl;
    private int direction;
    private String gsmSignal;
    private String gpsSignal;
    private GeoAttributes attributes;

    public String getImeiNumber() {
        if (imeiNumber == null)
            return "";
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

    public String getTrackerNumber() {
        if (trackerNumber == null)
            return "";
        return trackerNumber;
    }

    public void setTrackerNumber(String trackerNumber) {
        this.trackerNumber = trackerNumber;
    }

    public static String mgrsFromLatLon(double lat, double lon) {

        Angle latitude = Angle.fromDegrees(lat);

        Angle longitude = Angle.fromDegrees(lon);

        return MGRSCoord
                .fromLatLon(latitude, longitude)
                .toString();
    }

    public String getGRSM() {
        return mgrsFromLatLon(lat, lon);
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

    public MapPoint(Friend user, double lat, double lon, String name, String imeiNumber, String trackerNumber, long logTime,
                    String picUrl, int direction, String gsmSignal, String gpsSignal, GeoAttributes attributes) {
        this.user = user;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.imeiNumber = imeiNumber;
        this.trackerNumber = trackerNumber;
        this.logTime = logTime;
        this.picUrl = picUrl;
        this.direction = direction;
        this.gsmSignal = gsmSignal;
        this.gpsSignal = gpsSignal;
        this.attributes = attributes;
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

    public float getDistanceFor(MapPoint mapPoint) {
        float[] results = new float[1];
        Location.distanceBetween(lat, lon, mapPoint.lat, mapPoint.lon, results);
        return results[0];
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public boolean isMainAvatar() {
        return mainAvatar;
    }

    public void setMainAvatar(boolean mainAvatar) {
        this.mainAvatar = mainAvatar;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
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
}
