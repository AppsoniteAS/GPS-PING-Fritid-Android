package no.appsonite.gpsping.viewmodel;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import no.appsonite.gpsping.api.content.PoiAnswer;
import no.appsonite.gpsping.api.content.Profile;
import no.appsonite.gpsping.api.content.geo.GeoDevicePoints;
import no.appsonite.gpsping.api.content.geo.GeoItem;
import no.appsonite.gpsping.api.content.geo.GeoPoint;
import no.appsonite.gpsping.api.content.geo.GeoPointsAnswer;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.model.MapPoint;
import no.appsonite.gpsping.utils.ObservableString;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
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
    private Date removeTracksDate = new Date(0l);
    private MediaPlayer mediaPlayer;
    private int standSound = R.raw.bleep;
    public ObservableString distance = new ObservableString("");
    public ObservableField<Friend> currentFriend = new ObservableField<>();
    public ObservableArrayList<Friend> friendList = new ObservableArrayList<>();
    public ObservableArrayList<MapPoint> mapPoints = new ObservableArrayList<>();
    public ObservableField<MapPoint> currentMapPoint = new ObservableField<>();
    public ObservableField<Poi> currentPoi = new ObservableField<>();
    public ObservableArrayList<Poi> pois = new ObservableArrayList<>();

    public Observable<FriendsAnswer> requestFriends() {
        Observable<FriendsAnswer> observable = execute(ApiFactory.getService().getFriends());
        observable.subscribe(new Observer<FriendsAnswer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(FriendsAnswer friendsAnswer) {
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
        });
        return observable;
    }

    public void setRemoveTracksDate(Date removeTracksDate) {
        this.removeTracksDate = removeTracksDate;
        restartRequest();
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

    PublishSubject<Object> cancelRequest = PublishSubject.create();

    protected long getFrom() {
        return Math.max(removeTracksDate.getTime() / 1000l, getTo() - DisplayOptionsFragmentViewModel.getHistoryValueSeconds());
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
                .flatMap(new Func1<TimeInterval<Long>, Observable<GeoPointsAnswer>>() {
                    @Override
                    public Observable<GeoPointsAnswer> call(TimeInterval<Long> aLong) {
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

                    }
                })
                .cache()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<GeoPointsAnswer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(GeoPointsAnswer geoPointsAnswer) {
                parseGeoPointsAnswer(geoPointsAnswer);
            }
        });
        return observable;
    }

    protected void parseGeoPointsAnswer(GeoPointsAnswer geoPointsAnswer) {
        ArrayList<MapPoint> mapPoints = new ArrayList<>();
        for (GeoItem geoItem : geoPointsAnswer.getUsers()) {
            for (GeoDevicePoints geoDevicePoints : geoItem.getDevices()) {
                ArrayList<MapPoint> devicePoints = new ArrayList<>();
                for (GeoPoint geoPoint : geoDevicePoints.getPoints()) {
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
        TrackersMapFragmentViewModel.this.mapPoints.clear();
        TrackersMapFragmentViewModel.this.mapPoints.addAll(mapPoints);
    }

    private void checkForStand(ArrayList<MapPoint> mapPoints) {
        if (mapPoints.size() > 1) {
            final MapPoint last = mapPoints.get(mapPoints.size() - 1);
            final MapPoint prev = mapPoints.get(mapPoints.size() - 2);
            Observable.defer(new Func0<Observable<Boolean>>() {
                @Override
                public Observable<Boolean> call() {
                    Realm realm = Realm.getDefaultInstance();
                    RealmTracker tracker = realm.where(RealmTracker.class).equalTo("imeiNumber", prev.getImeiNumber()).findFirst();
                    Boolean checkForStand = false;
                    if (tracker != null) {
                        checkForStand = tracker.isCheckForStand();
                    }
                    realm.close();
                    return Observable.just(checkForStand);
                }
            }).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean checkForStand) {
                    if (checkForStand) {
                        if (Math.abs(prev.getDistanceFor(last)) <= 10) {
                            playStandSound();
                        }
                    }
                }
            });

        }
    }

    public Observable<GeoPointsAnswer> requestPoints() {
        return requestPoints(true);
    }


    public void saveBitmap(final Bitmap bitmap) {
        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return saveBitmapToGallery(bitmap);
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String filePath) {
                        notifyBitmapSaved(filePath);
                    }
                });
    }

    private void notifyBitmapSaved(String filePath) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + filePath), "image/*");
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 12, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(getContext().getString(R.string.notify_photo_title))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(getContext().getString(R.string.notify_photo_text));
        notificationManager.notify(100, builder.build());
    }

    private Observable<String> saveBitmapToGallery(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bytes);
        File directory = new File(Environment.getExternalStorageDirectory()
                + File.separator + getContext().getString(R.string.app_name) + File.separator);
        directory.mkdirs();
        File outputFile = new File(directory, new Date().toString() + ".png");
        try {
            outputFile.createNewFile();
            FileOutputStream fo = new FileOutputStream(outputFile);
            fo.write(bytes.toByteArray());
            fo.close();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, outputFile.getPath());
            values.put(MediaStore.Images.Media.DATE_TAKEN, outputFile.lastModified());
            if (getContext().getContentResolver() != null) {
                getContext().getContentResolver()
                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                getContext().getContentResolver().notifyChange(
                        Uri.parse("file://" + outputFile.getPath()), null);
            }
            return Observable.just(outputFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void playStandSound() {
        if (true)
            return;
        mediaPlayer = MediaPlayer.create(Application.getContext(), standSound);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
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
                .observeOn(AndroidSchedulers.mainThread()).flatMap(new Func1<PoiAnswer, Observable<ArrayList<Poi>>>() {
            @Override
            public Observable<ArrayList<Poi>> call(PoiAnswer poiAnswer) {
                ArrayList<Poi> result = new ArrayList<>();
                for (Poi poi : poiAnswer.getPoi()) {
                    Friend friend = getFriendById(poi.getUserId());
                    if (friend != null) {
                        poi.setUser(friend);
                        result.add(poi);
                    }
                }
                return Observable.just(result);
            }
        }).subscribe(new Action1<ArrayList<Poi>>() {
            @Override
            public void call(ArrayList<Poi> pois) {
                TrackersMapFragmentViewModel.this.pois.clear();
                TrackersMapFragmentViewModel.this.pois.addAll(pois);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
