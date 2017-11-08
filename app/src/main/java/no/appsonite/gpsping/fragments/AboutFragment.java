package no.appsonite.gpsping.fragments;

import android.os.Bundle;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.activities.WebViewActivity;
import no.appsonite.gpsping.databinding.FragmentAboutBinding;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class AboutFragment extends BaseBindingFragment<FragmentAboutBinding, BaseFragmentViewModel> {
    public static final String CONTACT_URL = "http://www.gpsping.no/kontakt-oss/";
    public static final String WEBSITE_URL = "http://www.gpsping.no/";
    private static final String TAG = AboutFragment.class.getSimpleName();

    public static AboutFragment newInstance() {
        Bundle args = new Bundle();
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    protected void onViewModelCreated(BaseFragmentViewModel model) {
        super.onViewModelCreated(model);
        getBinding().contactUs.setOnClickListener(v -> WebViewActivity.open(getActivity(), CONTACT_URL, getString(R.string.contactUs), false));

        getBinding().website.setOnClickListener(v -> WebViewActivity.open(getActivity(), WEBSITE_URL, getString(R.string.visitWebsite), false));

        getBinding().aboutApp.setOnClickListener(v -> getBaseActivity().replaceFragment(AboutAppFragment.newInstance(), true));
    }
}
