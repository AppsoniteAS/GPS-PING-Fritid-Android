package no.appsonite.gpsping.services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

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
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, LocationListener {
    private static final int NOTIFICATION_ID = 11;
    private static final long LOCATION_INTERVAL = 30 * 1000;
    private static final long LOCATION_INTERVAL_FASTEST = 15 * 1000;

    private GoogleApiClient client;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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
        context.startService(new Intent(context, LocationService.class));
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, LocationService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startTracking();
    }

    private void startTracking() {
        client = new GoogleApiClient.Builder(getBaseContext()).addApi(LocationServices.API)
                .addConnectionCallbacks(this).build();
        client.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationRequest();
    }

    private void startLocationRequest() {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        LocationServices.FusedLocationApi.requestLocationUpdates(client, createLocationRequest(), LocationService.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (client != null && client.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
            client.disconnect();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(final Location location) {
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

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_INTERVAL_FASTEST);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}
