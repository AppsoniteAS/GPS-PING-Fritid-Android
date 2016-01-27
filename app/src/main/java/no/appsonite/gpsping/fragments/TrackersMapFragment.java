package no.appsonite.gpsping.fragments;

import android.os.Bundle;

import no.appsonite.gpsping.viewmodel.TrackersMapFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class TrackersMapFragment extends TrackersMapBaseFragment<TrackersMapFragmentViewModel> {
    private static final String TAG = "TrackersMapFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    public static TrackersMapFragment newInstance() {
        Bundle args = new Bundle();
        TrackersMapFragment fragment = new TrackersMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
