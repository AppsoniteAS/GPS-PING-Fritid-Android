package no.appsonite.gpsping.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;

import com.github.lzyzsd.randomcolor.RandomColor;

import java.util.HashMap;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.model.Friend;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 26.01.2016
 */
public class MarkerHelper {
    private static HashMap<Long, Integer> colorsMap = new HashMap<>();
    private static HashMap<Long, Bitmap> userBitmaps = new HashMap<>();
    private static HashMap<Long, Bitmap> trackerBitmaps = new HashMap<>();
    private static HashMap<Long, Bitmap> trackerHistoryBitmaps = new HashMap<>();

    public static Bitmap getUserBitmap(Friend friend) {
        Bitmap bitmap = userBitmaps.get(friend.id.get());
        if (bitmap == null) {
            bitmap = getMarkerBitmap(getFriendColor(friend), R.drawable.ic_ellipse, R.drawable.ic_user_marker);
            userBitmaps.put(friend.id.get(), bitmap);
        }
        return bitmap;
    }

    public static Bitmap getTrackerBitmap(Friend friend) {
        Bitmap bitmap = trackerBitmaps.get(friend.id.get());
        if (bitmap == null) {
            bitmap = getMarkerBitmap(getFriendColor(friend), R.drawable.ic_triangle, R.drawable.ic_triangle_marker);
            trackerBitmaps.put(friend.id.get(), bitmap);
        }
        return bitmap;
    }

    public static Bitmap getTrackerHistoryBitmap(Friend friend) {
        Bitmap bitmap = trackerHistoryBitmaps.get(friend.id.get());
        if (bitmap == null) {
            bitmap = getMarkerBitmap(getFriendColor(friend), R.drawable.ic_small_ellipse, R.drawable.ic_ellipse_marker);
            trackerHistoryBitmaps.put(friend.id.get(), bitmap);
        }
        return bitmap;
    }

    public static int getFriendColor(Friend friend) {
        Integer color = colorsMap.get(friend.id.get());
        if (color == null) {
            color = new RandomColor().randomColor();
            colorsMap.put(friend.id.get(), color);
        }
        return color;
    }

    private static Bitmap getMarkerBitmap(int color, int bottomRes, int topRes) {
        Resources res = Application.getContext().getResources();
        final Bitmap topIcon = BitmapFactory.decodeResource(res, topRes);
        final Bitmap bottomIcon = BitmapFactory.decodeResource(res, bottomRes);

        final Bitmap output = Bitmap.createBitmap(bottomIcon.getWidth(), bottomIcon.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bottomIcon.getWidth(), bottomIcon.getHeight());
        canvas.drawARGB(0, 0, 0, 0);
        paint.setAntiAlias(true);
        canvas.drawBitmap(bottomIcon, rect, rect, paint);

        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));

        float left = (bottomIcon.getWidth() - topIcon.getWidth()) / 2f;
        float top = (bottomIcon.getHeight() - topIcon.getHeight()) / 2f;
        canvas.drawBitmap(topIcon, left, top, paint);

        topIcon.recycle();
        return output;
    }
}
