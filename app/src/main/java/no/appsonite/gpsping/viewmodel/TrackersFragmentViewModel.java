package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableArrayList;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.Tracker;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
                        TrackersFragmentViewModel.this.trackers.clear();
                        TrackersFragmentViewModel.this.trackers.addAll(trackers);
                    }
                });

    }
}
