package no.appsonite.gpsping.viewmodel;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.graphics.Bitmap;
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

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.FriendsAnswer;
import no.appsonite.gpsping.api.content.geo.GeoDevicePoints;
import no.appsonite.gpsping.api.content.geo.GeoItem;
import no.appsonite.gpsping.api.content.geo.GeoPoint;
import no.appsonite.gpsping.api.content.geo.GeoPointsAnswer;
import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.model.MapPoint;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.TimeInterval;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class TrackersMapFragmentViewModel extends BaseFragmentViewModel {
    private static final long INTERVAL = 5;
    boolean isActive = false;
    public ObservableArrayList<Friend> friendList = new ObservableArrayList<>();
    public ObservableArrayList<MapPoint> mapPoints = new ObservableArrayList<>();

    public Observable<FriendsAnswer> requestFriends() {
        Observable<FriendsAnswer> observable = ApiFactory.getService().getFriends()
                .cache()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
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
                all.firstName.set("All");
                friendList.add(all);
                friendList.addAll(friendsAnswer.getFriends());
            }
        });
        return observable;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        isActive = true;
        requestPoints();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isActive = false;
    }

    protected Observable<GeoPointsAnswer> requestPoints(final long from, final long to, final boolean repeat) {
        Observable<GeoPointsAnswer> observable = Observable.interval(INTERVAL, TimeUnit.SECONDS)
                .takeUntil(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return !isActive || !repeat;
                    }
                })
                .timeInterval()
                .flatMap(new Func1<TimeInterval<Long>, Observable<GeoPointsAnswer>>() {
                    @Override
                    public Observable<GeoPointsAnswer> call(TimeInterval<Long> aLong) {
                        return ApiFactory.getService().getGeoPoints(from, to)
                                .cache()
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
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
                ArrayList<MapPoint> mapPoints = new ArrayList<>();
                for (GeoItem geoItem : geoPointsAnswer.getUsers()) {
                    for (GeoDevicePoints geoDevicePoints : geoItem.getDevices()) {
                        for (GeoPoint geoPoint : geoDevicePoints.getPoints()) {
                            mapPoints.add(new MapPoint(geoItem.getUser(), geoPoint.getLat(), geoPoint.getLon()));
                        }
                        if (mapPoints.size() > 0) {
                            mapPoints.get(mapPoints.size() - 1).setLast(true);
                        }
                    }
                }
                TrackersMapFragmentViewModel.this.mapPoints.clear();
                TrackersMapFragmentViewModel.this.mapPoints.addAll(mapPoints);
            }
        });
        return observable;
    }

    public Observable<GeoPointsAnswer> requestPoints() {
        long to = new Date().getTime() / 1000l;
        long from = to - 15 * 60;
        return requestPoints(from, to, true);
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
}
