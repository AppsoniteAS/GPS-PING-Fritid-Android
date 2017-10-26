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
            case TK_STAR_BIKE:
                message = String.format("Upload123456 %s", tracker.getRepeatTime());
                break;
            case TK_STAR_PET:
            case TK_STAR:
                message = "gprs123456";
                break;
            case LK209:
            case LK330: {
                long timeHours = tracker.getRepeatTime() / 60 / 60;
                String formattedTime = timeHours + "";
                while (formattedTime.length() < 2) {
                    formattedTime = "0" + formattedTime;
                }
                message = String.format("DW005,%s", formattedTime);
                break;
            }
            case VT600: {
                String formattedTime = tracker.getRepeatTime() / 10 + "";
                while (formattedTime.length() < 5) {
                    formattedTime = "0" + formattedTime;
                }
                message = String.format("W00000,014,%s", formattedTime);
                break;
            }
            case S1:
            case A9:
                String repeatTime = tracker.getRepeatTime() + "";
                if (repeatTime.length() == 2) {
                    repeatTime = "0" + repeatTime;
                }
                message = String.format("pw,123456,uploadUUIDToServer,%s#", repeatTime);
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
            SMS sms = tracker.getResetSmsIp(EditTrackerFragmentViewModel.TRACCAR_IP);
            if (sms != null)
                smses.add(sms);
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
            case VT600:
                message = "W000000,013,0";
                break;
            case LK209:
            case LK330:
                message = "gpsloc123456,1";
                break;
            case S1:
            case A9:
                message = "pw,123456,uploadUUIDToServer,000#";
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
