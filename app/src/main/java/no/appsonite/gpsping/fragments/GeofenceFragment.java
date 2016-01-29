package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.view.View;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentGeofenceBinding;
import no.appsonite.gpsping.model.SMS;
import no.appsonite.gpsping.viewmodel.GeofenceFragmentViewModel;
import rx.Observable;
import rx.Observer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class GeofenceFragment extends BaseBindingFragment<FragmentGeofenceBinding, GeofenceFragmentViewModel> {
    private static final String TAG = "GeofenceFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    public static GeofenceFragment newInstance() {
        Bundle args = new Bundle();
        GeofenceFragment fragment = new GeofenceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.geofence);
    }

    @Override
    protected void onViewModelCreated(GeofenceFragmentViewModel model) {
        super.onViewModelCreated(model);
        getBinding().startGeoFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchGeofence();
            }
        });
    }

    private void switchGeofence() {
        if (getModel().activeTracker.get() == null)
            return;
        Observable<SMS> observable = getModel().switchGeofence(getActivity());
        if (observable != null) {
            showProgress();
            observable.subscribe(new Observer<SMS>() {
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
    }
}
