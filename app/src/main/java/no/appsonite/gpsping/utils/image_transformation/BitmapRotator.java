package no.appsonite.gpsping.utils.image_transformation;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by taras on 11/2/17.
 */

public class BitmapRotator {
    public Bitmap rotateBitmapIfNecessary(String path, Bitmap bitmap) {
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            if (orientation == 6 || orientation == 3 || orientation == 8) {
                Matrix matrix = new Matrix();
                switch (orientation) {
                    case 6:
                        matrix.postRotate(90);
                        break;
                    case 3:
                        matrix.postRotate(180);
                        break;
                    case 8:
                        matrix.postRotate(270);
                        break;

                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
