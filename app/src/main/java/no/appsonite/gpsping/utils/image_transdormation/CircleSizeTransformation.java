package no.appsonite.gpsping.utils.image_transdormation;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * Created by taras on 10/18/17.
 */

public class CircleSizeTransformation implements Transformation {
    private int targetSize;

    public CircleSizeTransformation(int targetSize) {
        this.targetSize = targetSize;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }
        Bitmap targetBitmap = Bitmap.createScaledBitmap(squaredBitmap, targetSize, targetSize, true);
        if (targetBitmap != squaredBitmap) {
            squaredBitmap.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(targetSize, targetSize, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(targetBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = targetSize / 2f;
        canvas.drawCircle(r, r, r, paint);

        targetBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return CircleSizeTransformation.class.getSimpleName();
    }
}
