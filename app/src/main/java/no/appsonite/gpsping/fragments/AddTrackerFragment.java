package no.appsonite.gpsping.fragments;

import android.os.Bundle;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentAddTrackerBinding;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.viewmodel.AddTrackerFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class AddTrackerFragment extends BaseBindingFragment<FragmentAddTrackerBinding, AddTrackerFragmentViewModel> {
    private static final String TAG = "AddTrackerFragment";
    private static final String ARG_TRACKER = "arg_tracker";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.addTracker);
    }

    public static AddTrackerFragment newInstance(Tracker tracker) {
        Bundle args = new Bundle();
        AddTrackerFragment fragment = new AddTrackerFragment();
        args.putSerializable(ARG_TRACKER, tracker);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onViewModelCreated(AddTrackerFragmentViewModel model) {
        super.onViewModelCreated(model);
        model.tracker.set((Tracker) getArguments().getSerializable(ARG_TRACKER));
        model.setActionListener(new AddTrackerFragmentViewModel.ActionListener() {
            @Override
            public void onAddTracker() {
                getBaseActivity().getSupportFragmentManager().popBackStack(TrackersFragment.TAG, 0);
            }
        });
    }
}
