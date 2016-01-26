package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableArrayList;

import java.util.ArrayList;

import io.realm.Realm;
import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.TrackersAnswer;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.Tracker;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.imeiNumber.get()).findFirst();
        realm.beginTransaction();
        realmTracker.removeFromRealm();
        realm.commitTransaction();
    }

    public void requestTrackers() {
        ApiFactory.getService().getTrackers().flatMap(new Func1<TrackersAnswer, Observable<ArrayList<Tracker>>>() {
            @Override
            public Observable<ArrayList<Tracker>> call(TrackersAnswer trackersAnswer) {
                ArrayList<Tracker> trackers = trackersAnswer.getTrackers();
                for (Tracker tracker : trackers) {
                    RealmTracker.add(tracker);
                }
                return Observable.just(trackers);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArrayList<Tracker>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                RealmTracker.requestTrackersFromRealm(trackers);
                e.printStackTrace();
            }

            @Override
            public void onNext(ArrayList<Tracker> trackers) {
                RealmTracker.requestTrackersFromRealm(TrackersFragmentViewModel.this.trackers);
            }
        });
    }
}
