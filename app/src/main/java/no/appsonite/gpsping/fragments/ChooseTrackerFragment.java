package no.appsonite.gpsping.fragments;

import android.os.Bundle;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentChooseTrackerBinding;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class ChooseTrackerFragment extends BaseBindingFragment<FragmentChooseTrackerBinding, BaseFragmentViewModel> {
    private static final String TAG = "ChooseTrackerFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.addTracker);
    }

    @Override
    protected void onViewModelCreated(BaseFragmentViewModel model) {
        super.onViewModelCreated(model);

    }

    public static ChooseTrackerFragment newInstance() {
        Bundle args = new Bundle();
        ChooseTrackerFragment fragment = new ChooseTrackerFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
