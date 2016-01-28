package no.appsonite.gpsping.services;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 28.01.2016
 */
public class LocationTrackerService extends LocationService {
    private static final int NOTIFICATION_ID = 11;
    private static final long LOCATION_INTERVAL = 30 * 1000;
    private static final long LOCATION_INTERVAL_FASTEST = 15 * 1000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, getNotification());
        return START_STICKY;
    }

    private Notification getNotification() {
        return new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.locationMessage))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
    }

    public static void startService(Context context) {
        context.startService(new Intent(context, LocationTrackerService.class));
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, LocationTrackerService.class));
    }

    @Override
    public long getLocationInterval() {
        return LOCATION_INTERVAL;
    }

    @Override
    public long getLocationFastestInterval() {
        return LOCATION_INTERVAL_FASTEST;
    }

    @Override
    public void onLocationChanged(Location location) {
        ApiFactory.getService().setUserPosition(location.getLatitude(), location.getLongitude())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ApiAnswer>() {
                    @Override
                    public void call(ApiAnswer apiAnswer) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }
}
