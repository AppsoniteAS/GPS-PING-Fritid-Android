package no.appsonite.gpsping.utils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;

/**
 * Created by taras on 10/31/17.
 */

public class DrawableSetHelper {
    public static Drawable getDrawableBattery(String battery) {
        int bat = Integer.parseInt(battery);
        Resources resources = Application.getContext().getResources();
        if (bat <=12) {
            return resources.getDrawable(R.drawable.ic_battery_0);
        } else if (bat > 12 && bat <=37) {
            return resources.getDrawable(R.drawable.ic_battery_25);
        } else if (bat > 37 && bat <= 62) {
            return resources.getDrawable(R.drawable.ic_battery_50);
        } else if (bat > 62 && bat <= 87) {
            return resources.getDrawable(R.drawable.ic_battery_75);
        } else {
            return resources.getDrawable(R.drawable.ic_battery_100);
        }
    }

}
