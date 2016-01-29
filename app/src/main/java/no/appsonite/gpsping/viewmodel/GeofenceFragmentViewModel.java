package no.appsonite.gpsping.viewmodel;

import android.app.Activity;
import android.databinding.ObservableField;
import android.text.TextUtils;

import java.util.ArrayList;

import io.realm.Realm;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.db.Geofence;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.SMS;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.ObservableString;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class GeofenceFragmentViewModel extends BaseFragmentSMSViewModel {
    public ObservableString yards = new ObservableString();
    public ObservableField<String> yardsError = new ObservableField<>();
    public ObservableField<Tracker> activeTracker = new ObservableField<>();

    public ObservableString phoneNumber = new ObservableString();
    public ObservableField<String> phoneNumberError = new ObservableField<>();

    @Override
    public void onModelAttached() {
        super.onModelAttached();
        Realm realm = Realm.getDefaultInstance();
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("isEnabled", true).findFirst();
        if (realmTracker != null) {
            activeTracker.set(new Tracker(realmTracker));
        }

        Geofence geofence = realm.where(Geofence.class).findFirst();
        if (geofence != null) {
            yards.set(geofence.getYards());
            phoneNumber.set(geofence.getPhone());
        }
        realm.close();
    }


    public Observable<SMS> switchGeofence(Activity activity) {
        if (!validateData())
            return null;
        saveGeofence();
        if (activeTracker.get().isGeofenceRunning.get()) {
            return stopGeofence(activity);
        } else {
            return startGeofence(activity);
        }
    }

    private void saveGeofence() {
        Realm realm = Realm.getDefaultInstance();
        Geofence geofence = realm.where(Geofence.class).findFirst();
        realm.beginTransaction();
        if (geofence == null) {
            geofence = realm.createObject(Geofence.class);
        }
        geofence.setPhone(phoneNumber.get());
        geofence.setYards(yards.get());
        realm.commitTransaction();
        realm.close();
    }

    private Observable<SMS> startGeofence(Activity activity) {
        Tracker tracker = activeTracker.get();
        setTrackerGeofenceRunning(tracker, true);
        ArrayList<SMS> smses = new ArrayList<>();
        smses.add(new SMS(tracker.trackerNumber.get(), String.format("admin123456 %s", phoneNumber.get())));
        smses.add(new SMS(tracker.trackerNumber.get(), String.format("move123456 %s", yards.get())));

        return sendSmses(activity, smses).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .last()
                .cache();
    }

    private void setTrackerGeofenceRunning(Tracker tracker, boolean isRunning) {
        activeTracker.get().isGeofenceRunning.set(isRunning);
        Realm realm = Realm.getDefaultInstance();
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.imeiNumber.get()).findFirst();
        realm.beginTransaction();
        realmTracker.setIsGeofenceRunning(isRunning);
        realm.commitTransaction();
    }

    private Observable<SMS> stopGeofence(Activity activity) {
        Tracker tracker = activeTracker.get();
        setTrackerGeofenceRunning(tracker, false);
        ArrayList<SMS> smses = new ArrayList<>();
        smses.add(new SMS(tracker.trackerNumber.get(), "move123456 0"));
        return sendSmses(activity, smses).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .last()
                .cache();
    }

    public boolean validateData() {
        if (TextUtils.isEmpty(yards.get())) {
            yardsError.set(getContext().getString(R.string.yardsCanNotBeEmpty));
            return false;
        }
        yardsError.set(null);
        if (TextUtils.isEmpty(phoneNumber.get())) {
            phoneNumberError.set(getContext().getString(R.string.phoneNumberCanNotBeEmpty));
            return false;
        }
        phoneNumberError.set(null);
        return true;
    }
}
