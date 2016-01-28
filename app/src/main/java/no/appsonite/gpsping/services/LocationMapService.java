package no.appsonite.gpsping.services;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import no.appsonite.gpsping.utils.RxBus;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 28.01.2016
 */
public class LocationMapService extends LocationService {
    private static final long LOCATION_INTERVAL = 15 * 1000;
    private static final long LOCATION_INTERVAL_FASTEST = 10 * 1000;

    @Override
    public long getLocationInterval() {
        return LOCATION_INTERVAL;
    }

    @Override
    public long getLocationFastestInterval() {
        return LOCATION_INTERVAL_FASTEST;
    }

    public static void startService(Context context) {
        context.startService(new Intent(context, LocationMapService.class));
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, LocationMapService.class));
    }

    @Override
    public void onLocationChanged(Location location) {
        RxBus.getInstance().post(location);
    }
}
