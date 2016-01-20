package no.appsonite.gpsping.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentTrackersMapBinding;
import no.appsonite.gpsping.viewmodel.TrackersMapFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class TrackersMapFragment extends BaseMapFragment<FragmentTrackersMapBinding, TrackersMapFragmentViewModel> {
    private static final String TAG = "TrackersMapFragment";
    private TileOverlay topoNorwayOverlay;
    private TileOverlay topoWorldOverlay;
    private MediaPlayer mediaPlayer;

    @Override
    public String getFragmentTag() {
        return TAG;
    }

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

    public static TrackersMapFragment newInstance() {
        Bundle args = new Bundle();
        TrackersMapFragment fragment = new TrackersMapFragment();
        fragment.setArguments(args);
        return fragment;
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
    protected void onViewModelCreated(TrackersMapFragmentViewModel model) {
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
