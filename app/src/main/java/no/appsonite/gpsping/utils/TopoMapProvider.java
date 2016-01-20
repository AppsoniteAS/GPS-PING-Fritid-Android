package no.appsonite.gpsping.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import no.appsonite.gpsping.R;


/**
 * Created by Belozerow on 03.05.2014.
 */
public class TopoMapProvider implements TileProvider {
    private static final String FORMAT = "http://opencache.statkart.no/gatekeeper/gk/gk.open_gmaps?layers=topo2&zoom=%d&x=%d&y=%d&format=image/png";
    private Context context;

    public TopoMapProvider(Context context) {
        this.context = context;
    }


    public URL getTileUrl(int x, int y, int zoom) {
        try {
            return new URL(String.format(FORMAT, zoom, x, y));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        Bitmap bitmap = loadBitmap(context, getTileFilename(x, y, zoom));
        if (bitmap == null) {
            bitmap = getBitmapFromURL(getTileUrl(x, y, zoom));
            saveFile(context, bitmap, getTileFilename(x, y, zoom));
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return new Tile(256, 256, byteArray);
    }

    private Bitmap getBitmapFromURL(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap adjustOpacity(Bitmap bitmap) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(256, 256, conf);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAlpha(128);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bmp;
    }

    private String getTileFilename(int x, int y, int zoom) {
        return zoom + "." + x + "." + y + ".png";
    }

    public void saveFile(Context context, Bitmap b, String picName) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 0, bytes);
            File directory = new File(Environment.getExternalStorageDirectory()
                    + File.separator + context.getString(R.string.app_name) + File.separator + "tilesCache");
            directory.mkdirs();
            File outputFile = new File(directory, picName);
            outputFile.createNewFile();
            FileOutputStream fo = new FileOutputStream(outputFile);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException ignored) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Bitmap loadBitmap(Context context, String picName) {
        Bitmap b;
        File directory = new File(Environment.getExternalStorageDirectory()
                + File.separator + context.getString(R.string.app_name) + File.separator + "tilesCache");
        directory.mkdirs();
        File tile = new File(directory, picName);
        if (tile.exists()) {
            b = BitmapFactory.decodeFile(tile.getPath());
            return b;
        }
        return null;
    }

    public static void clearCache(Context context) {
        File dir = new File(Environment.getExternalStorageDirectory()
                + File.separator + context.getString(R.string.app_name) + File.separator + "tilesCache");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }
}