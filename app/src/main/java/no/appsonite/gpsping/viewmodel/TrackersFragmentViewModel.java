package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableArrayList;

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
    }
}
