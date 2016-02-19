package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.activities.MapActivity;
import no.appsonite.gpsping.databinding.FragmentMainBinding;
import no.appsonite.gpsping.model.SMS;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.viewmodel.MainFragmentViewModel;
import rx.Observer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class MainFragment extends BaseBindingFragment<FragmentMainBinding, MainFragmentViewModel> implements TrackersDialogFragment.TrackersDialogListener {
    private static final String TAG = "MainFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.app_name);
    }

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onViewModelCreated(MainFragmentViewModel model) {
        super.onViewModelCreated(model);
        getBinding().settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().replaceFragment(SettingsFragment.newInstance(), true);
            }
        });

        getBinding().trackers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().replaceFragment(TrackersFragment.newInstance(), true);
            }
        });

        getBinding().startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTrackerState();
            }
        });

        getBinding().mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity.start(getContext(), MapActivity.Type.ACTIVE);
            }
        });

        getBinding().historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity.start(getContext(), MapActivity.Type.HISTORY);
            }
        });
    }

    private void switchTrackerState() {
        if (getModel().activeTracker.get() == null)
            return;
        showProgress();
        getModel().switchState(getActivity()).subscribe(new Observer<SMS>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                showError(e);
            }

            @Override
            public void onNext(SMS sms) {
                hideProgress();
            }
        });
    }

    @Override
    public void onSelect(Tracker tracker) {
        Toast.makeText(getActivity(), tracker.trackerName.get(), Toast.LENGTH_SHORT).show();
    }
}
