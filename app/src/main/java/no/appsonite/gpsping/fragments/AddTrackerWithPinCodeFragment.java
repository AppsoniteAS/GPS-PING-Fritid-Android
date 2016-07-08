package no.appsonite.gpsping.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.activities.MainActivity;
import no.appsonite.gpsping.databinding.FragmentAddTrackerWithPinCodeBinding;
import no.appsonite.gpsping.viewmodel.AddTrackerWithPinCodeViewModel;
import rx.Observable;
import rx.Observer;

/**
 * Created: Belozerov Sergei
 * E-mail: belozerow@gmail.com
 * Company: APPGRANULA LLC
 * Date: 05/07/16
 */
public class AddTrackerWithPinCodeFragment extends BaseBindingFragment<FragmentAddTrackerWithPinCodeBinding, AddTrackerWithPinCodeViewModel> {
    private static final String ARG_AFTER_REG = "arg_after_reg";

    @Override
    public String getFragmentTag() {
        return "AddTrackerWithPinCodeFragment";
    }

    @Override
    protected String getTitle() {
        return getString(R.string.addTracker);
    }

    @Override
    protected void onViewModelCreated(AddTrackerWithPinCodeViewModel model) {
        super.onViewModelCreated(model);
        getBinding().addTrackerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindTracker();
            }
        });

        getBinding().skipAddTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });

        boolean afterReg = getArguments().getBoolean(ARG_AFTER_REG, false);
        getModel().afterReg.set(afterReg);
    }

    public static AddTrackerWithPinCodeFragment newInstance() {
        return newInstance(false);
    }

    public static AddTrackerWithPinCodeFragment newInstance(boolean afterRegistration) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_AFTER_REG, afterRegistration);
        AddTrackerWithPinCodeFragment fragment = new AddTrackerWithPinCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void bindTracker() {
        Observable<Boolean> observable = getModel().addTracker(getBaseActivity());
        if (observable == null)
            return;
        showProgress();
        observable.subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                hideProgress();
                if (getModel().afterReg.get()) {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                } else {
                    getBaseActivity().getSupportFragmentManager().popBackStack(TrackersFragment.TAG, 0);
                }
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
}
