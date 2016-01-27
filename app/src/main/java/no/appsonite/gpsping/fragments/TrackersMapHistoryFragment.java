package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Date;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.viewmodel.TrackersMapFragmentViewModel;
import no.appsonite.gpsping.viewmodel.TrackersMapHistoryFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 21.01.2016
 */
public class TrackersMapHistoryFragment extends TrackersMapBaseFragment<TrackersMapHistoryFragmentViewModel> implements CalendarDialogFragment.CalendarListener {
    private static final String TAG = "TrackersMapHistoryFragment";

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_map_history, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_calendar) {
            CalendarDialogFragment calendarDialogFragment = CalendarDialogFragment.newInstance();
            calendarDialogFragment.show(getChildFragmentManager(), CalendarDialogFragment.TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static TrackersMapHistoryFragment newInstance() {
        Bundle args = new Bundle();
        TrackersMapHistoryFragment fragment = new TrackersMapHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.history);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public void onDateSelected(Date date) {
        getModel().historyDate.set(date);
    }
}
