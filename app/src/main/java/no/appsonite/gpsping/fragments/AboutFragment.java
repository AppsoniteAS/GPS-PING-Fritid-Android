package no.appsonite.gpsping.fragments;

import android.os.Bundle;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentAboutBinding;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class AboutFragment extends BaseBindingFragment<FragmentAboutBinding, BaseFragmentViewModel> {
    private static final String TAG = "AboutFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.about);
    }

    public static AboutFragment newInstance() {
        Bundle args = new Bundle();
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
