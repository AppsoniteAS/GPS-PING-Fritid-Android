package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentTrackersBinding;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.BindingHelper;
import no.appsonite.gpsping.viewmodel.TrackersFragmentViewModel;
import no.appsonite.gpsping.widget.TrackersAdapter;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class TrackersFragment extends BaseBindingFragment<FragmentTrackersBinding, TrackersFragmentViewModel> {
    public static final String TAG = "TrackersFragment";

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

    @Override
    protected void onViewModelCreated(final TrackersFragmentViewModel model) {
        super.onViewModelCreated(model);
        model.requestTrackers();
        final TrackersAdapter adapter = new TrackersAdapter() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.removeTracker:
                        model.removeTracker((Tracker) v.getTag());
                        break;
                    case R.id.editTracker:
                        getBaseActivity().replaceFragment(AddTrackerFragment.newInstance((Tracker) v.getTag()), true);
                        break;
                }
            }
        };
        adapter.setItems(model.trackers);
        getBinding().trackersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().trackersRecycler.setAdapter(adapter);
        BindingHelper.bindAdapter(adapter, model.trackers);
    }
}
