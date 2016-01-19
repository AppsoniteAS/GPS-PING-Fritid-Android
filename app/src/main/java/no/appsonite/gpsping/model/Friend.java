package no.appsonite.gpsping.model;

import android.databinding.ObservableField;

import no.appsonite.gpsping.utils.ObservableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public class Friend {
    public ObservableString name = new ObservableString();
    public ObservableString userName = new ObservableString();
    public ObservableField<Status> status = new ObservableField<>();

    public enum Status {
        not_confirmed, visible, invisible, not_added
    }
}
