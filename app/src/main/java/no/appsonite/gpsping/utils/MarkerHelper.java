package no.appsonite.gpsping.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

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
    static String[] COLORS = new String[]{
            "#FF0000", "#00FF00", "#0000FF", "#FF00FF", "#00FFFF", "#000000",
            "#800000", "#008000", "#000080", "#808000", "#800080", "#008080", "#808080",
            "#C00000", "#00C000", "#0000C0", "#C0C000", "#C000C0", "#00C0C0", "#C0C0C0",
            "#400000", "#004000", "#000040", "#404000", "#400040", "#004040", "#404040",
            "#200000", "#002000", "#000020", "#202000", "#200020", "#002020", "#202020",
            "#600000", "#006000", "#000060", "#606000", "#600060", "#006060", "#606060",
            "#A00000", "#00A000", "#0000A0", "#A0A000", "#A000A0", "#00A0A0", "#A0A0A0",
            "#E00000", "#00E000", "#0000E0", "#E0E000", "#E000E0", "#00E0E0", "#E0E0E0"
    };

    private static HashMap<Long, Integer> colorsMap = new HashMap<>();
    private static int lastColor = -1;

    private static HashMap<Long, BitmapDescriptor> userBitmaps = new HashMap<>();
    private static HashMap<Long, BitmapDescriptor> poiBitmaps = new HashMap<>();

    public static BitmapDescriptor getPoiBitmapDescriptor(Friend friend) {
        BitmapDescriptor bitmap = poiBitmaps.get(friend.id.get());
        if (bitmap == null) {
            bitmap = BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(getFriendColor(friend), R.drawable.poi_marker, R.drawable.poi_marker));
            poiBitmaps.put(friend.id.get(), bitmap);
        }
        return bitmap;
    }

    public static BitmapDescriptor getUserBitmapDescriptor(Friend friend) {
        BitmapDescriptor bitmap = userBitmaps.get(friend.id.get());
        if (bitmap == null) {
            bitmap = BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(getFriendColor(friend), R.drawable.ic_ellipse, R.drawable.ic_user_marker));
            userBitmaps.put(friend.id.get(), bitmap);
        }
        return bitmap;
    }

    public static Bitmap getUserBitmap(Friend friend) {
        return getMarkerBitmap(getFriendColor(friend), R.drawable.ic_ellipse, R.drawable.ic_user_marker);
    }

    private static int getFriendColor(Friend friend) {
        if (friend == null || friend.id == null)
            return 0;
        Integer color = colorsMap.get(friend.id.get());
        if (color == null) {
            if (++lastColor >= COLORS.length) {
                lastColor = 0;
            }
            color = Color.parseColor(COLORS[lastColor]);
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
