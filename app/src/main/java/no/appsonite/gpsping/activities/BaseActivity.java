package no.appsonite.gpsping.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionInflater;
import android.view.MenuItem;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.fragments.BaseBindingFragment;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 25.12.2015
 */
public class BaseActivity extends AppCompatActivity {
    public void replaceFragment(BaseBindingFragment fragment, boolean addToBackStack) {
        Bundle arguments;
        if (fragment.getArguments() == null) {
            arguments = new Bundle();
        } else {
            arguments = fragment.getArguments();
        }
        arguments.putBoolean(BaseBindingFragment.ADD_TO_BACK_STACK, addToBackStack);
        fragment.setArguments(arguments);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 21) {
            fragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_right));
            fragment.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.fade));
        } else {
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        }
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getFragmentTag());
        }
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, fragment.getFragmentTag());
        fragmentTransaction.commitAllowingStateLoss();
    }

    public BaseBindingFragment getLastFragment() {
        return (BaseBindingFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
    }

    @Override
    public void onBackPressed() {
        BaseBindingFragment lastFragment = getLastFragment();
        if (lastFragment != null && lastFragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
