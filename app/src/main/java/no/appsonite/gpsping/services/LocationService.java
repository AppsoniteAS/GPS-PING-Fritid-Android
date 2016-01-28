package no.appsonite.gpsping.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 28.01.2016
 */
public abstract class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, LocationListener {
    private GoogleApiClient client;

    public abstract long getLocationInterval();

    public abstract long getLocationFastestInterval();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
    public abstract void onLocationChanged(final Location location);

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(getLocationInterval());
        locationRequest.setFastestInterval(getLocationFastestInterval());
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}
