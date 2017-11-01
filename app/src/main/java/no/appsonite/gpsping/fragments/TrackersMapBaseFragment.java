package no.appsonite.gpsping.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.Poi;
import no.appsonite.gpsping.api.content.TrackersAnswer;
import no.appsonite.gpsping.data_structures.ColorMarkerHelper;
import no.appsonite.gpsping.databinding.FragmentTrackersMapBinding;
import no.appsonite.gpsping.enums.ColorPin;
import no.appsonite.gpsping.enums.DirectionPin;
import no.appsonite.gpsping.enums.SizeArrowPin;
import no.appsonite.gpsping.model.MapPoint;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.CalculateDirection;
import no.appsonite.gpsping.utils.MarkerHelper;
import no.appsonite.gpsping.utils.PinUtils;
import no.appsonite.gpsping.utils.RxBus;
import no.appsonite.gpsping.utils.map.WMSTileProvider;
import no.appsonite.gpsping.viewmodel.TrackersMapFragmentViewModel;
import no.appsonite.gpsping.widget.Compass;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.Manifest.permission.CALL_PHONE;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public abstract class TrackersMapBaseFragment<T extends TrackersMapFragmentViewModel> extends BaseBindingFragment<FragmentTrackersMapBinding, T>
        implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback {
    private static final String INSTANCE_STATE = "mapViewSaveState";
    private static final int PERMISSION_PHONE = 1;
    private TileOverlay topoNorwayOverlay;
    private TileOverlay topoWorldOverlay;
    private TileOverlay topoSwedenOverlay;
    private TileOverlay topoFinnishOverlay;
    private TileOverlay topoDanishOverlay;
    private Subscription locationSubscription;
    private Compass compass;
    private boolean firstTime = false;
    private GoogleMap googleMap;
    private MapView mapView;
    private HashMap<Marker, MapPoint> markerMapPointHashMap = new HashMap<>();
    private HashMap<Marker, Poi> markerPoiHashMap = new HashMap<>();
    private List<Tracker> trackers = new ArrayList<>();
    private ColorMarkerHelper markerHelper = new ColorMarkerHelper();
    private CalculateDirection calculateDirection = new CalculateDirection();

    private void clearTile() {
        if (topoNorwayOverlay != null) {
            topoNorwayOverlay.remove();
        }
        if (topoWorldOverlay != null) {
            topoWorldOverlay.remove();
        }
        if (topoSwedenOverlay != null) {
            topoSwedenOverlay.remove();
        }
        if (topoFinnishOverlay != null) {
            topoFinnishOverlay.remove();
        }
        if (topoDanishOverlay != null) {
            topoDanishOverlay.remove();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firstTime = true;
        final Bundle mapViewSavedInstanceState = savedInstanceState != null ? savedInstanceState.getBundle(INSTANCE_STATE) : null;
        mapView = getBinding().map;
        mapView.onCreate(mapViewSavedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        final Bundle mapViewSaveState = new Bundle(outState);
        mapView.onSaveInstanceState(mapViewSaveState);
        outState.putBundle(INSTANCE_STATE, mapViewSaveState);
        super.onSaveInstanceState(outState);
    }

    private void showTopo() {
        clearTile();
        GoogleMap map = getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NONE);
        TileOverlayOptions worldOverlay = new TileOverlayOptions();
        worldOverlay.tileProvider(new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                try {
                    return new URL(String.format("http://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/%s/%s/%s", zoom, y, x));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
        worldOverlay.zIndex(5);
        topoWorldOverlay = map.addTileOverlay(worldOverlay);

        TileOverlayOptions norwayOptions = new TileOverlayOptions();
        norwayOptions.tileProvider(new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                try {
                    return new URL(String.format("http://opencache.statkart.no/gatekeeper/gk/gk.open_gmaps?layers=topo2&zoom=%d&x=%d&y=%d&format=image/png", zoom, x, y));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
        norwayOptions.zIndex(9);
        topoNorwayOverlay = map.addTileOverlay(norwayOptions);

        TileOverlayOptions swedenOptions = new TileOverlayOptions();
        swedenOptions.tileProvider(new WMSTileProvider(256, 256) {
            final String URL = "http://fritid.gpsping.no:6057/service?LAYERS=sweden&FORMAT=image/png&" +
                    "SRS=EPSG:3857&EXCEPTIONS=application.vnd.ogc.se_inimage&" +
                    "TRANSPARENT=TRUE&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&" +
                    "BBOX=%f,%f,%f,%f&" +
                    "WIDTH=256&HEIGHT=256";

            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                double[] bbox = getBoundingBox(x, y, zoom);
                String s = String.format(Locale.US, URL, bbox[MINX],
                        bbox[MINY], bbox[MAXX], bbox[MAXY]);
                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        });
        swedenOptions.zIndex(8);
        topoSwedenOverlay = map.addTileOverlay(swedenOptions);

        TileOverlayOptions finnishOptions = new TileOverlayOptions();
        finnishOptions.tileProvider(new WMSTileProvider(256, 256) {
            final String URL = "http://fritid.gpsping.no:6057/service?LAYERS=finnish&FORMAT=image/png&" +
                    "SRS=EPSG:3857&EXCEPTIONS=application.vnd.ogc.se_inimage&" +
                    "TRANSPARENT=TRUE&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&" +
                    "BBOX=%f,%f,%f,%f&" +
                    "WIDTH=256&HEIGHT=256";

            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                double[] bbox = getBoundingBox(x, y, zoom);
                String s = String.format(Locale.US, URL, bbox[MINX],
                        bbox[MINY], bbox[MAXX], bbox[MAXY]);
                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        });
        finnishOptions.zIndex(6);
//        topoFinnishOverlay = map.addTileOverlay(finnishOptions);

        TileOverlayOptions danishOptions = new TileOverlayOptions();
        danishOptions.tileProvider(new WMSTileProvider(256, 256) {
//            final String URL = "http://services.kortforsyningen.dk/service?servicename=topo_skaermkort&service=WMS&version=1.1.1&request=GetMap" +
//                    "&LAYERS=dtk_skaermkort_774&srs=EPSG:3857&" +
//                    "bbox=%f,%f,%f,%f&" +
//                    "width=256&height=256&format=image/png&" +
//                    "styles=default&bgcolor=0xff0000&transparent=TRUE&" +
//                    "ignoreillegallayers=TRUE&exceptions=application/vnd.ogc.se_inimage&" +
//                    "login=kms1&password=adgang";

//            final String URL = "http://services.kortforsyningen.dk/service?servicename=topo25&service=WMS&version=1.1.1&request=GetMap" +
//                    "&LAYERS=topo25_graa&srs=EPSG:3857&" +
//                    "bbox=%f,%f,%f,%f&" +
//                    "width=256&height=256&format=image/png&" +
//                    "styles=default&bgcolor=0xffffff&transparent=TRUE&" +
//                    "ignoreillegallayers=TRUE&exceptions=application/vnd.ogc.se_inimage&" +
//                    "login=kms1&password=adgang";

            final String URL = "http://kortforsyningen.kms.dk/topo100?LAYERS=dtk_1cm&FORMAT=image/png&" +
                    "BGCOLOR=0xFFFFFF&TICKET=8b4e36fe4c851004fd1e69463fbabe3b&PROJECTION=EPSG:3857&" +
                    "TRANSPARENT=TRUE&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&" +
                    "STYLES=&SRS=EPSG:3857&" +
                    "BBOX=%f,%f,%f,%f&" +
                    "WIDTH=256&HEIGHT=256";

            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                double[] bbox = getBoundingBox(x, y, zoom);
                String s = String.format(Locale.US, URL, bbox[MINX],
                        bbox[MINY], bbox[MAXX], bbox[MAXY]);
                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        });
        danishOptions.zIndex(7);
//        topoDanishOverlay = map.addTileOverlay(danishOptions);
    }

    private void showStandard() {
        clearTile();
        if (getMap() != null)
            getMap().setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void showSatellite() {
        clearTile();
        if (getMap() != null)
            getMap().setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        locationSubscription = RxBus.getInstance().register(Location.class, this::onLocationUpdate);
    }

    protected void onLocationUpdate(Location location) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        locationSubscription.unsubscribe();
    }

    @Override
    protected void onViewModelCreated(T model) {
        super.onViewModelCreated(model);
        model.requestFriends();
        getBinding().mapType.check(R.id.topo);

        getBinding().mapType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.satellite:
                    showSatellite();
                    break;
                case R.id.standard:
                    showStandard();
                    break;
                case R.id.topo:
                    showTopo();
                    break;
            }
        });

        getBinding().friendSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getModel().currentFriend.set(getModel().friendList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        View.OnClickListener onInfoClick = view -> {
            getModel().currentMapPoint.set(null);
            getModel().currentPoi.set(null);
        };

        getBinding().poiInfo.setOnClickListener(onInfoClick);

        getBinding().editPoi.setOnClickListener(view -> editPoi(getModel().currentPoi.get()));

        getBinding().deletePoi.setOnClickListener(view -> deletePoi());

        getBinding().callBtn.setOnClickListener(view -> callBtn());

        getBinding().mapBtn.setOnClickListener(onInfoClick);

        getBinding().editBtn.setOnClickListener(view -> editBtn());

        subscribeOnPoints();
        subscribeOnPois();
        initCompass();
    }

    private void callBtn() {
        boolean check = getModel().validateCallForS1Tracker(getModel().currentMapPoint.get().getImeiNumber());
        if (check) {
            checkPhonePermission();
        }
    }

    private void checkPhonePermission() {
        new RxPermissions(getActivity())
                .request(CALL_PHONE)
                .subscribe(this::checkPhonePermissionOnNext);
    }

    private void checkPhonePermissionOnNext(boolean granted) {
        if (granted) {
            callToS1Tracker();
        } else {
            showInfoDeniedPermission(getContext(), PERMISSION_PHONE, CALL_PHONE);
        }
    }

    private void callToS1Tracker() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + getModel().currentMapPoint.get().getTrackerNumber()));
        startActivity(intent);
    }

    private void editBtn() {
        if (getModel().currentMapPoint.get().getImeiNumber().isEmpty()) {
            return;
        }
        if (!trackers.isEmpty()) {
            getTrackerFromCache();
        } else {
            getTrackerFromNetwork();
        }
    }

    private void getTrackerFromCache() {
        for (Tracker tracker : trackers) {
            if (tracker.imeiNumber.get().equals(getModel().currentMapPoint.get().getImeiNumber())) {
                getBaseActivity().replaceFragment(EditTrackerFragment.newInstance(tracker), true);
            }
        }
    }

    private void getTrackerFromNetwork() {
        showProgress();
        getModel().getTrackers()
                .subscribe(this::editBtnOnNext, this::showError, this::hideProgress);
    }

    private void editBtnOnNext(TrackersAnswer trackersAnswer) {
        if (trackersAnswer.getTrackers() != null || !trackersAnswer.getTrackers().isEmpty()) {
            trackers = trackersAnswer.getTrackers();
            for (Tracker tracker : trackersAnswer.getTrackers()) {
                showEditTrackerScreenIfBelonging(tracker);
            }
        }
    }

    private void showEditTrackerScreenIfBelonging(Tracker tracker) {
        if (tracker.imeiNumber.get().equals(getModel().currentMapPoint.get().getImeiNumber())) {
            getBaseActivity().replaceFragment(EditTrackerFragment.newInstance(tracker), true);
        }
    }

    private void initCompass() {
        compass = new Compass(getActivity());
        compass.arrowView = getBinding().compass;
        compass.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (compass != null)
            compass.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (compass != null)
            compass.stop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null)
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        onMapReady();
    }

    public GoogleMap getMap() {
        return googleMap;
    }

    public void onMapReady() {
        if ("sv".equals(Locale.getDefault().getLanguage())) {
            getMap().setMaxZoomPreference(15);
        }
        getMap().setTrafficEnabled(false);
        getMap().getUiSettings().setMapToolbarEnabled(false);
        getMap().getUiSettings().setCompassEnabled(true);

        showTopo();
        getMap().setOnMarkerClickListener(this);

        getBinding().friendSpinner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getBinding().friendSpinner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    getBinding().friendSpinner.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                Rect rect = new Rect();
                getBinding().friendSpinner.getGlobalVisibleRect(rect);
            }
        });

        getMap().setOnMapLongClickListener(this);
    }

    private void deletePoi() {
        BaseBindingDialogFragment dialogFragment = RemovePoiFragmentDialog.newInstance(getModel().currentPoi.get());
        dialogFragment.show(getChildFragmentManager(), RemovePoiFragmentDialog.TAG);
        dialogFragment.setOnDismissListener(dialogInterface -> {
            getModel().requestPois();
            getModel().currentPoi.set(null);
        });
    }

    protected void onMapPoint(MapPoint mapPoint) {

    }

    protected boolean skipMapPoint(MapPoint mapPoint) {
        return mapPoint.getLat() == 0 && mapPoint.getLon() == 0;
    }

    private void subscribeOnPois() {
        getModel().pois.addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {

            }

            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
                updatePois();
            }

            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                updatePois();
            }

            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {

            }

            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
                updatePois();
            }
        });
    }

    private void updatePois() {
        for (Marker marker : markerPoiHashMap.keySet()) {
            marker.remove();
        }
        markerPoiHashMap.clear();
        for (Poi poi : getModel().pois) {
            markerPoiHashMap.put(
                    getMap().addMarker(new MarkerOptions()
                            .position(poi.getLatLng())
                            .icon((
                                    MarkerHelper.getPoiBitmapDescriptor(poi.getUser()))
                            ))
                    , poi);
        }
    }

    private void subscribeOnPoints() {
        getModel().mapPoints.addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {

            }

            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
                updatePoints();
            }

            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                updatePoints();
            }

            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
            }

            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
                updatePoints();
            }
        });
    }

    private void updatePoints() {
        ObservableArrayList<MapPoint> mapPoints = getModel().mapPoints;
        clearTracks();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (mapPoints.size() > 0) {
            for (MapPoint mapPoint : mapPoints) {
                if (skipMapPoint(mapPoint)) {
                    continue;
                }
                onMapPoint(mapPoint);
                if (mapPoint.isBelongsToUser()) {
                    setUserMarker(mapPoint);
                } else {
                    setDogMarker(mapPoint);
                    includeThisPointForBuildingOfTheBounds(mapPoint, builder);
                }
            }
        }
        firstZoomToBound(builder);
    }

    private void clearTracks() {
        for (Marker marker : markerMapPointHashMap.keySet()) {
            marker.remove();
        }
        markerMapPointHashMap.clear();
    }

    private void setUserMarker(MapPoint mapPoint) {
        markerMapPointHashMap.put(getMap().addMarker(new MarkerOptions()
                .position(mapPoint.getLatLng())
                .icon((
                        MarkerHelper.getUserBitmapDescriptor(mapPoint.getUser()))
                )), mapPoint);
    }

    private void setDogMarker(MapPoint mapPoint) {
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_white_round_for_pes))
                .position(mapPoint.getLatLng());

        Marker marker = getMap().addMarker(markerOptions);
        marker.setTag("marker");

        setPhotoForDogMarker(marker, mapPoint);
        markerMapPointHashMap.put(marker, mapPoint);
    }

    private void setPhotoForDogMarker(Marker marker, MapPoint mapPoint) {
        String url = mapPoint.getPicUrl();
        ColorPin colorPin = getModel().colorArrowPin.get(mapPoint.getImeiNumber());
        if (url == null || url.isEmpty()) {
            setArrowPin(colorPin, mapPoint, marker);
        } else {
            setAvatarPin(colorPin, mapPoint, marker);
        }
    }

    private void setArrowPin(ColorPin colorPin, MapPoint mapPoint, Marker marker) {
        if (mapPoint.isMainAvatar()) {
            marker.setIcon(markerHelper.getArrowPin(colorPin, getDirection(mapPoint.getDirection()), SizeArrowPin.BIG));
        } else {
            marker.setIcon(markerHelper.getArrowPin(colorPin, getDirection(mapPoint.getDirection()), SizeArrowPin.MID));
        }
    }

    private DirectionPin getDirection(int direction) {
        return calculateDirection.getDirection(direction);
    }

    private void setAvatarPin(ColorPin colorPin, MapPoint mapPoint, Marker marker) {
        PinUtils.getPinDog(mapPoint.getPicUrl(), colorPin, getDirection(mapPoint.getDirection()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                            if (marker.getTag() != null) {
                                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                                marker.setIcon(bitmapDescriptor);
                            }
                        },
                        Throwable::printStackTrace);
    }

    private void includeThisPointForBuildingOfTheBounds(MapPoint mapPoint, LatLngBounds.Builder builder) {
        try {
            if (mapPoint.isLast() && mapPoint.getUser().id.get() == AuthHelper.getCredentials().getUser().id.get()) {
                builder.include(mapPoint.getLatLng());
            }
        } catch (Exception ignore) {

        }
    }

    private void firstZoomToBound(LatLngBounds.Builder builder) {
        try {
            if (firstTime) {
                firstTime = false;
                zoomToBounds(builder.build());
            }
        } catch (Exception ignore) {

        }
    }

    private void zoomToBounds(LatLngBounds bounds) {
        // Calculate distance between northeast and southwest
        float[] results = new float[1];
        android.location.Location.distanceBetween(bounds.northeast.latitude, bounds.northeast.longitude,
                bounds.southwest.latitude, bounds.southwest.longitude, results);

        CameraUpdate cu;
        if (results[0] < 1000) { // distance is less than 1 km -> set to zoom level 15
            cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 15);
        } else {
            int padding = 50; // offset from edges of the map in pixels
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        }
        getMap().moveCamera(cu);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        getModel().currentMapPoint.set(markerMapPointHashMap.get(marker));
        getModel().currentPoi.set(markerPoiHashMap.get(marker));
        setClickableEditBtn();
        return false;
    }

    private void setClickableEditBtn() {
        if (getModel().currentMapPoint.get().getImeiNumber().isEmpty()) {
            getModel().clickableEditBtn.set(false);
        } else {
            getModel().clickableEditBtn.set(true);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Poi poi = new Poi();
        poi.setLat(latLng.latitude);
        poi.setLon(latLng.longitude);
        editPoi(poi);
    }

    private void editPoi(Poi poi) {
        BaseBindingDialogFragment dialogFragment = EditPoiDialogFragment.newInstance(poi);
        dialogFragment.show(getChildFragmentManager(), EditPoiDialogFragment.TAG);
        dialogFragment.setOnDismissListener(dialogInterface -> {
            getModel().requestPois();
            getModel().currentPoi.set(null);
        });
    }
}
