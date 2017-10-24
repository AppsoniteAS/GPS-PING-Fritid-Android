package no.appsonite.gpsping.data_structures;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import no.appsonite.gpsping.R;

/**
 * Created by taras on 10/24/17.
 */

public class ColorMarkerHelper {

    @NonNull
    public static BitmapDescriptor getBitmapDescriptorMain(ColorData.Colors colors) {
        switch (colors) {
            case RED:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_red_big);
            case ORANGE:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_orange_big);
            case YELLOW:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_yellow_big);
            case GREEN:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_green_big);
            default:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_red_big);
        }
    }

    @NonNull
    public static BitmapDescriptor getBitmapDescriptorMini(ColorData.Colors colors) {
        switch (colors) {
            case RED:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_red_mid);
            case ORANGE:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_orange_mid);
            case YELLOW:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_yellow_mid);
            case GREEN:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_green_mid);
            default:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_red_mid);
        }
    }
}
