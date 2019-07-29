package no.appsonite.gpsping.utils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;

/**
 * Created by taras on 10/31/17.
 */

public class DrawableSetHelper {
    public static Drawable getDrawableBattery(int bat) {
        Resources resources = Application.getContext().getResources();
        if (bat <= 12) {
            return resources.getDrawable(R.drawable.ic_battery_0);
        } else if (bat > 12 && bat <= 37) {
            return resources.getDrawable(R.drawable.ic_battery_25);
        } else if (bat > 37 && bat <= 62) {
            return resources.getDrawable(R.drawable.ic_battery_50);
        } else if (bat > 62 && bat <= 87) {
            return resources.getDrawable(R.drawable.ic_battery_75);
        } else {
            return resources.getDrawable(R.drawable.ic_battery_100);
        }
    }

    public static Drawable getDrawableSignal(int signal) {
        Resources resources = Application.getContext().getResources();
        switch (signal) {
            case 0:
                return resources.getDrawable(R.drawable.ic_signal_0);
            case 1:
                return resources.getDrawable(R.drawable.ic_signal_1);
            case 2:
                return resources.getDrawable(R.drawable.ic_signal_2);
            case 3:
                return resources.getDrawable(R.drawable.ic_signal_3);
            case 4:
                return resources.getDrawable(R.drawable.ic_signal_4);
            case 5:
                return resources.getDrawable(R.drawable.ic_signal_5);
            default:
                return resources.getDrawable(R.drawable.ic_signal_0);
        }
    }

}
