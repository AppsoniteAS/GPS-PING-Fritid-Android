package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentAddTrackerBinding;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.viewmodel.AddTrackerFragmentViewModel;
import rx.Observable;
import rx.Observer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class AddTrackerFragment extends BaseBindingFragment<FragmentAddTrackerBinding, AddTrackerFragmentViewModel> {
    private static final String TAG = "AddTrackerFragment";
    private static final String ARG_TRACKER = "arg_tracker";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.addTracker);
    }

    public static AddTrackerFragment newInstance(Tracker tracker) {
        Bundle args = new Bundle();
        AddTrackerFragment fragment = new AddTrackerFragment();
        args.putSerializable(ARG_TRACKER, tracker);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onViewModelCreated(AddTrackerFragmentViewModel model) {
        super.onViewModelCreated(model);
        model.tracker.set((Tracker) getArguments().getSerializable(ARG_TRACKER));
        getBinding().addTrackerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTracker();
            }
        });
    }

    private void saveTracker() {
        Observable<Boolean> observable = getModel().saveTracker(getActivity());
        if (observable != null) {
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
                public void onNext(Boolean isNew) {
                    hideProgress();
                    getBaseActivity().getSupportFragmentManager().popBackStack(TrackersFragment.TAG, 0);
                    if (!isNew) {
                        Toast.makeText(getActivity(), getString(R.string.trackerUpdated), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
