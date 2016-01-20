package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentTrackersMapBinding;
import no.appsonite.gpsping.viewmodel.TrackersMapFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class TrackersMapFragment extends BaseMapFragment<FragmentTrackersMapBinding, TrackersMapFragmentViewModel> {
    private static final String TAG = "TrackersMapFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.map);
    }

    public static TrackersMapFragment newInstance() {
        Bundle args = new Bundle();
        TrackersMapFragment fragment = new TrackersMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.removeTracks) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onViewModelCreated(TrackersMapFragmentViewModel model) {
        super.onViewModelCreated(model);
        model.requestFriends();
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }
    }
}
