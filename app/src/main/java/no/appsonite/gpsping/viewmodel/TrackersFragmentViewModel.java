package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableArrayList;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import io.realm.Realm;
import io.realm.RealmResults;
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
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("uuid", tracker.uuid.get()).findFirst();
        realm.beginTransaction();
        realmTracker.removeFromRealm();
        realm.commitTransaction();
    }

    public void requestTrackers() {
        Task.callInBackground(new Callable<ArrayList<Tracker>>() {
            @Override
            public ArrayList<Tracker> call() throws Exception {
                ArrayList<Tracker> trackers = new ArrayList<>();
                Realm realm = Realm.getInstance(Application.getContext());
                RealmResults<RealmTracker> results = realm.where(RealmTracker.class).findAll();
                for (RealmTracker result : results) {
                    trackers.add(new Tracker(result));
                }
                return trackers;
            }
        }).continueWith(new Continuation<ArrayList<Tracker>, Object>() {
            @Override
            public Object then(Task<ArrayList<Tracker>> task) throws Exception {
                trackers.clear();
                trackers.addAll(task.getResult());
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }
}
