package no.appsonite.gpsping.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.UUID;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public class Utils {

    public static void hideKeyboard(Activity activity) {
        InputMethodManager im = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showKeyboard(Activity activity, EditText edit) {
        InputMethodManager imgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.showSoftInput(edit, 0);
    }

    public static Point getDisplaySize(Context context) {
        Point point = new Point();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        display.getSize(point);
        return point;
    }

    public static int getActionBarSize(Activity activity) {
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
        return TypedValue.complexToDimensionPixelSize(typedValue.data, activity.getResources().getDisplayMetrics());
    }

    public static String getDistanceText(float distance) {
        if (distance >= 1000f) {
            return Application.getContext().getString(R.string.distanceKm, ((int) Math.ceil(distance / 1000)));
        } else {
            return Application.getContext().getString(R.string.distanceM, ((int) Math.ceil(distance)));
        }
    }

    public static String getUniqueId() {
        SharedPreferences sharedPreferences = Application.getContext().getSharedPreferences("UUID", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("UUID", null);
        if (!TextUtils.isEmpty(id))
            return id;
        try {
            id = Settings.Secure.getString(Application.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception ignore) {

        }

        if (TextUtils.isEmpty(id)) {
            id = UUID.randomUUID().toString();
        }
        sharedPreferences.edit().putString("UUID", id).apply();
        return id;
    }
}
