package no.appsonite.gpsping.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.BuildConfig;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.data_structures.ArrowLocationPin;
import no.appsonite.gpsping.data_structures.ColorArrowPin;
import no.appsonite.gpsping.enums.DirectionPin;
import no.appsonite.gpsping.utils.image_transdormation.CircleSizeTransformation;
import no.appsonite.gpsping.utils.image_transdormation.MaxSizeTextureTransformation;
import rx.Observable;
import rx.exceptions.Exceptions;

/**
 * Created by taras on 10/18/17.
 */

public class PinUtils {
    public static Observable<Bitmap> getPinDog(String url, ColorArrowPin.Colors colors, DirectionPin direction) {
        Context context = Application.getContext();
        return Observable.just(BuildConfig.AMAZON_ADDRESS + url)
                .map(s -> {
                    try {
                        return Picasso.with(context)
                                .load(s)
                                .transform(new MaxSizeTextureTransformation(context))
                                .transform(new CircleSizeTransformation(44))
                                .get();
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .map(bitmap -> PinUtils.generatePinWithPhoto(context, bitmap, colors, direction));
    }

    private static Bitmap generatePinWithPhoto(Context context, Bitmap photo, ColorArrowPin.Colors colors, DirectionPin direction) {
        ArrowLocationPin arrowLocationPin = new ArrowLocationPin(direction);
        float leftForArrow = arrowLocationPin.getLeft();
        float topForArrow = arrowLocationPin.getTop();
        float rotate = arrowLocationPin.getRotate();
        Bitmap pin = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_white_round_for_pin);
        Bitmap result = Bitmap.createBitmap(pin.getWidth(), pin.getHeight(), pin.getConfig());
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(pin, 0, 0, paint);
        canvas.drawBitmap(photo, 8, 8, paint);
        Bitmap arrow = getArrowBitmap(context, rotate, colors);
        canvas.drawBitmap(arrow, leftForArrow, topForArrow, paint);
        pin.recycle();
        return result;
    }

    private static Bitmap getArrowBitmap(Context context, float rotate, ColorArrowPin.Colors colors) {
        Bitmap bitmap;
        Resources resources = context.getResources();
        switch (colors) {
            case RED:
                bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_arrow_red_small);
                break;
            case ORANGE:
                bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_arrow_orange_small);
                break;
            case YELLOW:
                bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_arrow_yellow_small);
                break;
            default:
                bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_arrow_green_small);

        }
        if (rotate == 0) {
            return bitmap;
        } else {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

    }
}
