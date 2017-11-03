package no.appsonite.gpsping.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import no.appsonite.gpsping.Application;

/**
 * Created by taras on 11/2/17.
 */

public class ProfileUpdateManager {
    public static File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Application.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
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
            e.printStackTrace();
        }
        return output;
    }

    public static File loadImage(File file, int targetW, int targetH) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        try {

            BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            if (bitmap == null)
                return null;
            Matrix matrix = new Matrix();
            matrix.postRotate(getRotationAngle(file));
            int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
            bitmap = Bitmap.createBitmap(bitmap, (bitmap.getWidth() - size) / 2, (bitmap.getHeight() - size) / 2, size, size,
                    matrix, true);
            File output = createImageFile();
            FileOutputStream outputStream = new FileOutputStream(output);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            bitmap.recycle();
            return output;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError outOfMemoryError) {
            outOfMemoryError.printStackTrace();
        }
        return null;
    }

    private static int getRotationAngle(File file) {
        try {
            ExifInterface exif = new ExifInterface(file.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }
            return angle;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
