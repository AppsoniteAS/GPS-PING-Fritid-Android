package no.appsonite.gpsping.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.graphics.Rect;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.RadioGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.FriendsAnswer;
import no.appsonite.gpsping.api.content.Poi;
import no.appsonite.gpsping.databinding.FragmentTrackersMapBinding;
import no.appsonite.gpsping.model.MapPoint;
import no.appsonite.gpsping.services.LocationMapService;
import no.appsonite.gpsping.utils.MarkerHelper;
import no.appsonite.gpsping.utils.RxBus;
import no.appsonite.gpsping.utils.Utils;
import no.appsonite.gpsping.utils.map.WMSTileProvider;
import no.appsonite.gpsping.viewmodel.TrackersMapFragmentViewModel;
import no.appsonite.gpsping.widget.Compass;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public abstract class TrackersMapBaseFragment<T extends TrackersMapFragmentViewModel> extends BaseBindingFragment<FragmentTrackersMapBinding, T>
        implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback {
    private TileOverlay topoNorwayOverlay;
    private TileOverlay topoWorldOverlay;
    private TileOverlay topoSwedenOverlay;
    private TileOverlay topoFinnishOverlay;
    private TileOverlay topoDanishOverlay;
    private MediaPlayer mediaPlayer;
    private Subscription locationSubscription;
    private Compass compass;
    private boolean firstTime = false;

    private boolean mapReady = false;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private HashMap<Marker, MapPoint> markerMapPointHashMap = new HashMap<>();
    private HashMap<Marker, Poi> markerPoiHashMap = new HashMap<>();

    @Override
    protected String getTitle() {
        return null;
    }

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
            final String URL = "http://industri.gpsping.no:6057/service?LAYERS=sweden&FORMAT=image/png&" +
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
            final String URL = "http://industri.gpsping.no:6057/service?LAYERS=finnish&FORMAT=image/png&" +
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

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mapReady = false;
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_map, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public String getFragmentTag() {
        return null;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.removeTracks) {
//            clearTracks();
//            getModel().setRemoveTracksDate(new Date());
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (trackUserLocation())
            LocationMapService.startService(context);
        locationSubscription = RxBus.getInstance().register(Location.class, new Action1<Location>() {
            @Override
            public void call(Location location) {
                onLocationUpdate(location);
            }
        });
    }

    protected void onLocationUpdate(Location location) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocationMapService.stopService(Application.getContext());
        locationSubscription.unsubscribe();
    }

    @Override
    protected void onViewModelCreated(T model) {
        super.onViewModelCreated(model);
        model.requestFriends().subscribe(new Action1<FriendsAnswer>() {
            @Override
            public void call(FriendsAnswer friendsAnswer) {

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                showError(throwable);
            }
        });
        getBinding().mapType.check(R.id.topo);

        getBinding().mapType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
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
            }
        });


