package no.appsonite.gpsping.utils.point;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import no.appsonite.gpsping.api.content.geo.GeoDevicePoints;
import no.appsonite.gpsping.api.content.geo.GeoItem;
import no.appsonite.gpsping.api.content.geo.GeoPoint;
import no.appsonite.gpsping.api.content.geo.GeoPointsAnswer;
import no.appsonite.gpsping.data_structures.ColorArrowPin;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.enums.ColorPin;
import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.model.MapPoint;
import rx.Observable;

/**
 * Created by taras on 11/15/17.
 */

public class CreatePointManager {
    private List<MapPoint> mapPoints;
    private PointCreator pointCreator;
    private ColorArrowPin colorArrowPin;

    public CreatePointManager() {
        mapPoints = new ArrayList<>();
        pointCreator = new PointCreator();
        colorArrowPin = new ColorArrowPin();
    }

    public ColorPin getColorPin(String name) {
        return colorArrowPin.get(name);
    }

    public List<MapPoint> getMapPoints(GeoPointsAnswer geoPointsAnswer) {
        for (GeoItem geoItem : geoPointsAnswer.getUsers()) {

            createUserPoint(geoItem.getUser());

            for (GeoDevicePoints geoDevicePoints : geoItem.getDevices()) {

                MapPoint point = pointCreator.createPointMainDeviceWithAvatar(geoItem.getUser(), geoDevicePoints.getDevice());
                mapPoints.add(point);

                ArrayList<MapPoint> devicePoints = new ArrayList<>();
                for (GeoPoint geoPoint : geoDevicePoints.getPoints()) {

                    MapPoint mapPoint = pointCreator.createPointSecondary(geoItem.getUser(), geoDevicePoints.getDevice(), geoPoint);
                    if (mapPoint != null) {
                        devicePoints.add(mapPoint);
                        mapPoints.add(mapPoint);
                    }

                }
                try {
                    MapPoint mapPoint = pointCreator.createPointMainDeviceWithoutAvatar(geoItem.getUser(), geoDevicePoints.getDevice(), devicePoints);
                    if (mapPoint != null) {
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
        }

        for (MapPoint mapPoint : mapPoints) {
            if (!mapPoint.isBelongsToUser()) {
                if (!mapPoint.getImeiNumber().isEmpty()) {
                    colorArrowPin.add(mapPoint.getName());
                }
            }
        }
        for (MapPoint mapPoint : mapPoints) {
            if (!mapPoint.isBelongsToUser()) {
                if (mapPoint.getImeiNumber().isEmpty()) {
                    colorArrowPin.add(mapPoint.getName());
                }
            }
        }
        return mapPoints;
    }

    public List<MapPoint> getMapPointsHistory(GeoPointsAnswer geoPointsAnswer) {
        for (GeoItem geoItem : geoPointsAnswer.getUsers()) {

            createUserPoint(geoItem.getUser());

            for (GeoDevicePoints geoDevicePoints : geoItem.getDevices()) {
                MapPoint mapPoint = pointCreator.createPointMainDeviceWithAvatar(geoItem.getUser(), geoDevicePoints.getDevice());
                mapPoint.setLast(true);
                mapPoints.add(mapPoint);
            }
        }
        for (MapPoint mapPoint : mapPoints) {
            if (!mapPoint.isBelongsToUser()) {
                if (!mapPoint.getImeiNumber().isEmpty()) {
                    colorArrowPin.add(mapPoint.getName());
                }
            }
        }
        for (MapPoint mapPoint : mapPoints) {
            if (!mapPoint.isBelongsToUser()) {
                if (mapPoint.getImeiNumber().isEmpty()) {
                    colorArrowPin.add(mapPoint.getName());
                }
            }
        }
        return mapPoints;
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

    private void createUserPoint(Friend user) {
        MapPoint userMapPoint = pointCreator.createUserPoint(user);
        mapPoints.add(userMapPoint);
    }
}
