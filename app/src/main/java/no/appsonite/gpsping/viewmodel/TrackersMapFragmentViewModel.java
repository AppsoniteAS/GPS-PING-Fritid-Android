package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.location.Location;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.FriendsAnswer;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.api.content.Poi;
import no.appsonite.gpsping.api.content.Profile;
import no.appsonite.gpsping.api.content.geo.GeoPointsAnswer;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.model.MapPoint;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.ObservableString;
import no.appsonite.gpsping.utils.TrackingHistoryTime;
import no.appsonite.gpsping.utils.point.CreatePointManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.schedulers.TimeInterval;
import rx.subjects.PublishSubject;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class TrackersMapFragmentViewModel extends BaseFragmentViewModel {
    private static final long INTERVAL = 10;
    public ObservableString distance = new ObservableString("");
    public ObservableField<Friend> currentFriend = new ObservableField<>();
    public ObservableArrayList<Friend> friendList = new ObservableArrayList<>();
    public ObservableArrayList<MapPoint> mapPoints = new ObservableArrayList<>();
    public ObservableField<MapPoint> currentMapPoint = new ObservableField<>();
    public ObservableField<Poi> currentPoi = new ObservableField<>();
    public ObservableArrayList<Poi> pois = new ObservableArrayList<>();
    public ObservableBoolean visibilityCalendar = new ObservableBoolean(false);
    public ObservableBoolean visibilityUserPosition = new ObservableBoolean(true);
    public ObservableBoolean clickableEditBtn = new ObservableBoolean(false);
    public ObservableBoolean visibilityCallBtn = new ObservableBoolean(false);
    public ObservableBoolean visibilityBattery = new ObservableBoolean(false);
    public CreatePointManager createPointManager;
    public ObservableString distanceBetweenUserAndMapPoint = new ObservableString();
    private DecimalFormat decimalFormat = new DecimalFormat("#.#");
    private PublishSubject<Object> cancelRequest = PublishSubject.create();

    public void requestFriends() {
        execute(ApiFactory.getService().getFriends())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::requestFriendsOnNext, Throwable::printStackTrace);
    }

    private void requestFriendsOnNext(FriendsAnswer friendsAnswer) {
        friendList.clear();
        Friend all = new Friend();
        all.firstName.set(Application.getContext().getString(R.string.all));
        friendList.add(all);
        Friend you = new Friend();
        LoginAnswer loginAnswer = AuthHelper.getCredentials();
        Profile profile = loginAnswer.getUser();
        you.id.set(profile.id.get());
        you.firstName.set(Application.getContext().getString(R.string.you));
        friendList.add(you);
        for (Friend friend : friendsAnswer.getFriends()) {
            if (friend.username != null) {
                friendList.add(friend);
            }
        }
        requestTrackers();
    }

    private void requestTrackers() {
        execute(ApiFactory.getService().getTrackers()).flatMap(trackersAnswer -> {
            ArrayList<Tracker> trackers = trackersAnswer.getTrackers();
            RealmTracker.sync(trackers);
            return Observable.just(trackers);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        requestPoints();
        currentFriend.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                restartRequest();
            }
        });


    }

    private void restartRequest() {
        cancelRequest.onNext(new Object());
        requestPoints();
        requestPois();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelRequest.onNext(new Object());
    }

    protected long getFrom() {
        return getTo() - TrackingHistoryTime.getTrackingHistorySeconds();
//        return ((new Date().getTime() / 1000l) - 1 * 24 * 60 * 60);
    }

    protected long getTo() {
        return new Date().getTime() / 1000l;
    }

    protected Observable<GeoPointsAnswer> requestPoints(final boolean repeat) {
        Observable<TimeInterval<Long>> intervalObservable;
        if (repeat) {
            intervalObservable = Observable.interval(0, INTERVAL, TimeUnit.SECONDS)
                    .takeUntil(cancelRequest)
                    .timeInterval();
        } else {
            intervalObservable = Observable.just(new TimeInterval<>(0l, 0l));
        }

        Observable<GeoPointsAnswer> observable = intervalObservable
                .flatMap(aLong -> {
                    if (currentFriend.get() == null || currentFriend.get().id.get() == -1) {
                        Log.e("TIME", "from:" + getFrom() + " to:" + getTo());
                        return execute(ApiFactory.getService().getGeoPoints(getFrom(), getTo()))
                                .cache()
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
                    } else {
                        Log.e("TIME", "from:" + getFrom() + " to:" + getTo());
                        return execute(ApiFactory.getService().getGeoPoints(getFrom(), getTo(), currentFriend.get().id.get()))
                                .cache()
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
                    }

                })
                .cache()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(this::parseGeoPointsAnswer, Throwable::printStackTrace);
        return observable;
    }

    protected void parseGeoPointsAnswer(GeoPointsAnswer geoPointsAnswer) {
        createPointManager = new CreatePointManager();
        List<MapPoint> mapPoints = createPointManager.getMapPoints(geoPointsAnswer);
        Log.d("DFSDF", "parseGeoPointsAnswer: " + mapPoints.get(0).getPicUrl());

        this.mapPoints.clear();
        this.mapPoints.addAll(mapPoints);
    }

    public boolean validateCallForS1Tracker(String imei) {
        if (imei.isEmpty()) {
            return false;
        }
        Realm realm = Realm.getDefaultInstance();
        RealmTracker tracker = realm.where(RealmTracker.class).equalTo("imeiNumber", imei).findFirst();
        if (tracker == null) {
            return false;
        }
        boolean check = tracker.getType().equals(Tracker.Type.S1.toString()) || tracker.getType().equals(Tracker.Type.A9.toString()) || tracker.getType().equals(Tracker.Type.D79.toString());
        realm.close();
        return check;
    }

    public Observable<GeoPointsAnswer> requestPoints() {
        return requestPoints(true);
    }

    public Observable<ArrayList<Tracker>> requestTrackers(List<Tracker> trackers) {
        RealmTracker.requestTrackersFromRealm(trackers);
        return execute(ApiFactory.getService().getTrackers()).flatMap(trackersAnswer -> {
            ArrayList<Tracker> trackersLocal = trackersAnswer.getTrackers();
            RealmTracker.sync(trackersLocal);
            return Observable.just(trackersLocal);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Friend getFriendById(long id) {
        for (Friend friend : friendList) {
            if (friend.id.get() == id) {
                return friend;
            }
        }
        return null;
    }

    public void requestPois() {
        long currentFriendId = 0;
        if (currentFriend.get() != null && currentFriend.get().id.get() != -1) {
            currentFriendId = currentFriend.get().id.get();
        }
        execute(ApiFactory.getService().getPois(currentFriendId)).cache()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).flatMap(poiAnswer -> {
            ArrayList<Poi> result = new ArrayList<>();
            for (Poi poi : poiAnswer.getPoi()) {
                Friend friend = getFriendById(poi.getUserId());
                if (friend != null) {
                    poi.setUser(friend);
                    result.add(poi);
                }
            }
            return Observable.just(result);
        }).subscribe(pois1 -> {
            TrackersMapFragmentViewModel.this.pois.clear();
            TrackersMapFragmentViewModel.this.pois.addAll(pois1);
        }, Throwable::printStackTrace);
    }

    public void setClickableEditBtn() {
        if (currentMapPoint.get().getImeiNumber().isEmpty()) {
            clickableEditBtn.set(false);
        } else {
            clickableEditBtn.set(true);
        }
    }

    public void setVisibilityCallBtn() {
        if (currentMapPoint.get().getImeiNumber().isEmpty()) {
            visibilityCallBtn.set(false);
        } else {
            String imei = currentMapPoint.get().getImeiNumber();
            Realm realm = Realm.getDefaultInstance();
            RealmTracker tracker = realm.where(RealmTracker.class).equalTo("imeiNumber", imei).findFirst();
            if (tracker == null) {
                visibilityCallBtn.set(false);
            } else {
                boolean check = tracker.getType().equals(Tracker.Type.S1.toString())
                        || tracker.getType().equals(Tracker.Type.A9.toString())
                        || tracker.getType().equals(Tracker.Type.D79.toString());
                if (check) {
                    visibilityCallBtn.set(true);
                } else {
                    visibilityCallBtn.set(false);
                }
            }
        }
    }

    public void setVisibilityBattery() {
        if (currentMapPoint.get().getImeiNumber().isEmpty()) {
            visibilityBattery.set(true);
        } else {
            String imei = currentMapPoint.get().getImeiNumber();
            Realm realm = Realm.getDefaultInstance();
            RealmTracker tracker = realm.where(RealmTracker.class).equalTo("imeiNumber", imei).findFirst();
            if (tracker == null) {
                visibilityBattery.set(true);
            } else {
                boolean check = tracker.getType().equals(Tracker.Type.TK_STAR_PET.toString());
                if (check) {
                    visibilityBattery.set(false);
                } else {
                    visibilityBattery.set(true);
                }
            }
        }
    }

    public void getDistanceBetweenUserAndMapPointMain(double latUser, double lonUser) {
        double latPoint = currentMapPoint.get().getLat();
        double lonPoint = currentMapPoint.get().getLon();
        float[] results = new float[1];
        Location.distanceBetween(latUser, lonUser, latPoint, lonPoint, results);
        if (results[0] > 1000) {
            String distance = "" + decimalFormat.format(results[0] / 1000) + " km";
            distanceBetweenUserAndMapPoint.set(distance);
        } else {
            String distance = "" + decimalFormat.format(results[0]) + " m";
            distanceBetweenUserAndMapPoint.set(distance);
        }
    }
}
