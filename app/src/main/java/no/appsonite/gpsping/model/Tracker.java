package no.appsonite.gpsping.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.utils.ObservableBoolean;
import no.appsonite.gpsping.utils.ObservableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class Tracker implements Serializable {
    @SerializedName("name")
    public ObservableString trackerName = new ObservableString();
    @SerializedName("tracker_number")
    public ObservableString trackerNumber = new ObservableString();
    @SerializedName("imei_number")
    public ObservableString imeiNumber = new ObservableString();
    @SerializedName("check_for_stand")
    public ObservableBoolean checkForStand = new ObservableBoolean(false);
    @SerializedName("reciver_signal_repeat_time")
    public ObservableString signalRepeatTime = new ObservableString("60");

    public android.databinding.ObservableBoolean isEnabled = new android.databinding.ObservableBoolean(false);
    @SerializedName("type")
    public ObservableString type = new ObservableString(Type.TK_STAR.toString());

    public android.databinding.ObservableBoolean isRunning = new android.databinding.ObservableBoolean(false);
    public android.databinding.ObservableBoolean isGeofenceRunning = new android.databinding.ObservableBoolean(false);

    public enum Type {
        TK_STAR, TK_STAR_PET, TK_ANYWHERE
    }

    public Tracker() {

    }

    public Tracker(RealmTracker tracker) {
        trackerName.set(tracker.getTrackerName());
        trackerNumber.set(tracker.getTrackerNumber());
        imeiNumber.set(tracker.getImeiNumber());
        checkForStand.set(tracker.isCheckForStand());
        signalRepeatTime.set(tracker.getSignalRepeatTime());
        isEnabled.set(tracker.isEnabled());
        type.set(tracker.getType());
        isRunning.set(tracker.isRunning());
        isGeofenceRunning.set(tracker.isGeofenceRunning());
    }

    public long getRepeatTime() {
        return Long.parseLong(signalRepeatTime.get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tracker tracker = (Tracker) o;

        if (trackerNumber != null ? !trackerNumber.get().equals(tracker.trackerNumber.get()) : tracker.trackerNumber.get() != null)
            return false;
        return !(imeiNumber != null ? !imeiNumber.get().equals(tracker.imeiNumber.get()) : tracker.imeiNumber.get() != null);

    }

    @Override
    public int hashCode() {
        int result = trackerNumber != null ? trackerNumber.get().hashCode() : 0;
        result = 31 * result + (imeiNumber != null ? imeiNumber.get().hashCode() : 0);
        return result;
    }
}
