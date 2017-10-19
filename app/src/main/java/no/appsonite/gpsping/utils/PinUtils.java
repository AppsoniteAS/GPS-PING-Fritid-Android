package no.appsonite.gpsping.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import no.appsonite.gpsping.BuildConfig;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.utils.image_transdormation.CircleSizeTransformation;
import no.appsonite.gpsping.utils.image_transdormation.MaxSizeTextureTransformation;
import rx.Observable;
import rx.exceptions.Exceptions;

/**
 * Created by taras on 10/18/17.
 */

public class PinUtils {
    public static Observable<Bitmap> getPinDog(Context context, String url) {
        return Observable.just(BuildConfig.AMAZON_ADDRESS + url)
                .map(s -> {
                    try {
                        return Picasso.with(context)
                                .load(s)
                                .transform(new MaxSizeTextureTransformation(context))
                                .transform(new CircleSizeTransformation(56))
                                .get();
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .map(bitmap -> PinUtils.generatePinWithPhoto(context, bitmap));
    }

    private static Bitmap generatePinWithPhoto(Context context, Bitmap photo) {
        Bitmap pin = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_background_dog_pin);
        Bitmap result = Bitmap.createBitmap(pin.getWidth(), pin.getHeight(), pin.getConfig());
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(pin, 0, 0, paint);
        canvas.drawBitmap(photo, 2, 2, paint);
        pin.recycle();
        return result;
    }
}
