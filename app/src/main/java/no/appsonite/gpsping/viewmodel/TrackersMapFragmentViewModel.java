package no.appsonite.gpsping.viewmodel;

import android.app.Activity;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import java.util.ArrayList;
import java.util.Date;
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
import no.appsonite.gpsping.api.content.TrackersAnswer;
import no.appsonite.gpsping.api.content.geo.GeoAttributes;
import no.appsonite.gpsping.api.content.geo.GeoDevice;
import no.appsonite.gpsping.api.content.geo.GeoDevicePoints;
import no.appsonite.gpsping.api.content.geo.GeoItem;
import no.appsonite.gpsping.api.content.geo.GeoPoint;
import no.appsonite.gpsping.api.content.geo.GeoPointsAnswer;
import no.appsonite.gpsping.data_structures.ColorArrowPin;
import no.appsonite.gpsping.data_structures.LatLonData;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.model.MapPoint;
import no.appsonite.gpsping.model.SMS;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.ObservableString;
import no.appsonite.gpsping.utils.TrackingHistoryTime;
import no.appsonite.gpsping.utils.Utils;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.schedulers.TimeInterval;
import rx.subjects.PublishSubject;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class TrackersMapFragmentViewModel extends BaseFragmentSMSViewModel {
    private static final long INTERVAL = 10;
    public ObservableString distance = new ObservableString("");
    public ObservableField<Friend> currentFriend = new ObservableField<>();
    public ObservableArrayList<Friend> friendList = new ObservableArrayList<>();
    public ObservableArrayList<MapPoint> mapPoints = new ObservableArrayList<>();
    public ObservableField<MapPoint> currentMapPoint = new ObservableField<>();
    public ObservableField<Poi> currentPoi = new ObservableField<>();
    public ObservableArrayList<Poi> pois = new ObservableArrayList<>();
    public ColorArrowPin colorArrowPin = new ColorArrowPin();
    public ObservableBoolean visibilityCalendar = new ObservableBoolean(false);
    public ObservableBoolean visibilityUserPosition = new ObservableBoolean(true);
    private PublishSubject<Object> cancelRequest = PublishSubject.create();
    private LatLonData latLonData = new LatLonData();
    public ObservableBoolean clickableEditBtn = new ObservableBoolean(false);

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
        return Math.max(new Date().getTime() / 1000l, getTo() - TrackingHistoryTime.getTrackingHistorySeconds());
//        return ((new Date().getTime() / 1000l) - 10 * 365 * 24 * 60 * 60);
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
                        return execute(ApiFactory.getService().getGeoPoints(getFrom(), getTo()))
                                .cache()
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
                    } else {
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
        latLonData.clear();
        ArrayList<MapPoint> mapPoints = new ArrayList<>();
        for (GeoItem geoItem : geoPointsAnswer.getUsers()) {
            for (GeoDevicePoints geoDevicePoints : geoItem.getDevices()) {
                mapPoints.add(createPoint(geoDevicePoints, geoItem, true));
                ArrayList<MapPoint> devicePoints = new ArrayList<>();
                for (GeoPoint geoPoint : geoDevicePoints.getPoints()) {
                    if (!latLonData.contains(geoPoint.getLat(), geoPoint.getLon())) {
                        MapPoint mapPoint = new MapPoint(geoItem.getUser(),
                                geoPoint.getLat(),
                                geoPoint.getLon(),
                                geoDevicePoints.getDevice().getName(),
                                geoDevicePoints.getDevice().getImeiNumber(),
                                geoDevicePoints.getDevice().getTrackerNumber(),
                                geoPoint.getTimestamp());
                        devicePoints.add(mapPoint);
                        mapPoints.add(mapPoint);
                    }
                }
                try {
                    if (devicePoints.isEmpty()) {
                        MapPoint mapPoint = createPoint(geoDevicePoints, geoItem, false);
                        devicePoints.add(mapPoint);
                        mapPoints.add(mapPoint);
                    }
                } catch (Exception ignore) {

                }
                if (mapPoints.size() > 0) {
                    mapPoints.get(mapPoints.size() - 1).setLast(true);
                }
                checkForStand(devicePoints);
            }

            MapPoint userMapPoint = new MapPoint(
                    geoItem.getUser(),
                    geoItem.getUser().lat,
                    geoItem.getUser().lon,
                    geoItem.getUser().getName(),
                    null,
                    null,
                    geoItem.getUser().lastUpdate);
            userMapPoint.setBelongsToUser(true);
            mapPoints.add(userMapPoint);
        }
        for (MapPoint mapPoint : mapPoints) {
            colorArrowPin.add(mapPoint.getImeiNumber());
        }
        this.mapPoints.clear();
        this.mapPoints.addAll(mapPoints);
    }

    private MapPoint createPoint(GeoDevicePoints geoDevicePoints, GeoItem geoItem, boolean avatar) {
        GeoDevice geoDevice = geoDevicePoints.getDevice();
        latLonData.add(geoDevice.getLastLat(), geoDevice.getLastLon());

        MapPoint mapPoint = new MapPoint(
                geoItem.getUser(), geoDevice.getLastLat(),
                geoDevice.getLastLon(), geoDevice.getName(),
                geoDevice.getImeiNumber(), geoDevice.getTrackerNumber(),
                geoDevice.getLastTimestamp(), geoDevice.getPicUrl(),
                geoDevice.getDirection(), geoDevice.getSpeed(),
                geoDevice.getGsmSignal(), geoDevice.getGpsSignal(),
                geoDevice.getAttributes());
        mapPoint.setMainAvatar(avatar);
        return mapPoint;
    }

    private void checkForStand(ArrayList<MapPoint> mapPoints) {
        if (mapPoints.size() > 1) {
            final MapPoint prev = mapPoints.get(mapPoints.size() - 2);
            Observable.defer(() -> {
                Realm realm = Realm.getDefaultInstance();
                RealmTracker tracker = realm.where(RealmTracker.class).equalTo("imeiNumber", prev.getImeiNumber()).findFirst();
                Boolean checkForStand = false;
                if (tracker != null) {
                    checkForStand = tracker.isCheckForStand();
                }
                realm.close();
                return Observable.just(checkForStand);
            }).subscribe(checkForStand -> {

            });
        }
    }

    public boolean validateCallForS1Tracker(String imei) {
        if (imei.isEmpty()) {
            return false;
        }
        Realm realm = Realm.getDefaultInstance();
        RealmTracker tracker = realm.where(RealmTracker.class).equalTo("imeiNumber", imei).findFirst();
        boolean check = tracker.getType().equals(Tracker.Type.S1.toString());
        realm.close();
        return check;
    }

    public Observable<GeoPointsAnswer> requestPoints() {
        return requestPoints(true);
    }

    public Observable<TrackersAnswer> getTrackers() {
        return execute(ApiFactory.getService().getTrackers())
                .subscribeOn(Schedulers.io())
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

    public Observable<TrackersAnswer> hasTrackers() {
        return execute(ApiFactory.getService().getTrackers())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void resetTrackers(Activity activity, ArrayList<Tracker> trackers) {
        ArrayList<SMS> smses = new ArrayList<>();
        for (Tracker tracker : trackers) {
            SMS sms = tracker.getResetSmsIp(EditTrackerFragmentViewModel.TRACCAR_IP);
            if (sms != null)
                smses.add(sms);
        }
        sendSmses(activity, smses);
        Utils.setUpdateTracker();
    }
}
