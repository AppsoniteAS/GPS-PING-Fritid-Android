package no.appsonite.gpsping.model;

import android.databinding.ObservableLong;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import no.appsonite.gpsping.utils.ObservableBoolean;
import no.appsonite.gpsping.utils.ObservableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public class Friend {
    @SerializedName("first_name")
    public ObservableString firstName = new ObservableString();
    @SerializedName("last_name")
    public ObservableString lastName = new ObservableString();
    public ObservableString username = new ObservableString();
    public ObservableLong id = new ObservableLong(-1);
    public ObservableBoolean confirmed = new ObservableBoolean();
    @SerializedName("is_seeing_trackers")
    public ObservableBoolean isSeeingTrackers = new ObservableBoolean();
    public double lat = 0d;
    public double lon = 0d;
    @SerializedName("timestamp")
    public long lastUpdate;


    public String getName() {
        String name;
        if (TextUtils.isEmpty(firstName.get()))
            return username.get();
        name = firstName.get();
        if (!TextUtils.isEmpty(lastName.get())) {
            name = name + " " + lastName.get();
        }
        return name;
    }
}
