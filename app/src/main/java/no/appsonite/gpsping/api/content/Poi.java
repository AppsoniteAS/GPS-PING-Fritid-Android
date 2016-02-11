package no.appsonite.gpsping.api.content;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.model.MapPoint;
import no.appsonite.gpsping.utils.ObservableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 10.02.2016
 */
public class Poi implements Serializable {
    private long id = -1;
    private double lat;
    private double lon;
    public ObservableString name = new ObservableString("");
    private Friend user;
    @SerializedName("user_id")
    private long userId;
    private long myId = AuthHelper.getCredentials().getUser().id.get();

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getGRSM() {
        return MapPoint.mgrsFromLatLon(lat, lon);
    }

    public boolean isMine() {
        return myId == getUser().id.get();
    }
}
