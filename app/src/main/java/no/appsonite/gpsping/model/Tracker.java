package no.appsonite.gpsping.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
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
    public ObservableString signalRepeatTime = new ObservableString();
    public ObservableString signalRepeatTimeMeasurement = new ObservableString();
    public android.databinding.ObservableBoolean isEnabled = new android.databinding.ObservableBoolean(false);
    @SerializedName("type")
    public ObservableString type = new ObservableString(Type.TK_STAR.toString());

    public android.databinding.ObservableBoolean isRunning = new android.databinding.ObservableBoolean(false);

    public void fixRepeatTime() {
        long repeatSeconds = Long.parseLong(signalRepeatTime.get());
        String[] timeArray = Application.getContext().getResources().getStringArray(R.array.receiveSignalTime);
        if (repeatSeconds % 60 == 0 && Arrays.asList(timeArray).contains(String.valueOf(repeatSeconds / 60))) {
            signalRepeatTime.set(String.valueOf(repeatSeconds / 60));
            signalRepeatTimeMeasurement.set("minutes");
        } else {
            signalRepeatTimeMeasurement.set("seconds");
        }
    }

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
        signalRepeatTimeMeasurement.set(tracker.getSignalRepeatTimeMeasurement());
        isEnabled.set(tracker.isEnabled());
        type.set(tracker.getType());
        isRunning.set(tracker.isRunning());
    }

    public long getRepeatTime() {
        long multiplier = 1;
        switch (signalRepeatTimeMeasurement.get()) {
            case "seconds":
                break;
            case "minutes":
                multiplier = 60;
                break;
        }
        return Long.parseLong(signalRepeatTime.get()) * multiplier;
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
