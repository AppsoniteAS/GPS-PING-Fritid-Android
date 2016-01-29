package no.appsonite.gpsping.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.utils.ObservableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 29.01.2016
 */
public class DisplayOptionsFragmentViewModel extends BaseFragmentViewModel {

    private static final String PREFS = "DisplayOptions";
    private static final String PREFS_HISTORY = "History";
    public ObservableString historyTime = new ObservableString();

    @Override
    public void onModelAttached() {
        super.onModelAttached();
        initDisplayOptions();
    }

    private void initDisplayOptions() {
        historyTime.set(getHistoryValue());
    }

    public void save() {
        SharedPreferences preferences = Application.getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        preferences.edit().putString(PREFS_HISTORY, historyTime.get()).apply();
    }

    public static long getHistoryValueSeconds() {
        String val = getHistoryValue();
        String[] split = val.split(" ");
        long minutes = Long.parseLong(split[0]);
        return minutes * 60;
    }

    private static String getHistoryValue() {
        SharedPreferences preferences = Application.getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String[] entries = Application.getContext().getResources().getStringArray(R.array.showDuring);
        return preferences.getString(PREFS_HISTORY, entries[1]);
    }
}
