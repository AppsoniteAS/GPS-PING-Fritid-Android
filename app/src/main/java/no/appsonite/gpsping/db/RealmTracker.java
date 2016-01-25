package no.appsonite.gpsping.db;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.model.Tracker;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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

    public RealmTracker() {
    }

    public static void initWithTracker(RealmTracker realmTracker, Tracker tracker) {
        realmTracker.setTrackerName(tracker.trackerName.get());
        realmTracker.setTrackerNumber(tracker.trackerNumber.get());
        realmTracker.setCheckForStand(tracker.checkForStand.get());
        realmTracker.setImeiNumber(tracker.imeiNumber.get());
        realmTracker.setSignalRepeatTime(tracker.signalRepeatTime.get());
        realmTracker.setSignalRepeatTimeMeasurement(tracker.signalRepeatTimeMeasurement.get());
        realmTracker.setType(tracker.type.get().name());
        realmTracker.setIsEnabled(tracker.isEnabled.get());
    }

    public static void requestTrackersFromRealm(final List<Tracker> result){
        Observable.create(new Observable.OnSubscribe<ArrayList<Tracker>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Tracker>> subscriber) {
                ArrayList<Tracker> trackers = new ArrayList<>();
                Realm realm = Realm.getInstance(Application.getContext());
                RealmResults<RealmTracker> results = realm.where(RealmTracker.class).findAll();
                for (RealmTracker result : results) {
                    trackers.add(new Tracker(result));
                }
                subscriber.onNext(trackers);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<Tracker>>() {
                    @Override
                    public void call(ArrayList<Tracker> trackers) {
                        result.clear();
                        result.addAll(trackers);
                    }
                });
    }

    public RealmTracker(Tracker tracker) {
        initWithTracker(this, tracker);
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
}
