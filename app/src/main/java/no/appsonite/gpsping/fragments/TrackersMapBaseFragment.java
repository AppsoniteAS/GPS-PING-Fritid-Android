package no.appsonite.gpsping.fragments;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.content.Poi;
import no.appsonite.gpsping.databinding.FragmentTrackersMapBinding;
import no.appsonite.gpsping.model.MapPoint;
import no.appsonite.gpsping.services.LocationMapService;
import no.appsonite.gpsping.utils.MarkerHelper;
import no.appsonite.gpsping.utils.RxBus;
import no.appsonite.gpsping.utils.Utils;
import no.appsonite.gpsping.viewmodel.TrackersMapFragmentViewModel;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public abstract class TrackersMapBaseFragment<T extends TrackersMapFragmentViewModel> extends BaseMapFragment<FragmentTrackersMapBinding, T> implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {
    private TileOverlay topoNorwayOverlay;
    private TileOverlay topoWorldOverlay;
    private MediaPlayer mediaPlayer;
    private Subscription locationSubscription;

    @Override
    protected String getTitle() {
        return getString(R.string.map);
    }

    private void clearTile() {
        if (topoNorwayOverlay != null) {
            topoNorwayOverlay.remove();
        }
        if (topoWorldOverlay != null) {
            topoWorldOverlay.remove();
        }
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
        norwayOptions.zIndex(6);
        topoNorwayOverlay = map.addTileOverlay(norwayOptions);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public String getFragmentTag() {
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.removeTracks) {
            clearTracks();
            getModel().setRemoveTracksDate(new Date());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
        model.requestFriends();
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


        getBinding().takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootSound();
                getMap().snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        getModel().saveBitmap(bitmap);
                    }
                });
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
    }

    private void deletePoi() {
        RemovePoiFragmentDialog.newInstance(getModel().currentPoi.get()).show(getChildFragmentManager(), RemovePoiFragmentDialog.TAG);
    }

    private void editPoi(Poi poi) {
        EditPoiDialogFragment.newInstance(poi).show(getChildFragmentManager(), EditPoiDialogFragment.TAG);
    }

    @Override
    public void onMapReady() {
        super.onMapReady();
        getMap().setTrafficEnabled(false);
        getMap().getUiSettings().setMapToolbarEnabled(false);
        showTopo();
        getMap().setOnMarkerClickListener(this);
        int actionBarSize = Utils.getActionBarSize(getActivity());
        getMap().setPadding(0, actionBarSize * 2, 0, actionBarSize);

        getMap().setOnMapLongClickListener(this);
        requestPois();
    }

    private HashMap<Marker, MapPoint> markerMapPointHashMap = new HashMap<>();
    private HashMap<Marker, Poi> markerPoiHashMap = new HashMap<>();

    private void updatePoints() {
        ObservableArrayList<MapPoint> mapPoints = getModel().mapPoints;
        clearTracks();
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
                }
            }
//            getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(mapPoints.get(mapPoints.size() - 1).getLatLng(), 15));
        }
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

    @Override
    protected void initToolbar() {
        super.initToolbar();
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        getModel().currentMapPoint.set(markerMapPointHashMap.get(marker));
        getModel().currentPoi.set(markerPoiHashMap.get(marker));
        return false;
    }

    private void requestPois() {
        getModel().requestPois().subscribe(new Observer<ArrayList<Poi>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ArrayList<Poi> pois) {
                addPoisForMap(pois);
            }
        });
    }

    private void addPoisForMap(ArrayList<Poi> pois) {
        for (Marker marker : markerPoiHashMap.keySet()) {
            marker.remove();
        }
        markerPoiHashMap.clear();
        for (Poi poi : pois) {
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
        poi.setLon(latLng.latitude);
        poi.setLon(latLng.longitude);
        editPoi(poi);
    }
}
