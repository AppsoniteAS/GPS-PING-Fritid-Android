package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentEditTrackerBinding;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.viewmodel.EditTrackerFragmentViewModel;
import rx.Observable;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class EditTrackerFragment extends BaseBindingFragment<FragmentEditTrackerBinding, EditTrackerFragmentViewModel> {
    private static final String TAG = EditTrackerFragment.class.getSimpleName();
    private static final String ARG_TRACKER = "arg_tracker";

    public static EditTrackerFragment newInstance(Tracker tracker) {
        Bundle args = new Bundle();
        EditTrackerFragment fragment = new EditTrackerFragment();
        args.putSerializable(ARG_TRACKER, tracker);
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
    protected void onViewModelCreated(EditTrackerFragmentViewModel model) {
        super.onViewModelCreated(model);
        model.tracker.set((Tracker) getArguments().getSerializable(ARG_TRACKER));
        initBlocks();
    }

    private void initBlocks() {
        initStartBtn();
        initSignalBlock();
        initSleepModeBlock();
        initBikeTrackingBlock();
        initGeofenceBlock();
        initUpdateBtn();
        initResetBtn();
    }

    private void initStartBtn() {
        getBinding().startStopBtn.setOnClickListener(v -> switchTrackerState());
    }

    private void switchTrackerState() {
        if (getModel().tracker.get() == null)
            return;
        showProgress();
        getModel().switchState(getActivity()).subscribe(sms -> {

        }, this::showError, this::hideProgress);
    }

    private void initSignalBlock() {
        final String[] variantsValues = getModel().tracker.get().getEntriesValues();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getModel().tracker.get().getEntriesText());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getBinding().signalTimeSpinner.setAdapter(spinnerArrayAdapter);

        String repeatTime = getModel().tracker.get().signalRepeatTime.get();
        int selected = 4;
        for (int i = 0; i < variantsValues.length; i++) {
            if (variantsValues[i].equals(repeatTime)) {
                selected = i;
                break;
            }
        }

        getBinding().signalTimeSpinner.setSelection(Math.min(selected, variantsValues.length - 1));
        getBinding().signalTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getModel().tracker.get().signalRepeatTime.set(variantsValues[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initSleepModeBlock() {
        getBinding().checkBattery.setOnClickListener(v -> {
            showProgress();
            getModel().checkBattery(getActivity()).subscribe(sms -> {

            }, this::showError, this::hideProgress);
        });

        getBinding().sleepMode.setOnClickListener(v -> {
            getModel().tracker.get().sleepMode.set(!getModel().tracker.get().sleepMode.get());
            Observable<Boolean> observable = getModel().updateSleepMode(getActivity());
            if (observable != null) {
                showProgress();
                observable.subscribe(aBoolean -> {

                }, this::showError, this::hideProgress);
            }
        });
    }

    private void initBikeTrackingBlock() {
        getBinding().ledActive.setOnClickListener(view -> {
            getModel().tracker.get().ledActive.set(!getModel().tracker.get().ledActive.get());
            Observable<Boolean> observable = getModel().updateLed(getActivity());
            if (observable != null) {
                showProgress();
                observable.subscribe(aBoolean -> {

                }, this::showError, this::hideProgressAndShowToastTrackerUpdated);
            }
        });

        getBinding().shockAlarmActive.setOnClickListener(view -> {
            getModel().tracker.get().shockAlarmActive.set(!getModel().tracker.get().shockAlarmActive.get());
            Observable<Boolean> observable = getModel().updateShockAlarm(getActivity());
            if (observable != null) {
                showProgress();
                observable.subscribe(aBoolean -> {

                }, this::showError, this::hideProgressAndShowToastTrackerUpdated);
            }
        });

        getBinding().shockFlashActive.setOnClickListener(view -> {
            getModel().tracker.get().shockFlashActive.set(!getModel().tracker.get().shockFlashActive.get());
            Observable<Boolean> observable = getModel().updateShockFlashAlarm(getActivity());
            if (observable != null) {
                showProgress();
                observable.subscribe(aBoolean -> {

                }, this::showError, this::hideProgressAndShowToastTrackerUpdated);
            }
        });
    }

    private void initGeofenceBlock() {
        getBinding().geofence.setOnClickListener(v -> getBaseActivity().replaceFragment(GeofenceFragment.newInstance(getModel().tracker.get()), true));
    }

    private void initUpdateBtn() {
        getBinding().updateTrackerBtn.setOnClickListener(v -> updateTracker());
    }

    private void updateTracker() {
        Observable<Boolean> observable = getModel().updateTracker(getActivity());
        if (observable != null) {
            showProgress();
            observable.subscribe(isNew -> {
                hideProgress();
                getBaseActivity().getSupportFragmentManager().popBackStack();
                if (!isNew)
                    showToastTrackerUpdated();
            }, this::showError);
        }
    }

    private void showToastTrackerUpdated() {
        Toast.makeText(getActivity(), getString(R.string.trackerUpdated), Toast.LENGTH_SHORT).show();
    }

    private void hideProgressAndShowToastTrackerUpdated() {
        hideProgress();
        showToastTrackerUpdated();
    }

    private void initResetBtn() {
//        getBinding().resetButton.setOnClickListener(view -> resetTracker());
    }

//    private void resetTracker() {
//        Observable<SMS> observable = getModel().resetTracker(getActivity());
//        if (observable != null) {
//            showProgress();
//            observable.subscribe(new Observer<SMS>() {
//                @Override
//                public void onCompleted() {
//
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    showError(e);
//                }
//
//                @Override
//                public void onNext(SMS sms) {
//                    hideProgress();
//                    Toast.makeText(getActivity(), getString(R.string.trackerUpdated), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
}
