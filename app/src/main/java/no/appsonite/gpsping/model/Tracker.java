package no.appsonite.gpsping.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.LoginAnswer;
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

    public android.databinding.ObservableBoolean ledActive = new android.databinding.ObservableBoolean(false);
    public android.databinding.ObservableBoolean shockAlarmActive = new android.databinding.ObservableBoolean(false);
    public android.databinding.ObservableBoolean shockFlashActive = new android.databinding.ObservableBoolean(false);

    public android.databinding.ObservableBoolean isRunning = new android.databinding.ObservableBoolean(false);
    public android.databinding.ObservableBoolean isGeofenceRunning = new android.databinding.ObservableBoolean(false);

    public enum Type {
        TK_STAR, TK_STAR_PET, TK_ANYWHERE, TK_BIKE
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

    public ArrayList<SMS> getResetSms(String address) {
        ArrayList<SMS> smses = new ArrayList<>();
        String trackerNumber = this.trackerNumber.get();
        LoginAnswer loginAnswer = AuthHelper.getCredentials();
        switch (Tracker.Type.valueOf(this.type.get())) {
            case TK_ANYWHERE:
            case TK_STAR:
            case TK_STAR_PET:
                smses.add(new SMS(trackerNumber, String.format("admin123456 00%s%s", loginAnswer.getUser().phoneCode.get(), loginAnswer.getUser().phoneNumber.get())));
                smses.add(new SMS(trackerNumber, "apn123456 internet.ts.m2m"));
                smses.add(new SMS(trackerNumber, String.format("adminip123456 %s 5013", address)));
                smses.add(new SMS(trackerNumber, "gprs123456"));
                smses.add(new SMS(trackerNumber, "sleep123456 off"));
                break;
            case TK_BIKE:
                smses.add(new SMS(trackerNumber, String.format("admin123456 00%s%s", loginAnswer.getUser().phoneCode.get(), loginAnswer.getUser().phoneNumber.get())));
                smses.add(new SMS(trackerNumber, "apn123456 internet.ts.m2m"));
                smses.add(new SMS(trackerNumber, String.format("adminip123456 %s 5093", address)));
                smses.add(new SMS(trackerNumber, "gprs123456"));
                smses.add(new SMS(trackerNumber, "sleep123456 off"));
                break;
        }
        return smses;
    }
}
