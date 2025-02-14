package no.appsonite.gpsping.db;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.ObservableString;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public class RealmTracker extends RealmObject {
    private String trackerName;
    private String trackerNumber;
    private String imeiNumber;
    private boolean checkForStand;
    private String signalRepeatTime;
    private String signalRepeatTimeMeasurement;
    private boolean isEnabled;
    private String type;
    private String picUrl;
    private boolean isRunning;
    private boolean isGeofenceRunning;
    private boolean ledActive;
    private boolean shockAlarmActive;
    private boolean shockFlashActive;
    private boolean sleepMode;

    public RealmTracker() {
    }

    public RealmTracker(Tracker tracker) {
        initWithTracker(this, tracker);
    }

    public static void initWithTracker(RealmTracker realmTracker, Tracker tracker) {
        realmTracker.setTrackerName(tracker.trackerName.get());
        realmTracker.setTrackerNumber(tracker.trackerNumber.get());
        realmTracker.setCheckForStand(tracker.checkForStand.get());
        realmTracker.setImeiNumber(tracker.imeiNumber.get());
        realmTracker.setSignalRepeatTime(tracker.signalRepeatTime.get());
        if (tracker.type == null) {
            tracker.type = new ObservableString(Tracker.Type.TK_ANYWHERE.toString());
        }
        if (tracker.picUrl != null) {
            realmTracker.setPicUrl(tracker.picUrl.get());
        } else {
            realmTracker.setPicUrl(null);
        }
        realmTracker.setType(tracker.type.get());
        realmTracker.setIsEnabled(tracker.isEnabled.get());
        realmTracker.setLedActive(tracker.ledActive.get());
        realmTracker.setShockAlarmActive(tracker.shockAlarmActive.get());
        realmTracker.setShockFlashActive(tracker.shockFlashActive.get());
        realmTracker.setSleepMode(tracker.sleepMode.get());
    }

    public static void requestTrackersFromRealm(final List<Tracker> result) {
        Observable.create((Observable.OnSubscribe<ArrayList<Tracker>>) subscriber -> {
            ArrayList<Tracker> trackers = new ArrayList<>();
            Realm realm = Realm.getDefaultInstance();
            RealmResults<RealmTracker> results = realm.where(RealmTracker.class).findAll();
            for (RealmTracker result1 : results) {
                trackers.add(new Tracker(result1));
            }
            realm.close();
            subscriber.onNext(trackers);
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trackers -> {
                    result.clear();
                    result.addAll(trackers);
                });
    }

    public static void add(Tracker tracker) {
        Log.e("FIND_REALM", "Add tracker method called");
        Realm realm = Realm.getDefaultInstance();
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.imeiNumber.get()).findFirst();
        realm.beginTransaction();
        if (realmTracker == null) {
            realmTracker = realm.createObject(RealmTracker.class);
        } else {
            tracker.isEnabled.set(realmTracker.isEnabled());
            tracker.sleepMode.set(realmTracker.isSleepMode());
        }
        RealmTracker.initWithTracker(realmTracker, tracker);
        realm.copyToRealm(realmTracker);
        realm.commitTransaction();
        realm.close();
    }

    public static void sync(ArrayList<Tracker> trackers) {
        Log.e("FIND_REALM", "Sync tracker method called");
        if (trackers.size() == 1) {
            trackers.get(0).isEnabled.set(true);
        }
        for (Tracker tracker : trackers) {
            RealmTracker.add(tracker);
        }
        Realm realm = Realm.getDefaultInstance();
        ArrayList<RealmTracker> trackersForRemove = new ArrayList<>();
        RealmResults<RealmTracker> realmTrackers = realm.where(RealmTracker.class).findAll();
        for (RealmTracker realmTracker : realmTrackers) {
            Tracker tracker = new Tracker(realmTracker);
            if (!trackers.contains(tracker)) {
                trackersForRemove.add(realmTracker);
            }
        }

        if (!trackersForRemove.isEmpty()) {
            realm.beginTransaction();
            for (RealmTracker realmTracker : trackersForRemove) {
                realmTracker.removeFromRealm();
            }
            realm.commitTransaction();
        }
        realm.close();
    }

    public static boolean hasRunning() {
        Realm realm = Realm.getDefaultInstance();
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("isRunning", true).findFirst();
        boolean isRunning = realmTracker != null;
        realm.close();
        return isRunning;
    }

    public boolean isSleepMode() {
        return sleepMode;
    }

    public void setSleepMode(boolean sleepMode) {
        this.sleepMode = sleepMode;
    }

    public boolean isGeofenceRunning() {
        return isGeofenceRunning;
    }

    public void setIsGeofenceRunning(boolean isGeofenceRunning) {
        this.isGeofenceRunning = isGeofenceRunning;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public String getTrackerName() {
        return trackerName;
    }

    public void setTrackerName(String trackerName) {
        this.trackerName = trackerName;
    }

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

    public boolean isCheckForStand() {
        return checkForStand;
    }

    public void setCheckForStand(boolean checkForStand) {
        this.checkForStand = checkForStand;
    }

    public String getSignalRepeatTime() {
        return signalRepeatTime;
    }

    public void setSignalRepeatTime(String signalRepeatTime) {
        this.signalRepeatTime = signalRepeatTime;
    }

    public String getSignalRepeatTimeMeasurement() {
        return signalRepeatTimeMeasurement;
    }

    public void setSignalRepeatTimeMeasurement(String signalRepeatTimeMeasurement) {
        this.signalRepeatTimeMeasurement = signalRepeatTimeMeasurement;
    }

    public boolean isShockAlarmActive() {
        return shockAlarmActive;
    }

    public void setShockAlarmActive(boolean shockAlarmActive) {
        this.shockAlarmActive = shockAlarmActive;
    }

    public boolean isLedActive() {
        return ledActive;
    }

    public void setLedActive(boolean ledActive) {
        this.ledActive = ledActive;
    }

    public boolean isShockFlashActive() {
        return shockFlashActive;
    }

    public void setShockFlashActive(boolean shockFlashActive) {
        this.shockFlashActive = shockFlashActive;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
