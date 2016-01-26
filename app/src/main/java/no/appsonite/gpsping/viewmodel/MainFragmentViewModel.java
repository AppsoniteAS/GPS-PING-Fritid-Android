package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;

import io.realm.Realm;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.Tracker;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class MainFragmentViewModel extends BaseFragmentViewModel {
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
}
