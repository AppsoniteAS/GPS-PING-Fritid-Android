package no.appsonite.gpsping.fragments;

import android.os.Bundle;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentDisplayOptionsBinding;
import no.appsonite.gpsping.viewmodel.DisplayOptionsFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 29.01.2016
 */
public class DisplayOptionsFragment extends BaseBindingFragment<FragmentDisplayOptionsBinding, DisplayOptionsFragmentViewModel> {
    private static final String TAG = "DisplayOptionsFragment";

    public static DisplayOptionsFragment newInstance() {
        Bundle args = new Bundle();
        DisplayOptionsFragment fragment = new DisplayOptionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.displayOptions);
    }

    @Override
    protected void onViewModelCreated(DisplayOptionsFragmentViewModel model) {
        super.onViewModelCreated(model);
        getBinding().saveButton.setOnClickListener(v -> {
            getModel().save();
            getBaseActivity().getSupportFragmentManager().popBackStack();
        });
    }
}
