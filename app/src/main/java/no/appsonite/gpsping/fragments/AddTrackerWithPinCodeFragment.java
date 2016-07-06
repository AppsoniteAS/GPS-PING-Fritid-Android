package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.view.View;

import no.appsonite.gpsping.R;
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
    }

    public static AddTrackerWithPinCodeFragment newInstance() {

        Bundle args = new Bundle();

        AddTrackerWithPinCodeFragment fragment = new AddTrackerWithPinCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void bindTracker() {
        Observable<Boolean> observable = getModel().addTracker();
        if (observable == null)
            return;
        showProgress();
        observable.subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                showError(e);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                hideProgress();
                getBaseActivity().getSupportFragmentManager().popBackStack(TrackersFragment.TAG, 0);
            }
        });
    }
}
