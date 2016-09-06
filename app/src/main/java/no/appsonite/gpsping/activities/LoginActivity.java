package no.appsonite.gpsping.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.fragments.LoginFragment;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        if (savedInstanceState == null) {
            replaceFragment(LoginFragment.newInstance(), false);
        }

        if (AuthHelper.getCredentials() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("INTRO", MODE_PRIVATE);
        boolean introCompleted = sharedPreferences.getBoolean("INTRO_COMPLETED", false);
        if (!introCompleted) {
            startActivity(new Intent(this, IntroActivity.class));
        }
    }
}
