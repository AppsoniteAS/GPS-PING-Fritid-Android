package no.appsonite.gpsping.activities;

import android.os.Bundle;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.fragments.LoginFragment;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        if (savedInstanceState == null) {
            replaceFragment(LoginFragment.newInstance(), false);
        }
    }
}
