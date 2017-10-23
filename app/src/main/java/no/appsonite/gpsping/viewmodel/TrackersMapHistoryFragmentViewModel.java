package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import no.appsonite.gpsping.api.content.geo.GeoDevicePoints;
import no.appsonite.gpsping.api.content.geo.GeoItem;
import no.appsonite.gpsping.api.content.geo.GeoPointsAnswer;
import no.appsonite.gpsping.model.MapPoint;
import rx.Observable;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public class TrackersMapHistoryFragmentViewModel extends TrackersMapFragmentViewModel {
    public ObservableField<Date> historyDate = new ObservableField<>(null);

    @Override
    public Observable<GeoPointsAnswer> requestPoints() {
        return requestPoints(false);
    }

    @Override
    protected long getFrom() {
        if (historyDate.get() == null) {
            return super.getFrom();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(historyDate.get());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis() / 1000l;
    }

    @Override
    protected long getTo() {
        if (historyDate.get() == null) {
            return super.getTo();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(historyDate.get());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.SECOND, -1);
        return calendar.getTimeInMillis() / 1000l;
    }

    @Override
    public void onModelAttached() {
        super.onModelAttached();
        historyDate.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                requestPoints();
            }
        });
    }

    @Override
    protected void playStandSound() {
        //ignore
    }

    @Override
    protected void parseGeoPointsAnswer(GeoPointsAnswer geoPointsAnswer) {
        if (historyDate.get() != null) {
            super.parseGeoPointsAnswer(geoPointsAnswer);
            return;
        }

        ArrayList<MapPoint> mapPoints = new ArrayList<>();
        for (GeoItem geoItem : geoPointsAnswer.getUsers()) {
            for (GeoDevicePoints geoDevicePoints : geoItem.getDevices()) {
                MapPoint mapPoint = new MapPoint(geoItem.getUser(),
                        geoDevicePoints.getDevice().getLastLat(),
                        geoDevicePoints.getDevice().getLastLon(),
                        geoDevicePoints.getDevice().getName(),
                        geoDevicePoints.getDevice().getImeiNumber(),
                        geoDevicePoints.getDevice().getTrackerNumber(),
                        geoDevicePoints.getDevice().getLastTimestamp(),
                        geoDevicePoints.getDevice().getPicUrl());
                mapPoint.setLast(true);
                mapPoints.add(mapPoint);
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
        this.mapPoints.clear();
        this.mapPoints.addAll(mapPoints);
    }
}
