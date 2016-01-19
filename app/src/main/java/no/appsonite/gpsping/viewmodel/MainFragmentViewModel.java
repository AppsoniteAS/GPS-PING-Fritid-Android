package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;

import no.appsonite.gpsping.model.Tracker;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class MainFragmentViewModel extends BaseFragmentViewModel {
    public ObservableField<Tracker> activeTracker = new ObservableField<>();
}
