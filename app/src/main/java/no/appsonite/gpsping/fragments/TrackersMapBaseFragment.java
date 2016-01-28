package no.appsonite.gpsping.fragments;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentTrackersMapBinding;
import no.appsonite.gpsping.model.MapPoint;
import no.appsonite.gpsping.utils.MarkerHelper;
import no.appsonite.gpsping.viewmodel.TrackersMapFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public abstract class TrackersMapBaseFragment<T extends TrackersMapFragmentViewModel> extends BaseMapFragment<FragmentTrackersMapBinding, T> {
    private TileOverlay topoNorwayOverlay;
    private TileOverlay topoWorldOverlay;
    private MediaPlayer mediaPlayer;

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
        getMap().setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void showSatellite() {
        clearTile();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.removeTracks) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onViewModelCreated(T model) {
        super.onViewModelCreated(model);
        model.requestFriends();

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

        subscribeOnPoints();
    }

    @Override
    public void onMapReady() {
        super.onMapReady();
        getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                getModel().currentMapPoint.set(markerMapPointHashMap.get(marker));
                return false;
            }
        });

        TypedValue typedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
        int actionBarSize = TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
        getMap().setPadding(0, actionBarSize * 2, 0, actionBarSize);
    }

    private HashMap<Marker, MapPoint> markerMapPointHashMap = new HashMap<>();

    private void updatePoints() {
        ObservableArrayList<MapPoint> mapPoints = getModel().mapPoints;
        getMap().clear();
        markerMapPointHashMap.clear();
        if (mapPoints.size() > 0) {
            for (MapPoint mapPoint : mapPoints) {
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
            getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(mapPoints.get(mapPoints.size() - 1).getLatLng(), 15));
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
}
