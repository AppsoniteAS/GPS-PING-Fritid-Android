package no.appsonite.gpsping.utils;

import android.content.Context;
import android.content.SharedPreferences;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;

/**
 * Created by taras on 10/6/17.
 */

public class TrackingHistoryTime {
    private static final String PREFS_HISTORY = "History";
    private static SharedPreferences prefs = Application.getContext().getSharedPreferences("DisplayOptions", Context.MODE_PRIVATE);

    public static void saveTrackingHistory(String historyTime) {
        prefs.edit().putString(PREFS_HISTORY, historyTime).apply();
    }

    public static long getTrackingHistorySeconds() {
        String val = getHTrackingHistory();
        String[] split = val.split(" ");
        long minutes = Long.parseLong(split[0]);
        return minutes * 60;
    }

    public static String getHTrackingHistory() {
        String[] entries = Application.getContext().getResources().getStringArray(R.array.showDuring);
        return prefs.getString(PREFS_HISTORY, entries[1]);
    }
}
