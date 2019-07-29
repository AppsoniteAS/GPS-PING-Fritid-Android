package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableArrayList;
import android.util.Log;

import java.util.ArrayList;

import io.realm.Realm;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.Tracker;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class TrackersFragmentViewModel extends BaseFragmentViewModel {
    public ObservableArrayList<Tracker> trackers = new ObservableArrayList<>();

    public Observable<ApiAnswer> removeTracker(final Tracker tracker) {
        Observable<ApiAnswer> observable = execute(ApiFactory.getService().removeTracker(tracker.imeiNumber.get()))
                .flatMap(apiAnswer -> {
                    Log.e("FIND_REALM", "Remove tracker method called");
                    Realm realm = Realm.getDefaultInstance();
                    RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.imeiNumber.get()).findFirst();
                    realm.beginTransaction();
                    realmTracker.removeFromRealm();
                    realm.commitTransaction();
                    realm.close();
                    return Observable.just(apiAnswer);
                })
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(apiAnswer -> trackers.remove(tracker),
                Throwable::printStackTrace);

        return observable;
    }

    public void requestTrackers() {
        RealmTracker.requestTrackersFromRealm(trackers);
        execute(ApiFactory.getService().getTrackers()).flatMap(trackersAnswer -> {
            ArrayList<Tracker> trackers = trackersAnswer.getTrackers();
            RealmTracker.sync(trackers);
            return Observable.just(trackers);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trackers1 -> RealmTracker.requestTrackersFromRealm(TrackersFragmentViewModel.this.trackers),
                        this::requestTrackersThrowable);
    }

    private void requestTrackersThrowable(Throwable throwable) {
        RealmTracker.requestTrackersFromRealm(trackers);
        throwable.printStackTrace();
    }

//    public void enableTracker(Tracker enabledTracker) {
//        if (enabledTracker.isEnabled.get()) {
//            return;
//        }
//        for (Tracker tracker : trackers) {
//            tracker.isEnabled.set(tracker.equals(enabledTracker));
//        }
//        Observable.defer(new Func0<Observable<Boolean>>() {
//            @Override
//            public Observable<Boolean> call() {
//                Realm realm = Realm.getDefaultInstance();
//                realm.beginTransaction();
//                for (Tracker tracker : trackers) {
//                    RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.imeiNumber.get()).findFirst();
//                    realmTracker.setIsEnabled(tracker.isEnabled.get());
//                }
//                realm.commitTransaction();
//                realm.close();
//                return Observable.just(true);
//            }
//        }).subscribe();
//    }
}
