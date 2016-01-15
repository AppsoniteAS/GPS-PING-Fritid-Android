package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import no.appsonite.gpsping.model.Tracker;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class MainFragmentViewModel extends BaseFragmentViewModel {
    public ObservableField<Tracker> activeTracker = new ObservableField<>();

    public interface ActionsListener {
        void mapAndTrackingClick();

        void historyClick();

        void settingsClick();

        void onStartClick();
    }

    private ActionsListener actionsListener;

    public void setActionsListener(ActionsListener actionsListener) {
        this.actionsListener = actionsListener;
    }

    public void onStartClick(View v) {
        if (actionsListener != null) {
            actionsListener.onStartClick();
        }
    }

    public void onMapClick(View v) {
        if (actionsListener != null) {
            actionsListener.mapAndTrackingClick();
        }
    }

    public void onHistoryClick(View v) {
        if (actionsListener != null) {
            actionsListener.historyClick();
        }
    }

    public void onSettingsClick(View v) {
        if (actionsListener != null) {
            actionsListener.settingsClick();
        }
    }
}
