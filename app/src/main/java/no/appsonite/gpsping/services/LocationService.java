package no.appsonite.gpsping.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 28.01.2016
 */
public abstract class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, LocationListener {
    private GoogleApiClient client;
    private FusedLocationProviderClient providerClient;
    private LocationCallback locationCallback;

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

        providerClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    onLocationChanged(location);
                }
            }
        };
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        providerClient.removeLocationUpdates(locationCallback);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        providerClient.requestLocationUpdates(createLocationRequest(), locationCallback, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
        if (providerClient != null) {
            providerClient.removeLocationUpdates(locationCallback);
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
