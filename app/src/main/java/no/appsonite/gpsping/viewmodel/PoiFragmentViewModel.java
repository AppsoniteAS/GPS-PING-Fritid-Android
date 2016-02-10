package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.text.TextUtils;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.api.content.Poi;
import no.appsonite.gpsping.utils.ObservableString;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 10.02.2016
 */
public class PoiFragmentViewModel extends BaseFragmentViewModel {
    public ObservableField<Poi> poi = new ObservableField<>();
    public ObservableString nameError = new ObservableString();

    public boolean isNew() {
        return poi.get().getId() == -1;
    }

    public Observable<ApiAnswer> savePoi() {
        if (!isValid())
            return null;
        if (isNew()) {
            return createPoi();
        } else {
            return updatePoi();
        }
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(poi.get().name.get())) {
            nameError.set(Application.getContext().getString(R.string.poiNameCanNotBeEmpty));
            return false;
        }
        nameError.set(null);
        return true;
    }

    private Observable<ApiAnswer> updatePoi() {
        return ApiFactory.getService().updatePoi(poi.get().getId(), poi.get().name.get(), poi.get().getLat(), poi.get().getLon())
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<ApiAnswer> createPoi() {
        return ApiFactory.getService().addPoi(poi.get().name.get(), poi.get().getLat(), poi.get().getLon())
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
