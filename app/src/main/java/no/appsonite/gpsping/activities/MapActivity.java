package no.appsonite.gpsping.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.fragments.BaseMapFragment;
import no.appsonite.gpsping.fragments.TrackersMapFragment;
import no.appsonite.gpsping.fragments.TrackersMapHistoryFragment;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class MapActivity extends BaseActivity implements OnMapReadyCallback {
    private static final String EXTRA_TYPE = "extra_type";
    private SupportMapFragment mapFragment;
    private boolean mapReady = false;
    private GoogleMap googleMap;

    public enum Type {
        HISTORY, ACTIVE
    }

    public static void start(Context context, Type type) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(EXTRA_TYPE, type.toString());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapReady = false;
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Type type = Type.valueOf(getIntent().getStringExtra(EXTRA_TYPE));
        if (savedInstanceState == null) {
            replaceFragment(type.equals(Type.ACTIVE) ? TrackersMapFragment.newInstance() : TrackersMapHistoryFragment.newInstance(), false);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        this.googleMap = googleMap;
        if (getLastFragment() != null) {
            ((BaseMapFragment) getLastFragment()).onMapReady();
        }
    }

    public boolean isMapReady() {
        return mapReady;
    }

    public GoogleMap getMap() {
        return googleMap;
    }
}
