package no.appsonite.gpsping.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class Tracker extends BaseObservable {
    private String trackerName;
    private String trackerNumber;
    private String imeiNumber;

    @Bindable
    public String getTrackerName() {
        return trackerName;
    }

    public void setTrackerName(String trackerName) {
        this.trackerName = trackerName;
        notifyPropertyChanged(BR.trackerName);
    }

    @Bindable
    public String getTrackerNumber() {
        return trackerNumber;
    }

    public void setTrackerNumber(String trackerNumber) {
        this.trackerNumber = trackerNumber;
        notifyPropertyChanged(BR.trackerNumber);

    }

    @Bindable
    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
        notifyPropertyChanged(BR.imeiNumber);
    }
}
