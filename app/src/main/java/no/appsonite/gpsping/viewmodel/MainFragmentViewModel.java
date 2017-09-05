package no.appsonite.gpsping.viewmodel;

import android.app.Activity;
import android.databinding.ObservableField;

import java.util.ArrayList;

import io.realm.Realm;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.TrackersAnswer;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.SMS;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.Utils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class MainFragmentViewModel extends BaseFragmentSMSViewModel {
    public ObservableField<Tracker> activeTracker = new ObservableField<>();

    @Override
    public void onModelAttached() {
        super.onModelAttached();
        requestActiveTracker();
    }

    private void requestActiveTracker() {
        Realm realm = Realm.getDefaultInstance();
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("isEnabled", true).findFirst();
        if (realmTracker != null) {
            activeTracker.set(new Tracker(realmTracker));
        }
        realm.close();
    }

    private Observable<SMS> startTracker(Activity activity) {
        Tracker tracker = activeTracker.get();
        setTrackerRunning(tracker, true);
        String message;
        switch (Tracker.Type.valueOf(tracker.type.get())) {
            case TK_BIKE:
                message = String.format("Upload123456 %s", tracker.getRepeatTime());
                break;
            case TK_STAR_PET:
            case TK_STAR:
                message = "gprs123456";
                break;
            default:
                message = String.format("T%ss***n123456", tracker.getRepeatTime());
                break;
        }

//        LocationTrackerService.startService(activity);

        ArrayList<SMS> smses = new ArrayList<>();
        smses.add(new SMS(tracker.trackerNumber.get(), message));
        return sendSmses(activity, smses).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .last()
                .cache();
    }

    public Observable<TrackersAnswer> hasTrackers() {
        return execute(ApiFactory.getService().getTrackers())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void resetTrackers(Activity activity, ArrayList<Tracker> trackers) {
        ArrayList<SMS> smses = new ArrayList<>();
        for (Tracker tracker : trackers) {
            smses.addAll(tracker.getResetSms(AddTrackerFragmentViewModel.TRACCAR_IP));
        }
        sendSmses(activity, smses);
        Utils.setUpdateTracker();
    }

    private Observable<SMS> stopTracker(Activity activity) {
        Tracker tracker = activeTracker.get();
        setTrackerRunning(tracker, false);
        String message;
        switch (Tracker.Type.valueOf(tracker.type.get())) {
            case TK_STAR_PET:
            case TK_STAR:
                message = "nogprs123456";
                break;
            default:
                message = "Notn123456";
                break;
        }

        ArrayList<SMS> smses = new ArrayList<>();
        smses.add(new SMS(tracker.trackerNumber.get(), message));

        if (!RealmTracker.hasRunning()) {
//            LocationTrackerService.stopService(activity);
        }

        return sendSmses(activity, smses).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .last()
                .cache();
    }

    public Observable<SMS> switchState(Activity activity) {
        if (activeTracker.get().isRunning.get()) {
            return stopTracker(activity);
        } else {
            return startTracker(activity);
        }
    }

    private void setTrackerRunning(Tracker tracker, boolean isRunning) {
        activeTracker.get().isRunning.set(isRunning);
        Realm realm = Realm.getDefaultInstance();
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.imeiNumber.get()).findFirst();
        realm.beginTransaction();
        realmTracker.setIsRunning(isRunning);
        realm.commitTransaction();
    }
}
