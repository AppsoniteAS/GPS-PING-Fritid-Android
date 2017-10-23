package no.appsonite.gpsping.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private static final String TAG = "TrackersMapHistoryFragment";
    private Location lastLocation;
    private long myId = AuthHelper.getCredentials().getUser().id.get();

    public static TrackersMapHistoryFragment newInstance() {
        Bundle args = new Bundle();
        TrackersMapHistoryFragment fragment = new TrackersMapHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_map_history, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_calendar) {
            Friend currentFriend = getModel().currentFriend.get();
            if (currentFriend == null)
                return true;
            Date date = getModel().historyDate.get();
            if (date == null)
                date = new Date();
            CalendarDialogFragment calendarDialogFragment = CalendarDialogFragment.newInstance(currentFriend.id.get(), date);
            calendarDialogFragment.show(getChildFragmentManager(), CalendarDialogFragment.TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.history);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getBinding().showUserPosition.setVisibility(View.INVISIBLE);
    }
}
