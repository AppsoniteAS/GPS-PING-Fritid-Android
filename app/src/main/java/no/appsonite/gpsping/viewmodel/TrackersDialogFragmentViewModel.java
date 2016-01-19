package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableArrayList;

import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.Tracker;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public class TrackersDialogFragmentViewModel extends BaseFragmentViewModel {
    public ObservableArrayList<Tracker> trackers = new ObservableArrayList<>();

    public void requestTrackers() {
        RealmTracker.requestTrackersFromRealm(trackers);
    }

}
