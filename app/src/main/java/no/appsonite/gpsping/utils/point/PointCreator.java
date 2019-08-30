package no.appsonite.gpsping.utils.point;

import java.util.List;

import no.appsonite.gpsping.api.content.geo.GeoDevice;
import no.appsonite.gpsping.api.content.geo.GeoPoint;
import no.appsonite.gpsping.data_structures.LatLonData;
import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.model.MapPoint;

/**
 * Created by taras on 11/15/17.
 */

public class PointCreator {
    private LatLonData latLonData;

    public PointCreator() {
        this.latLonData = new LatLonData();
    }

    public MapPoint createPointMainDeviceWithAvatar(Friend user, GeoDevice geoDevice) {
        latLonData.add(geoDevice.getLastLat(), geoDevice.getLastLon());

        return createPointMain(user, geoDevice, true);
    }

    public MapPoint createPointMainDeviceWithoutAvatar(Friend user, GeoDevice geoDevice, List<MapPoint> devicePoints) {
        MapPoint mapPoint = null;
        if (!latLonData.contains(geoDevice.getLastLat(), geoDevice.getLastLon())) {
            if (devicePoints.isEmpty()) {
                latLonData.add(geoDevice.getLastLat(), geoDevice.getLastLon());

                mapPoint = createPointMain(user, geoDevice, false);
            }
        }
        return mapPoint;
    }

    private MapPoint createPointMain(Friend user, GeoDevice geoDevice, boolean isAvatar) {
        return new MapPoint(
                user,
                geoDevice.getLastLat(),
                geoDevice.getLastLon(),
                geoDevice.getName(),
                geoDevice.getImeiNumber(),
                geoDevice.getTrackerNumber(),
                geoDevice.getLastTimestamp(),
                geoDevice.getPicUrl(),
                geoDevice.getDirection(),
                geoDevice.getSpeed(),
                geoDevice.getGsmSignal(),
                geoDevice.getGpsSignal(),
                geoDevice.getAttributes(),
                geoDevice.getDistance(),
                geoDevice.getDistanceTravelled(),
                geoDevice.getDailyTrack(),
                isAvatar);
    }

    public MapPoint createPointSecondary(Friend user, GeoDevice geoDevice, GeoPoint geoPoint) {
        MapPoint mapPoint = null;
        if (!latLonData.contains(geoPoint.getLat(), geoPoint.getLon())) {
            mapPoint = new MapPoint(
                    user,
                    geoPoint.getLat(),
                    geoPoint.getLon(),
                    geoDevice.getName(),
                    geoDevice.getImeiNumber(),
                    geoDevice.getTrackerNumber(),
                    geoPoint.getTimestamp(),
                    geoDevice.getPicUrl(),
                    geoPoint.getDirection(),
                    geoPoint.getSpeed(),
                    geoPoint.getGsmSignal(),
                    geoPoint.getGpsSignal(),
                    geoPoint.getAttributes(),
                    geoPoint.getDistance(),
                    geoPoint.getDistanceTravelled(),
                    geoPoint.getDailyTrack(),
                    false);
        }
        return mapPoint;
    }

    public MapPoint createUserPoint(Friend user) {
        MapPoint mapPoint = new MapPoint(
                user,
                user.lat,
                user.lon,
                user.getName(),
                null,
                null,
                user.lastUpdate);
        mapPoint.setBelongsToUser(true);
        return mapPoint;
    }
}
