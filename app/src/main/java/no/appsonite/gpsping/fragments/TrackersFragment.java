package no.appsonite.gpsping.fragments;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.databinding.FragmentTrackersBinding;
import no.appsonite.gpsping.databinding.ItemTrackerBinding;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.BindingHelper;
import no.appsonite.gpsping.viewmodel.TrackersFragmentViewModel;
import no.appsonite.gpsping.widget.GPSPingBaseRecyclerSwipeAdapter;
import rx.Observable;
import rx.Observer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class TrackersFragment extends BaseBindingFragment<FragmentTrackersBinding, TrackersFragmentViewModel> {
    public static final String TAG = TrackersFragment.class.getSimpleName();

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addTracker) {
            getBaseActivity().replaceFragment(AddTrackerWithPinCodeFragment.newInstance(), true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trackers, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static TrackersFragment newInstance() {
        Bundle args = new Bundle();
        TrackersFragment fragment = new TrackersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onViewModelCreated(final TrackersFragmentViewModel model) {
        super.onViewModelCreated(model);
        model.requestTrackers();
        GPSPingBaseRecyclerSwipeAdapter<Tracker> adapter = new GPSPingBaseRecyclerSwipeAdapter<Tracker>() {
            @Override
            public ViewDataBinding onCreateViewDataBinding(ViewGroup parent, int viewType) {
                return ItemTrackerBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.enableTracker:
                        getModel().enableTracker((Tracker) v.getTag());
                        break;
                    case R.id.removeTracker:
                        removeTracker((Tracker) v.getTag());
                        break;
                    case R.id.editTracker:
                        getBaseActivity().replaceFragment(AddTrackerFragment.newInstance((Tracker) v.getTag()), true);
                        break;
                }
            }
        };
        adapter.setItems(model.trackers);
        getBinding().trackersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().trackersRecycler.setAdapter(adapter);
        BindingHelper.bindAdapter(adapter, model.trackers);
    }

    private void removeTracker(Tracker tracker) {
        Observable<ApiAnswer> observable = getModel().removeTracker(tracker);
        showProgress();
        observable.subscribe(new Observer<ApiAnswer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                showError(e);
            }

            @Override
            public void onNext(ApiAnswer o) {
                hideProgress();
            }
        });
    }
}
