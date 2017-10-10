package no.appsonite.gpsping.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by taras on 10/10/17.
 */

public class ImageUtils {
    private static final Point displaySize = new Point();

    public static Point getDisplaySize(Context context) {
        if (displaySize.x == 0 || displaySize.y == 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            display.getSize(displaySize);
        }
        return displaySize;
    }

    public static Bitmap resizeBitmap(int currentDPI, int targetDPI, Bitmap bitmap) {
        float scale = ((float) targetDPI) / currentDPI;
        int width = Math.round(bitmap.getWidth() * scale);
        int height = Math.round(bitmap.getHeight() * scale);
        Bitmap result = Bitmap.createScaledBitmap(bitmap, width, height, true);
        bitmap.recycle();
        return result;
    }

    public static Bitmap compressBitmap(Bitmap src, int targetHeight, int targetWidth) {
        int height = src.getHeight();
        int width = src.getWidth();

        if (targetHeight > height && targetWidth > width) {
            return src;
        }

        if (width > height) {
            // landscape
            float ratio = (float) height / targetHeight;
            height = targetHeight;
            width = (int) (width / ratio);
            // height now should be 320, width could be greater
        } else {
            // portrait
            float ratio = (float) width / targetWidth;
            width = targetWidth;
            height = (int) (height / ratio);
            // width now should be 240, height could be greater
        }

        return Bitmap.createScaledBitmap(src, width, height, true);
    }
}
