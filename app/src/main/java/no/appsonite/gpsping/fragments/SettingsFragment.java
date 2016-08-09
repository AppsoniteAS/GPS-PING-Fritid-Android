package no.appsonite.gpsping.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.Profile;
import no.appsonite.gpsping.databinding.FragmentSettingsBinding;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class SettingsFragment extends BaseBindingFragment<FragmentSettingsBinding, BaseFragmentViewModel> {
    private static final String TAG = "SettingsFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.settings);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBinding().about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().replaceFragment(AboutFragment.newInstance(), true);
            }
        });

        getBinding().geofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().replaceFragment(GeofenceFragment.newInstance(), true);
            }
        });
        getBinding().profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().replaceFragment(ProfileFragment.newInstance(), true);
            }
        });

        getBinding().connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().replaceFragment(FriendsFragment.newInstance(), true);
            }
        });

        getBinding().displayOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().replaceFragment(DisplayOptionsFragment.newInstance(), true);
            }
        });

        getBinding().pauseSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile user = AuthHelper.getCredentials().getUser();
                String email = "support@gpsping.no";
                String body = getString(R.string.pauseSubscriptionEmailBody, user.firstName.get() + " " + user.lastName.get(), user.address.get(), user.username.get());
                String subject = getString(R.string.pauseSubscription);


                Intent gmail = new Intent(Intent.ACTION_VIEW);
                gmail.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                gmail.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                gmail.putExtra(Intent.EXTRA_SUBJECT, subject);
                gmail.setType("plain/text");
                gmail.putExtra(Intent.EXTRA_TEXT, body);
                try {
                    startActivity(gmail);
                } catch (Exception e) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.setData(Uri.parse("mailto:"));
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    i.putExtra(Intent.EXTRA_SUBJECT, subject);
                    i.putExtra(Intent.EXTRA_TEXT, body);

                    startActivity(Intent.createChooser(i, getString(R.string.pauseSubscription)));
                }


            }
        });
    }
}
