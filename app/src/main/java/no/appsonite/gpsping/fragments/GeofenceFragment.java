package no.appsonite.gpsping.fragments;

import android.os.Bundle;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentGeofenceBinding;
import no.appsonite.gpsping.viewmodel.GeofenceFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class GeofenceFragment extends BaseBindingFragment<FragmentGeofenceBinding, GeofenceFragmentViewModel> {
    private static final String TAG = "GeofenceFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    public static GeofenceFragment newInstance() {
        Bundle args = new Bundle();
        GeofenceFragment fragment = new GeofenceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.geofence);
    }
}
