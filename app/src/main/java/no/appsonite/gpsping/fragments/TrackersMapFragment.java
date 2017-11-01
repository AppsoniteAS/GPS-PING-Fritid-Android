package no.appsonite.gpsping.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.Profile;
import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.model.MapPoint;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.services.LocationMapService;
import no.appsonite.gpsping.services.LocationTrackerService;
import no.appsonite.gpsping.utils.MarkerHelper;
import no.appsonite.gpsping.utils.Utils;
import no.appsonite.gpsping.viewmodel.TrackersMapFragmentViewModel;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class TrackersMapFragment extends TrackersMapBaseFragment<TrackersMapFragmentViewModel> {
    private static final String TAG = TrackersMapFragment.class.getSimpleName();
    private static final int PERMISSION_LOCATION = 1;
    private static final int PERMISSION_SMS = 2;
    private Marker userMarker;
    private MapPoint userMapPoint;
    private Location lastLocation;
    private long myId = AuthHelper.getCredentials() != null ? AuthHelper.getCredentials().getUser().id.get() : 0;
    private Handler refreshHandler = new Handler();
    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (lastLocation != null) {
                updateLine(lastLocation);
            }
            refreshHandler.postDelayed(refreshRunnable, 16);
        }
    };

    public static TrackersMapFragment newInstance() {
        Bundle args = new Bundle();
        TrackersMapFragment fragment = new TrackersMapFragment();
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
    protected void onLocationUpdate(Location location) {
        super.onLocationUpdate(location);
        if (AuthHelper.getCredentials() == null)
            return;
        lastLocation = location;
        if (getMap() != null) {
            getBinding().mapTarget.setVisibility(View.VISIBLE);
            updateLine(location);
            if (userMarker == null) {
                Profile profile = AuthHelper.getCredentials().getUser();
                Friend me = new Friend();
                me.lastName.set(profile.lastName.get());
                me.firstName.set(profile.firstName.get());
                me.username.set(profile.username.get());
                me.id.set(profile.id.get());

                userMapPoint = new MapPoint(me,
                        location.getLatitude(),
                        location.getLongitude(),
                        me.getName(),
                        null,
                        null,
                        location.getTime() / 1000l);

                userMarker = getMap().addMarker(new MarkerOptions()
                        .position(userMapPoint.getLatLng())
                        .icon((
                                MarkerHelper.getUserBitmapDescriptor(userMapPoint.getUser()))
                        ));
                updateDistance();
                getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), 15));

            } else {
                userMapPoint.setLat(location.getLatitude());
                userMapPoint.setLon(location.getLongitude());
                userMapPoint.setLogTime(location.getTime() / 1000l);
                userMarker.setPosition(userMapPoint.getLatLng());
            }
        }
    }

    @Override
    public void onMapReady() {
        super.onMapReady();
        getMap().setOnCameraChangeListener(cameraPosition -> updateDistance());
    }

    private void updateDistance() {
        if (lastLocation == null)
            return;
        float[] dis = new float[1];
        Rect visibleRect = new Rect();
        getBinding().mapTarget.getLocalVisibleRect(visibleRect);
        LatLng latLng = getMap().getProjection().fromScreenLocation(new Point(visibleRect.centerX(), visibleRect.centerY()));
        Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(), latLng.latitude, latLng.longitude, dis);
        getModel().distance.set(Utils.getDistanceText(dis[0]));
    }

    private void updateLine(Location location) {
        Point screenPosition = getMap().getProjection().toScreenLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        getBinding().mapTarget.setLineStart(screenPosition);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        refreshHandler.post(refreshRunnable);
        startLocationMapService(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        refreshHandler.removeCallbacks(refreshRunnable);
        LocationMapService.stopService(Application.getContext());
    }

    private void startLocationMapService(Context context) {
        new RxPermissions(getActivity())
                .request(ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION)
                .subscribe(granted -> onNextStartLocationMapService(granted, context));
    }

    private void onNextStartLocationMapService(boolean granted, Context context) {
        if (granted) {
            LocationMapService.startService(context);
        } else {
            showInfoDeniedPermission(context, PERMISSION_LOCATION,
                    ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(userMarker)) {
            getModel().currentMapPoint.set(userMapPoint);
            getModel().currentPoi.set(null);
            return false;
        }
        return super.onMarkerClick(marker);
    }

    @Override
    protected boolean skipMapPoint(MapPoint mapPoint) {
        return (mapPoint.isBelongsToUser() && myId == mapPoint.getUser().id.get()) || super.skipMapPoint(mapPoint);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBinding().showUserPositionBtn.setOnClickListener(v -> showUserPositionUserBtn());
        updateShowPositionButton();
        showAlertUpdateTrackers();
    }


    private void showUserPositionUserBtn() {
        boolean isRunning = LocationTrackerService.isRunning();
        if (isRunning) {
            stopService();
        } else {
            startService();
        }
    }

    private void stopService() {
        LocationTrackerService.stopService(getContext());
        getBinding().showUserPositionBtn.setImageResource(R.drawable.ic_visible_white);
    }

    private void startService() {
        LocationTrackerService.startService(getContext());
        getBinding().showUserPositionBtn.setImageResource(R.drawable.ic_invisible_white);
    }

    private void showAlertUpdateTrackers() {
        if (!Utils.isUpdateTracker()) {
            getModel().hasTrackers().subscribe(trackersAnswer -> {
                if (trackersAnswer.getTrackers() != null && !trackersAnswer.getTrackers().isEmpty()) {
                    showAlertDialog(trackersAnswer.getTrackers());
                }
            });
        }
    }

    private void showAlertDialog(final ArrayList<Tracker> trackers) {
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage(R.string.updateTracker)
                .setPositiveButton(R.string.update, (dialog, which) -> resetTrackers(trackers))
                .show();
    }

    private void resetTrackers(ArrayList<Tracker> trackers) {
        new RxPermissions(getActivity())
                .request(SEND_SMS, READ_SMS, RECEIVE_SMS)
                .subscribe(granted -> onNextResetTrackers(granted, trackers));
    }

    private void onNextResetTrackers(boolean granted, ArrayList<Tracker> trackers) {
        if (granted) {
            getModel().resetTrackers(getActivity(), trackers);
        } else {
            showInfoDeniedPermission(getContext(), PERMISSION_SMS, SEND_SMS, READ_SMS, RECEIVE_SMS);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSION_LOCATION:
                startLocationMapService(getContext());
                break;
            case PERMISSION_SMS:
                showAlertUpdateTrackers();
                break;
        }
    }

    private void updateShowPositionButton() {
        getBinding().showUserPositionBtn.setImageResource(LocationTrackerService.isRunning() ? R.drawable.ic_invisible_white : R.drawable.ic_visible_white);
    }
}
