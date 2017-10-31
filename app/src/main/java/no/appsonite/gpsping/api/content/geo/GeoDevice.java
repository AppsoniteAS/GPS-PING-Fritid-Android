package no.appsonite.gpsping.api.content.geo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
    private int heading;
    @SerializedName("GSM_Signal")
    private String gsmSignal;
    @SerializedName("GPS_Signal")
    private String gpsSignal;
    private List<GeoAttributes> geoAttributes;

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

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
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

    public List<GeoAttributes> getGeoAttributes() {
        return geoAttributes;
    }

    public void setGeoAttributes(List<GeoAttributes> geoAttributes) {
        this.geoAttributes = geoAttributes;
    }
}