//        getBinding().takePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shootSound();
//                getMap().snapshot(new GoogleMap.SnapshotReadyCallback() {
//                    @Override
//                    public void onSnapshotReady(Bitmap bitmap) {
//                        getModel().saveBitmap(bitmap);
//                    }
//                });
//            }
//        });

        getBinding().friendSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getModel().currentFriend.set(getModel().friendList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        View.OnClickListener onInfoClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getModel().currentMapPoint.set(null);
                getModel().currentPoi.set(null);
            }
        };

        getBinding().mapPointInfo.setOnClickListener(onInfoClick);

        getBinding().poiInfo.setOnClickListener(onInfoClick);

        getBinding().editPoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPoi(getModel().currentPoi.get());
            }
        });

        getBinding().deletePoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePoi();
            }
        });

        subscribeOnPoints();
        subscribeOnPois();

        initCompass();
    }

    private void initCompass() {
        compass = new Compass(getActivity());
        compass.arrowView = getBinding().compass;
        compass.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (compass != null)
            compass.start();
        if (isMapReady()) {
            onMapReady();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (compass != null)
            compass.stop();
    }

    private void deletePoi() {
        BaseBindingDialogFragment dialogFragment = RemovePoiFragmentDialog.newInstance(getModel().currentPoi.get());
        dialogFragment.show(getChildFragmentManager(), RemovePoiFragmentDialog.TAG);
        dialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getModel().requestPois();
                getModel().currentPoi.set(null);
            }
        });
    }

    private void editPoi(Poi poi) {
        BaseBindingDialogFragment dialogFragment = EditPoiDialogFragment.newInstance(poi);
        dialogFragment.show(getChildFragmentManager(), EditPoiDialogFragment.TAG);
        dialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getModel().requestPois();
                getModel().currentPoi.set(null);
            }
        });
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

                int actionBarSize = Utils.getActionBarSize(getActivity());
                Rect rect = new Rect();
                getBinding().friendSpinner.getGlobalVisibleRect(rect);
                getMap().setPadding(0, rect.bottom, 0, actionBarSize);
            }
        });

        getMap().setOnMapLongClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        this.googleMap = googleMap;
        onMapReady();
    }

    public boolean isMapReady() {
        return mapReady;
    }

    public GoogleMap getMap() {
        return googleMap;
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
                    markerMapPointHashMap.put(getMap().addMarker(new MarkerOptions()
                            .position(mapPoint.getLatLng())
                            .icon((
                                    MarkerHelper.getUserBitmapDescriptor(mapPoint.getUser()))
                            )), mapPoint);
                } else {
                    markerMapPointHashMap.put(getMap().addMarker(new MarkerOptions()
                            .position(mapPoint.getLatLng())
                            .icon((
                                    mapPoint.isLast() ? MarkerHelper.getTrackerBitmapDescriptor(mapPoint.getUser()) : MarkerHelper.getTrackerHistoryBitmapDescriptor(mapPoint.getUser())
                            ))), mapPoint);
                    try {
                        if (mapPoint.isLast() && mapPoint.getUser().id.get() == AuthHelper.getCredentials().getUser().id.get()) {
                            builder.include(mapPoint.getLatLng());
                        }
                    } catch (Exception ignore) {

                    }

                }
            }
        }
        try {
            if (firstTime) {
                firstTime = false;
                zoomToBounds(builder.build());
            }
        } catch (Exception ignore) {

        }

    }

    public void zoomToBounds(LatLngBounds bounds) {
        // Calculate distance between northeast and southwest
        float[] results = new float[1];
        android.location.Location.distanceBetween(bounds.northeast.latitude, bounds.northeast.longitude,
                bounds.southwest.latitude, bounds.southwest.longitude, results);

        CameraUpdate cu = null;
        if (results[0] < 1000) { // distance is less than 1 km -> set to zoom level 15
            cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 15);
        } else {
            int padding = 50; // offset from edges of the map in pixels
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        }
        getMap().moveCamera(cu);
    }

    protected void onMapPoint(MapPoint mapPoint) {

    }

    private void clearTracks() {
        for (Marker marker : markerMapPointHashMap.keySet()) {
            marker.remove();
        }
        markerMapPointHashMap.clear();
    }

    protected boolean skipMapPoint(MapPoint mapPoint) {
        if (mapPoint.getLat() == 0 && mapPoint.getLon() == 0)
            return true;
        return false;
    }


    protected boolean trackUserLocation() {
        return false;
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

    private void shootSound() {
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        if (volume != 0) {
            if (mediaPlayer == null)
                mediaPlayer = MediaPlayer.create(getContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (mediaPlayer != null)
                mediaPlayer.start();
        }
    }

//    @Override
//    protected void initToolbar() {
//        super.initToolbar();
//        ActionBar actionBar = getActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
//        }
//    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        getModel().currentMapPoint.set(markerMapPointHashMap.get(marker));
        getModel().currentPoi.set(markerPoiHashMap.get(marker));
        return false;
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

    @Override
    public void onMapLongClick(LatLng latLng) {
        Poi poi = new Poi();
        poi.setLat(latLng.latitude);
        poi.setLon(latLng.longitude);
        editPoi(poi);
    }
}
