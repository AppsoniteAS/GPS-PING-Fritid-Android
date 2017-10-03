package no.appsonite.gpsping.fragments;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Locale;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public abstract class BaseMapFragment<B extends ViewDataBinding, M extends BaseFragmentViewModel> extends BaseBindingFragment<B, M> implements OnMapReadyCallback {
    private boolean mapReady = false;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    public void onMapReady() {
        if ("sv".equals(Locale.getDefault().getLanguage())) {
            getMap().setMaxZoomPreference(15);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        this.googleMap = googleMap;
        onMapReady();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMapReady()) {
            onMapReady();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mapReady = false;
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    public boolean isMapReady() {
        return mapReady;
    }

    public GoogleMap getMap() {
        return googleMap;
    }
}
