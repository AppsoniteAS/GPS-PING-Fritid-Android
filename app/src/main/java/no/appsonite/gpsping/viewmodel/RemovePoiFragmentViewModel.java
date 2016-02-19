package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;

import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.api.content.Poi;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 10.02.2016
 */
public class RemovePoiFragmentViewModel extends BaseFragmentViewModel {
    public ObservableField<Poi> poi = new ObservableField<>();

    public Observable<ApiAnswer> removePoi() {
        return execute(ApiFactory.getService().removePoi(poi.get().getId()))
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
