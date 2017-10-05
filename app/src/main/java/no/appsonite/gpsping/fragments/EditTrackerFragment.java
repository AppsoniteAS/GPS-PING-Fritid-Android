package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentAddTrackerBinding;
import no.appsonite.gpsping.databinding.FragmentEditTrackerBinding;
import no.appsonite.gpsping.model.SMS;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.viewmodel.EditTrackerFragmentViewModel;
import rx.Observable;
import rx.Observer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class EditTrackerFragment extends BaseBindingFragment<FragmentEditTrackerBinding, EditTrackerFragmentViewModel> {
    private static final String TAG = "EditTrackerFragment";
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
        getBinding().updateTrackerBtn.setOnClickListener(v -> updateTracker());
//        getBinding().resetButton.setOnClickListener(view -> resetTracker());
        getBinding().startButton.setOnClickListener(v -> switchTrackerState());
        initRepeatTime();
    }

    private void switchTrackerState() {
        if (getModel().tracker.get() == null)
            return;
        showProgress();
        getModel().switchState(getActivity()).subscribe(sms -> {

        }, this::showError, this::hideProgress);
    }

    private void initRepeatTime() {
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

        getBinding().shockAlarmActive.setOnClickListener(view -> {
            getModel().tracker.get().shockAlarmActive.set(!getModel().tracker.get().shockAlarmActive.get());
            Observable<Boolean> observable = getModel().updateShockAlarm(getActivity());
            if (observable != null) {
                showProgress();
                observable.subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        hideProgress();
                        Toast.makeText(getActivity(), getString(R.string.trackerUpdated), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showError(e);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                    }
                });
            }
        });

        getBinding().shockFlashActive.setOnClickListener(view -> {
            getModel().tracker.get().shockFlashActive.set(!getModel().tracker.get().shockFlashActive.get());
            Observable<Boolean> observable = getModel().updateShockFlashAlarm(getActivity());
            if (observable != null) {
                showProgress();
                observable.subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        hideProgress();
                        Toast.makeText(getActivity(), getString(R.string.trackerUpdated), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showError(e);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
            }
        });

        getBinding().sleepMode.setOnClickListener(v -> {
            getModel().tracker.get().sleepMode.set(!getModel().tracker.get().sleepMode.get());
            Observable<Boolean> observable = getModel().updateSleepMode(getActivity());
            if (observable != null) {
                showProgress();
                observable.subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showError(e);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
            }
        });

        getBinding().checkBattery.setOnClickListener(v -> {
            showProgress();
            getModel().checkBattery(getActivity()).subscribe(new Observer<SMS>() {
                @Override
                public void onCompleted() {
                    hideProgress();
                }

                @Override
                public void onError(Throwable e) {
                    showError(e);
                }

                @Override
                public void onNext(SMS sms) {

                }
            });
        });

        getBinding().ledActive.setOnClickListener(view -> {
            getModel().tracker.get().ledActive.set(!getModel().tracker.get().ledActive.get());
            Observable<Boolean> observable = getModel().updateLed(getActivity());
            if (observable != null) {
                showProgress();
                observable.subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        hideProgress();
                        Toast.makeText(getActivity(), getString(R.string.trackerUpdated), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showError(e);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
            }
        });

        getBinding().geofence.setOnClickListener(v -> getBaseActivity().replaceFragment(GeofenceFragment.newInstance(getModel().tracker.get()), true));

        getBinding().displayOptions.setOnClickListener(v -> getBaseActivity().replaceFragment(DisplayOptionsFragment.newInstance(), true));
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

    private void updateTracker() {
        Observable<Boolean> observable = getModel().updateTracker(getActivity());
        if (observable != null) {
            showProgress();
            observable.subscribe(isNew -> {
                hideProgress();
//                getBaseActivity().getSupportFragmentManager().popBackStack(TrackersFragment.TAG, 0);
                getBaseActivity().getSupportFragmentManager().popBackStack();
                if (!isNew) {
                    Toast.makeText(getActivity(), getString(R.string.trackerUpdated), Toast.LENGTH_SHORT).show();
                }
            }, this::showError);
        }
    }
}
