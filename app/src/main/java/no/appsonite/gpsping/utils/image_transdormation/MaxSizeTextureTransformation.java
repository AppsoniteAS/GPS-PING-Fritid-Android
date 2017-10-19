package no.appsonite.gpsping.utils.image_transdormation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;

import com.squareup.picasso.Transformation;

import no.appsonite.gpsping.utils.ImageUtils;

/**
 * Created by taras on 10/18/17.
 */

public class MaxSizeTextureTransformation implements Transformation {
    private static final int maxTextureSize = 2048;
    private Context context;

    public MaxSizeTextureTransformation(Context context) {
        this.context = context;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        if (source == null) return null;
        Point displaySize = ImageUtils.getDisplaySize(context);
        int textureSize = displaySize.x == 0 || displaySize.y == 0 ? maxTextureSize :
                Math.min(maxTextureSize, Math.max(displaySize.x, displaySize.y));
        int sourceWidth = source.getWidth(), sourceHeight = source.getHeight();
        if (sourceWidth > textureSize || sourceHeight > textureSize) {
            int destWidth, destHeight;
            if (sourceWidth > sourceHeight) {
                destWidth = textureSize;
                destHeight = (destWidth * sourceHeight) / sourceWidth;
            } else if (sourceWidth < sourceHeight) {
                destHeight = textureSize;
                destWidth = (destHeight * sourceWidth) / sourceHeight;
            } else {
                destWidth = destHeight = textureSize;
            }
            Bitmap dest = Bitmap.createScaledBitmap(source, destWidth, destHeight, true);
            source.recycle();
            return dest;
        } else {
            return source;
        }
    }

    @Override
    public String key() {
        return MaxSizeTextureTransformation.class.getSimpleName();
    }
}
