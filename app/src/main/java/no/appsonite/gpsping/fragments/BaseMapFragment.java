package no.appsonite.gpsping.fragments;

import android.databinding.ViewDataBinding;

import com.google.android.gms.maps.GoogleMap;

import no.appsonite.gpsping.activities.MapActivity;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public abstract class BaseMapFragment<B extends ViewDataBinding, M extends BaseFragmentViewModel> extends BaseBindingFragment<B, M> {
    public GoogleMap getMap() {
        return getMapActivity().getMap();
    }

    protected MapActivity getMapActivity() {
        return (MapActivity) getActivity();
    }

    public void onMapReady() {
        getMap().setMaxZoomPreference(18);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getMapActivity().isMapReady()) {
            onMapReady();
        }
    }
}
