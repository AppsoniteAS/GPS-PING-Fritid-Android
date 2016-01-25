package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableArrayList;

import io.realm.Realm;
import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.Tracker;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class TrackersFragmentViewModel extends BaseFragmentViewModel {
    public ObservableArrayList<Tracker> trackers = new ObservableArrayList<>();

    public void removeTracker(Tracker tracker) {
        trackers.remove(tracker);
        Realm realm = Realm.getInstance(Application.getContext());
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.imeiNumber.get()).findFirst();
        realm.beginTransaction();
        realmTracker.removeFromRealm();
        realm.commitTransaction();
    }

    public void requestTrackers() {
        RealmTracker.requestTrackersFromRealm(trackers);
    }
}
