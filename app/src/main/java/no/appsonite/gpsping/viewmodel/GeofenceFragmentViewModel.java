package no.appsonite.gpsping.viewmodel;

import android.app.Activity;
import android.databinding.ObservableField;
import android.text.TextUtils;

import java.util.ArrayList;

import io.realm.Realm;
import no.appsonite.gpsping.Application;
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
    public ObservableField<Tracker> editableTracker = new ObservableField<>();


    @Override
    public void onModelAttached() {
        super.onModelAttached();
        Realm realm = Realm.getDefaultInstance();

        Geofence geofence = realm.where(Geofence.class).findFirst();
        if (geofence != null) {
            yards.set(geofence.getYards());
        }
        realm.close();
    }


    public Observable<SMS> switchGeofence(Activity activity) {
        if (!validateData())
            return null;
        saveGeofence();
        if (editableTracker.get().isGeofenceRunning.get()) {
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
        geofence.setYards(yards.get());
        realm.commitTransaction();
        realm.close();
    }

    private Observable<SMS> startGeofence(Activity activity) {
        Tracker tracker = editableTracker.get();
        setTrackerGeofenceRunning(tracker, true);
        ArrayList<SMS> smses = new ArrayList<>();

        switch (Tracker.Type.valueOf(tracker.type.get())) {
            case VT600:
                String yardValue = yards.get();
                String[] yardsArray = Application.getContext().getResources().getStringArray(R.array.geofenceValues);
                int index = 0;
                for (; index < yardsArray.length; index++) {
                    if (yardsArray[index].equals(yardValue)) {
                        break;
                    }
                }
                String[] yardsKey = Application.getContext().getResources().getStringArray(R.array.geofenceKeys);
                String key = yardsKey[index];
                smses.add(new SMS(tracker.trackerNumber.get(), String.format("W000000,006,%s", key)));
                break;
            default:
                smses.add(new SMS(tracker.trackerNumber.get(), String.format("move123456 %s", yards.get())));
                break;
        }
        //smses.add(new SMS(tracker.trackerNumber.get(), String.format("admin123456 %s", activeTracker.get().trackerNumber.get())));

        return sendSmses(activity, smses).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .last()
                .cache();
    }

    private void setTrackerGeofenceRunning(Tracker tracker, boolean isRunning) {
        editableTracker.get().isGeofenceRunning.set(isRunning);
        Realm realm = Realm.getDefaultInstance();
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.imeiNumber.get()).findFirst();
        realm.beginTransaction();
        realmTracker.setIsGeofenceRunning(isRunning);
        realm.commitTransaction();
    }

    private Observable<SMS> stopGeofence(Activity activity) {
        Tracker tracker = editableTracker.get();
        setTrackerGeofenceRunning(tracker, false);
        ArrayList<SMS> smses = new ArrayList<>();

        switch (Tracker.Type.valueOf(tracker.type.get())) {
            case VT600:
                smses.add(new SMS(tracker.trackerNumber.get(), String.format("W000000,006,0", 0)));
                break;
            default:
                smses.add(new SMS(tracker.trackerNumber.get(), "move123456 0"));
                break;
        }
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
        return true;
    }
}
