package no.appsonite.gpsping.model;


import android.databinding.ObservableField;

import java.io.Serializable;

import no.appsonite.gpsping.utils.ObservableBoolean;
import no.appsonite.gpsping.utils.ObservableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class Tracker implements Serializable {
    public ObservableString trackerName = new ObservableString();
    public ObservableString trackerNumber = new ObservableString();
    public ObservableString imeiNumber = new ObservableString();
    public ObservableBoolean checkForStand = new ObservableBoolean();
    public ObservableString signalRepeatTime = new ObservableString();
    public ObservableString signalRepeatTimeMeasurement = new ObservableString();
    public ObservableBoolean isEnabled = new ObservableBoolean(true);

    public ObservableField<Type> type = new ObservableField<>(Type.TK_STAR);

    public enum Type {
        TK_STAR, TK_STAR_PET, TK_ANYWHERE
    }
}
