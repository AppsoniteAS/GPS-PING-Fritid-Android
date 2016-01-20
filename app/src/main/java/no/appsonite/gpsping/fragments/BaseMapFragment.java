package no.appsonite.gpsping.fragments;

import com.google.android.gms.maps.GoogleMap;

import no.appsonite.gpsping.activities.MapActivity;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public abstract class BaseMapFragment extends BaseBindingFragment {
    public GoogleMap getMap() {
        return getMapActivity().getMap();
    }

    protected MapActivity getMapActivity() {
        return (MapActivity) getActivity();
    }

    public void onMapReady() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getMapActivity().isMapReady()) {
            onMapReady();
        }
    }
}
