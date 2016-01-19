package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.view.View;

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
    private static final String TAG = "AboutFragment";
    public static final String CONTACT_URL = "http://www.gpsping.no/kontakt-oss/";
    public static final String WEBSITE_URL = "http://www.gpsping.no/";

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

    @Override
    protected void onViewModelCreated(BaseFragmentViewModel model) {
        super.onViewModelCreated(model);
        getBinding().contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.open(getActivity(), CONTACT_URL, getString(R.string.contactUs));
            }
        });

        getBinding().website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.open(getActivity(), WEBSITE_URL, getString(R.string.visitWebsite));
            }
        });

        getBinding().aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().replaceFragment(AboutAppFragment.newInstance(), true);
            }
        });
    }
}
