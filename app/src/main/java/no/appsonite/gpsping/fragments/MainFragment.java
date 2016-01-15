package no.appsonite.gpsping.fragments;

import android.os.Bundle;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentMainBinding;
import no.appsonite.gpsping.viewmodel.MainFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class MainFragment extends BaseBindingFragment<FragmentMainBinding, MainFragmentViewModel> implements MainFragmentViewModel.ActionsListener {
    private static final String TAG = "MainFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.app_name);
    }

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onViewModelCreated(MainFragmentViewModel model) {
        super.onViewModelCreated(model);
        model.setActionsListener(this);
    }

    @Override
    public void mapAndTrackingClick() {

    }

    @Override
    public void historyClick() {

    }

    @Override
    public void settingsClick() {
        getBaseActivity().replaceFragment(SettingsFragment.newInstance(), true);
    }

    @Override
    public void onStartClick() {

    }
}
