package no.appsonite.gpsping.utils;

import android.databinding.BaseObservable;

import java.util.Objects;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class BindableString extends BaseObservable {
    private String value;
    public String get() {
        return value != null ? value : "";
    }
    public void set(String value) {
        if (value == null || !value.equals(this.value)) {
            this.value = value;
            notifyChange();
        }
    }
    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }
}
