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
    public ObservableBoolean checkForStand = new ObservableBoolean();
    @SerializedName("reciver_signal_repeat_time")
    public ObservableString signalRepeatTime = new ObservableString();
    public ObservableString signalRepeatTimeMeasurement = new ObservableString();
    public ObservableBoolean isEnabled = new ObservableBoolean(true);
    @SerializedName("type")
    public ObservableString type = new ObservableString(Type.TK_STAR.toString());

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
}
