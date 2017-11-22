package no.appsonite.gpsping.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;

import java.util.Date;

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
    private Location lastLocation;
    private FusedLocationProviderClient client;

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

    @Override
    protected void locationUpdater() {
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(location -> lastLocation = location);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        getModel().currentMapPoint.set(markerMapPointHashMap.get(marker));
        getModel().currentPoi.set(markerPoiHashMap.get(marker));
        if (getModel().currentMapPoint.get() != null) {
            getDistanceBetweenUserAndMapPoint();
            getModel().setVisibilityCallBtn();
            getModel().setClickableEditBtn();
        }
        return false;
    }

    private void getDistanceBetweenUserAndMapPoint() {
        if (lastLocation == null) {
            return;
        }
        double latUser = lastLocation.getLatitude();
        double lonUser = lastLocation.getLongitude();
        getModel().getDistanceBetweenUserAndMapPointMain(latUser, lonUser);
    }
}
