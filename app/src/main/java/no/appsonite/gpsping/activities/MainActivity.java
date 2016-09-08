package no.appsonite.gpsping.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.fragments.FriendsFragment;
import no.appsonite.gpsping.fragments.MainFragment;
import no.appsonite.gpsping.utils.PushHelper;
import no.appsonite.gpsping.viewmodel.SubscriptionViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class MainActivity extends BaseActivity implements DialogInterface.OnCancelListener {
    private static final String EXTRA_FRIENDS = "extra_friends";
    private static final String EXTRA_LOGOUT = "extra_logout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        if (savedInstanceState == null) {
            replaceFragment(MainFragment.newInstance(), false);
        }

        parseIntent(getIntent());

        PushHelper.sendPushToken();

        SharedPreferences sharedPreferences = getSharedPreferences("INTRO", MODE_PRIVATE);
        boolean introCompleted = sharedPreferences.getBoolean("INTRO_COMPLETED", false);
        if (!introCompleted) {
            startActivity(new Intent(this, IntroActivity.class));
        }
    }

    public static void logout(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_LOGOUT, true);
        context.startActivity(intent);
    }

    private void parseIntent(Intent intent) {
        if (intent == null)
            return;
        if (intent.getBooleanExtra(EXTRA_FRIENDS, false)) {
            replaceFragment(FriendsFragment.newInstance(), true);
        }
        if (intent.getBooleanExtra(EXTRA_LOGOUT, false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGooglePlayServices();
    }

    public static Intent getFriendsIntent() {
        Intent intent = new Intent(Application.getContext(), MainActivity.class);
        intent.putExtra(EXTRA_FRIENDS, true);
        return intent;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(intent);
    }

    private boolean checkGooglePlayServices() {
        int statusGS = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (statusGS != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, statusGS, 0, this).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getLastFragment() != null) {
            if (getLastFragment().getModel() instanceof SubscriptionViewModel) {
                if (((SubscriptionViewModel) getLastFragment().getModel()).onActivityResult(requestCode, resultCode, data)) {
                    return;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        finish();
    }
}
