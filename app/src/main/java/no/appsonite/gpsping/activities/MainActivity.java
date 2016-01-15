package no.appsonite.gpsping.activities;

import android.os.Bundle;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.fragments.LoginFragment;
import no.appsonite.gpsping.fragments.MainFragment;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        if (savedInstanceState == null) {
            replaceFragment(MainFragment.newInstance(), false);
        }
    }
}
