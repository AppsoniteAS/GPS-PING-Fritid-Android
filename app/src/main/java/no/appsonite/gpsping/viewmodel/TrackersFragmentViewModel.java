package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableArrayList;

import java.util.ArrayList;

import io.realm.Realm;
import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
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

    public Observable<ApiAnswer> removeTracker(final Tracker tracker) {
        Observable<ApiAnswer> observable = ApiFactory.getService().removeTracker(tracker.imeiNumber.get())
                .flatMap(new Func1<ApiAnswer, Observable<ApiAnswer>>() {
                    @Override
                    public Observable<ApiAnswer> call(ApiAnswer apiAnswer) {
                        Realm realm = Realm.getInstance(Application.getContext());
                        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.imeiNumber.get()).findFirst();
                        realm.beginTransaction();
                        realmTracker.removeFromRealm();
                        realm.commitTransaction();
                        realm.close();
                        return Observable.just(apiAnswer);
                    }
                })
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<ApiAnswer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();

            }

            @Override
            public void onNext(ApiAnswer apiAnswer) {
                trackers.remove(tracker);
            }
        });

        return observable;
    }

    public void requestTrackers() {
        ApiFactory.getService().getTrackers().flatMap(new Func1<TrackersAnswer, Observable<ArrayList<Tracker>>>() {
            @Override
            public Observable<ArrayList<Tracker>> call(TrackersAnswer trackersAnswer) {
                ArrayList<Tracker> trackers = trackersAnswer.getTrackers();
                RealmTracker.sync(trackers);
                return Observable.just(trackers);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Tracker>>() {
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
