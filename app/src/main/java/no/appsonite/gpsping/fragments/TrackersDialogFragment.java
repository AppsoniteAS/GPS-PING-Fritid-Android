package no.appsonite.gpsping.fragments;

import android.app.Activity;
import android.databinding.ViewDataBinding;
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.DialogFragmentTrackersBinding;
import no.appsonite.gpsping.databinding.ItemTrackerSelectBinding;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.BindingHelper;
import no.appsonite.gpsping.utils.Utils;
import no.appsonite.gpsping.viewmodel.TrackersDialogFragmentViewModel;
import no.appsonite.gpsping.widget.GPSPingBaseRecyclerSwipeAdapter;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public class TrackersDialogFragment extends BaseBindingDialogFragment<DialogFragmentTrackersBinding, TrackersDialogFragmentViewModel> {
    private static final String TAG = "TrackersDialogFragment";

    public interface TrackersDialogListener {
        void onSelect(Tracker tracker);
    }

    private TrackersDialogListener listener;

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.selectTracker);
    }

    @Override
    protected void onViewModelCreated(final TrackersDialogFragmentViewModel model) {
        super.onViewModelCreated(model);
        model.requestTrackers();
        GPSPingBaseRecyclerSwipeAdapter<Tracker> adapter = new GPSPingBaseRecyclerSwipeAdapter<Tracker>() {
            @Override
            public ViewDataBinding onCreateViewDataBinding(ViewGroup parent, int viewType) {
                return ItemTrackerSelectBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            }

            @Override
            public void onClick(View v) {
                Tracker tracker = (Tracker) v.getTag();
                if (listener != null) {
                    listener.onSelect(tracker);
                }
                dismiss();
            }
        };
        adapter.setItems(model.trackers);
        getBinding().trackersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().trackersRecycler.setAdapter(adapter);
        BindingHelper.bindAdapter(adapter, model.trackers);
    }

    @Override
    public void onStart() {
        super.onStart();
        Point screenSize = Utils.getDisplaySize(getContext());
        int width = Math.min(screenSize.x, getResources().getDimensionPixelSize(R.dimen.trackerDialogWidth));
        int height = Math.min(screenSize.y, getResources().getDimensionPixelSize(R.dimen.trackerDialogHeight));
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof TrackersDialogListener) {
            listener = (TrackersDialogListener) activity;
        } else if (getParentFragment() instanceof TrackersDialogListener) {
            listener = (TrackersDialogListener) getParentFragment();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
