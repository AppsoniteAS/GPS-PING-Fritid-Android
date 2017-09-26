package no.appsonite.gpsping.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
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

    public android.databinding.ObservableBoolean sleepMode = new android.databinding.ObservableBoolean(false);

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
        sleepMode.set(tracker.isSleepMode());
    }

    public String[] getEntriesText() {
        Tracker.Type trType = getTypeSafely();
        switch (trType) {
            case TK_BIKE:
            case TK_STAR_BIKE:
                return Application.getContext().getResources().getStringArray(R.array.receiveSignalTime);
            case TK_STAR:
            case TK_STAR_PET:
            case TK_ANYWHERE:
                return Application.getContext().getResources().getStringArray(R.array.receiveSignalTimeDog);
            case LK209:
            case LK330:
                return Application.getContext().getResources().getStringArray(R.array.receiveSignalTimeLK330);
            case S1:
            case A9:
                return Application.getContext().getResources().getStringArray(R.array.receiveSignalTimeS1);
            default:
                return Application.getContext().getResources().getStringArray(R.array.receiveSignalTime);
        }
    }

    public String[] getEntriesValues() {
        Tracker.Type trType = getTypeSafely();
        switch (trType) {
            case TK_BIKE:
            case TK_STAR_BIKE:
                return Application.getContext().getResources().getStringArray(R.array.receiveSignalTimeValues);
            case TK_STAR:
            case TK_STAR_PET:
            case TK_ANYWHERE:
                return Application.getContext().getResources().getStringArray(R.array.receiveSignalTimeDogValues);
            case LK209:
            case LK330:
                return Application.getContext().getResources().getStringArray(R.array.receiveSignalTimeLK330Values);
            case S1:
            case A9:
                return Application.getContext().getResources().getStringArray(R.array.receiveSignalTimeS1Values);
            default:
                return Application.getContext().getResources().getStringArray(R.array.receiveSignalTimeValues);
        }
    }

    private Type getTypeSafely() {
        Tracker.Type trType = Type.TK_STAR;

        try {
            trType = Tracker.Type.valueOf(type.get());
        } catch (Exception e) {

        }
        return trType;
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

    public ArrayList<SMS> getUpdateSms() {
        ArrayList<SMS> smses = new ArrayList<>();
        switch (Tracker.Type.valueOf(this.type.get())) {
            case TK_ANYWHERE:
            case TK_STAR:
            case TK_STAR_PET:
                smses.add(new SMS(trackerNumber.get(), String.format("Upload123456 %s", getRepeatTime())));
                break;
            case TK_BIKE:
            case TK_STAR_BIKE:
                break;
        }
        return smses;
    }

    public ArrayList<SMS> getResetSms(String address) {
        ArrayList<SMS> smses = new ArrayList<>();
        String trackerNumber = this.trackerNumber.get();
        LoginAnswer loginAnswer = AuthHelper.getCredentials();
        try {
            String phoneCode = loginAnswer.getUser().phoneCode.get().replaceAll("[^\\d.]", "");
            phoneCode = "00" + phoneCode;
            String phoneNumber = loginAnswer.getUser().phoneNumber.get();
            switch (Tracker.Type.valueOf(this.type.get())) {
                case TK_ANYWHERE:
                case TK_STAR:
                case TK_STAR_PET:
                    smses.add(new SMS(trackerNumber, String.format("admin123456 %s%s", phoneCode, phoneNumber)));
                    smses.add(new SMS(trackerNumber, "apn123456 internet.ts.m2m"));
                    smses.add(new SMS(trackerNumber, String.format("adminip123456 %s 5013", address)));
                    smses.add(new SMS(trackerNumber, "sleep123456 off"));
                    break;
                case TK_BIKE:
                case TK_STAR_BIKE:
                    smses.add(new SMS(trackerNumber, String.format("admin123456 %s%s", phoneCode, phoneNumber)));
                    smses.add(new SMS(trackerNumber, "apn123456 internet.ts.m2m"));
                    smses.add(new SMS(trackerNumber, String.format("adminip123456 %s 5093", address)));
                    smses.add(new SMS(trackerNumber, "gprs123456"));
                    smses.add(new SMS(trackerNumber, "sleep123456 off"));
                    break;
                case LK209:
                case LK330:
                    smses.add(new SMS(trackerNumber, String.format("admin123456 00%s%s", loginAnswer.getUser().phoneCode.get().replaceAll("[^\\d.]", ""), loginAnswer.getUser().phoneNumber.get())));
                    smses.add(new SMS(trackerNumber, "apn123456 internet.ts.m2m"));
                    smses.add(new SMS(trackerNumber, String.format("adminip123456 %s 5013", address)));
                    smses.add(new SMS(trackerNumber, "gprs123456"));
                    break;
                case VT600:
                    smses.add(new SMS(trackerNumber, "W000000,010,internet.ts.m2m"));
                    smses.add(new SMS(trackerNumber, String.format("W000000,012,%s,5009", address)));
                    smses.add(new SMS(trackerNumber, "W000000,013,1"));
                    break;
                case S1:
                case A9:
                    smses.add(new SMS(trackerNumber, "pw,123456,apn,internet.ts.m2m,,,23820#"));
                    smses.add(new SMS(trackerNumber, String.format("pw,123456,ip,%s,5093#", address)));
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(Application.getContext().getString(R.string.inputPhoneInProfileError));
        }
        return smses;
    }

    public SMS getResetSmsIp(String address) {
        SMS sms = null;
        String trackerNumber = this.trackerNumber.get();
        try {
            switch (Tracker.Type.valueOf(this.type.get())) {
                case TK_ANYWHERE:
                case TK_STAR:
                case TK_STAR_PET:
                    sms = new SMS(trackerNumber, String.format("adminip123456 %s 5013", address));
                    break;
                case TK_BIKE:
                case TK_STAR_BIKE:
                    sms = new SMS(trackerNumber, String.format("adminip123456 %s 5093", address));
                    break;
                case LK209:
                case LK330:
                    sms = new SMS(trackerNumber, String.format("adminip123456 %s 5013", address));
                    break;
                case VT600:
                    sms = new SMS(trackerNumber, String.format("W000000,012,%s,5009", address));
                    break;
                case S1:
                    sms = new SMS(trackerNumber, String.format("pw,123456,ip,%s,5093#", address));
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(Application.getContext().getString(R.string.inputPhoneInProfileError));
        }
        return sms;
    }

    public enum Type {
        TK_STAR, TK_STAR_PET, TK_ANYWHERE, TK_BIKE, TK_STAR_BIKE, LK209, VT600, LK330, S1, A9
    }
}
