package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.support.v4.app.NavUtils;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentAboutAppBinding;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public class AboutAppFragment extends BaseBindingFragment<FragmentAboutAppBinding, BaseFragmentViewModel> {
    private static final String TAG = "AboutAppFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.aboutApp);
    }

    public static AboutAppFragment newInstance() {
        Bundle args = new Bundle();
        AboutAppFragment fragment = new AboutAppFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
