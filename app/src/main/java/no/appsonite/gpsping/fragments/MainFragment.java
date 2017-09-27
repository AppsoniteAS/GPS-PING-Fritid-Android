package no.appsonite.gpsping.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.activities.IntroActivity;
import no.appsonite.gpsping.activities.MapActivity;
import no.appsonite.gpsping.api.content.TrackersAnswer;
import no.appsonite.gpsping.databinding.FragmentMainBinding;
import no.appsonite.gpsping.model.SMS;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.Utils;
import no.appsonite.gpsping.viewmodel.MainFragmentViewModel;
import rx.Observer;
import rx.functions.Action1;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class MainFragment extends BaseBindingFragment<FragmentMainBinding, MainFragmentViewModel> implements TrackersDialogFragment.TrackersDialogListener {
    private static final String TAG = "MainFragment";

    public static MainFragment newInstance() {

        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.app_name);
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

        getBinding().introAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), IntroActivity.class));
            }
        });
        if (!Utils.isUpdateTracker()) {
            showUpdateTrackers();
        }
    }

    private void showUpdateTrackers() {
        getModel().hasTrackers().subscribe(new Action1<TrackersAnswer>() {
            @Override
            public void call(TrackersAnswer trackersAnswer) {
                if (trackersAnswer.getTrackers() != null && !trackersAnswer.getTrackers().isEmpty()) {
                    showAlertDialog(trackersAnswer.getTrackers());
                }
            }
        });
    }

    private void showAlertDialog(final ArrayList<Tracker> trackers) {
        if(getActivity() == null)
            return;
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage(R.string.updateTracker)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getModel().resetTrackers(getActivity(), trackers);
                    }
                })
                .show();
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
