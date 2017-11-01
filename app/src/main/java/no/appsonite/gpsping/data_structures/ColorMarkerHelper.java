package no.appsonite.gpsping.data_structures;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.enums.ColorPin;
import no.appsonite.gpsping.enums.DirectionPin;
import no.appsonite.gpsping.enums.SizeArrowPin;

/**
 * Created by taras on 10/24/17.
 */

public class ColorMarkerHelper {
    private Resources resources;

    public ColorMarkerHelper() {
        resources = Application.getContext().getResources();
    }

    @NonNull
    public BitmapDescriptor getArrowPin(ColorPin colorPin, DirectionPin direction, SizeArrowPin sizeArrowPin) {
        Bitmap bitmap;
        int resourceId;
        boolean isBigArrow = sizeArrowPin == SizeArrowPin.BIG;
        switch (colorPin) {
            case RED:
                resourceId = isBigArrow ? R.drawable.ic_arrow_red_big : R.drawable.ic_arrow_red_mid;
                bitmap = BitmapFactory.decodeResource(resources, resourceId);
                break;
            case ORANGE:
                resourceId = isBigArrow ? R.drawable.ic_arrow_orange_big : R.drawable.ic_arrow_orange_mid;
                bitmap = BitmapFactory.decodeResource(resources, resourceId);
                break;
            case YELLOW:
                resourceId = isBigArrow ? R.drawable.ic_arrow_yellow_big : R.drawable.ic_arrow_yellow_mid;
                bitmap = BitmapFactory.decodeResource(resources, resourceId);
                break;
            //case GREEN:
            default:
                resourceId = isBigArrow ? R.drawable.ic_arrow_green_big : R.drawable.ic_arrow_green_mid;
                bitmap = BitmapFactory.decodeResource(resources, resourceId);
                break;
        }

        float rotate = new ArrowLocationPin(direction).getRotate();
        if (rotate == 0) {
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        } else {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            return BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
        }
    }
}
