package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import no.appsonite.gpsping.databinding.FragmentSettingsBinding;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class SettingsFragment extends BaseBindingFragment<FragmentSettingsBinding, BaseFragmentViewModel> {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBinding().connect.setOnClickListener(v -> getBaseActivity().replaceFragment(FriendsFragment.newInstance(), true));

        getBinding().profile.setOnClickListener(v -> getBaseActivity().replaceFragment(ProfileFragment.newInstance(), true));

        getBinding().faq.setOnClickListener(view1 -> getBaseActivity().replaceFragment(FAQFragment.newInstance(), true));

        getBinding().about.setOnClickListener(v -> getBaseActivity().replaceFragment(AboutFragment.newInstance(), true));
    }
}
