package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;

import java.util.Date;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.model.MapPoint;
import no.appsonite.gpsping.viewmodel.TrackersMapHistoryFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 21.01.2016
 */
public class TrackersMapHistoryFragment extends TrackersMapBaseFragment<TrackersMapHistoryFragmentViewModel> implements CalendarDialogFragment.CalendarListener {
    private static final String TAG = TrackersMapHistoryFragment.class.getSimpleName();
    private long myId = AuthHelper.getCredentials() != null ? AuthHelper.getCredentials().getUser().id.get() : 0;

    public static TrackersMapHistoryFragment newInstance() {
        Bundle args = new Bundle();
        TrackersMapHistoryFragment fragment = new TrackersMapHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected void onViewModelCreated(TrackersMapHistoryFragmentViewModel model) {
        super.onViewModelCreated(model);
        getBinding().calendarBtn.setOnClickListener(v -> calendarBtn());
    }

    private void calendarBtn() {
        Friend currentFriend = getModel().currentFriend.get();
        if (currentFriend == null)
            return;
        Date date = getModel().historyDate.get();
        if (date == null)
            date = new Date();
        CalendarDialogFragment calendarDialogFragment = CalendarDialogFragment.newInstance(currentFriend.id.get(), date);
        calendarDialogFragment.show(getChildFragmentManager(), CalendarDialogFragment.TAG);
    }

    @Override
    public void onDateSelected(Date date) {
        getModel().historyDate.set(date);
    }

    @Override
    protected void onMapPoint(MapPoint mapPoint) {
        if (mapPoint.isBelongsToUser() && mapPoint.getUser().id.get() == myId) {
            getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(mapPoint.getLatLng(), 15));
        }
    }
}
