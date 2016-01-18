package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentTrackersBinding;
import no.appsonite.gpsping.viewmodel.TrackersFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class TrackersFragment extends BaseBindingFragment<FragmentTrackersBinding, TrackersFragmentViewModel> {
    private static final String TAG = "TrackersFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.trackers);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addTracker) {
            getBaseActivity().replaceFragment(ChooseTrackerFragment.newInstance(), true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trackers, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static TrackersFragment newInstance() {

        Bundle args = new Bundle();

        TrackersFragment fragment = new TrackersFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
