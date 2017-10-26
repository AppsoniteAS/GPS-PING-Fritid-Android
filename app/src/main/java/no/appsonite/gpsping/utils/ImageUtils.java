package no.appsonite.gpsping.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import no.appsonite.gpsping.Application;

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

    public static File copyFileFromUri(String uri) {
        File output = null;
        try {
            output = createImageFile();
            final int chunkSize = 1024;
            byte[] imageData = new byte[chunkSize];
            InputStream is = Application.getContext().getContentResolver().openInputStream(Uri.parse(uri));
            OutputStream out = new FileOutputStream(output);
            int bytesRead;
            //noinspection ConstantConditions
            while ((bytesRead = is.read(imageData)) > 0) {
                out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
            }
        } catch (IOException e) {

        }
        return output;
    }

    private static File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Application.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }
}
