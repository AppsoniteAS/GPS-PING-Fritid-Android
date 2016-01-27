package no.appsonite.gpsping.api.content.geo;

import com.google.gson.annotations.SerializedName;

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
}
