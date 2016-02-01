package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;

import java.util.Calendar;
import java.util.Date;

import no.appsonite.gpsping.api.content.geo.GeoPointsAnswer;
import rx.Observable;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public class TrackersMapHistoryFragmentViewModel extends TrackersMapFragmentViewModel {
    public ObservableField<Date> historyDate = new ObservableField<>(new Date());

    @Override
    public Observable<GeoPointsAnswer> requestPoints() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(historyDate.get());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long from = calendar.getTimeInMillis() / 1000l;
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.SECOND, -1);
        long to = calendar.getTimeInMillis() / 1000l;
        return requestPoints(from, to, false);
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
}
