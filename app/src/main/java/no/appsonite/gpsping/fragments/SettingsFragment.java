package no.appsonite.gpsping.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import io.realm.Realm;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.Profile;
import no.appsonite.gpsping.databinding.FragmentSettingsBinding;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.Tracker;
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
        getBinding().about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().replaceFragment(AboutFragment.newInstance(), true);
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

        getBinding().faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBaseActivity().replaceFragment(FAQFragment.newInstance(), true);
            }
        });

        getBinding().pauseSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tracker tracker = getCurrentTracker();
                String imei = tracker != null ? tracker.imeiNumber.get() : "noImei";
                String phone = tracker != null ? tracker.trackerNumber.get() : "noPhone";
                Profile user = AuthHelper.getCredentials().getUser();
                String email = "support@gpsping.no";
                String body = getString(R.string.pauseSubscriptionEmailBody, user.firstName.get() + " " + user.lastName.get(), user.address.get(), user.username.get(), imei, phone);
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

    private Tracker getCurrentTracker() {
        Realm realm = Realm.getDefaultInstance();
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("isEnabled", true).findFirst();
        Tracker tracker = null;
        if (realmTracker != null) {
            tracker = new Tracker(realmTracker);
        }
        realm.close();
        return tracker;
    }
}
