package no.appsonite.gpsping.utils;

import android.databinding.BaseObservable;
import android.databinding.BindingConversion;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */

public class ObservableString extends BaseObservable implements Parcelable, Serializable {
    static final long serialVersionUID = 1L;
    private String mValue;
    public static final Creator<ObservableString> CREATOR = new Creator<ObservableString>() {
        public ObservableString createFromParcel(Parcel source) {
            return new ObservableString(source.readString());
        }

        public ObservableString[] newArray(int size) {
            return new ObservableString[size];
        }
    };

    public ObservableString(String value) {
        this.mValue = value;
    }

    public ObservableString() {
    }

    public String get() {
        return this.mValue;
    }

    public void set(String value) {
        if (!equals(value, this.mValue)) {
            this.mValue = value;
            this.notifyChange();
        }

    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mValue);
    }

    public boolean isEmpty() {
        return mValue == null || mValue.isEmpty();
    }

    @BindingConversion
    public static String convertToString(ObservableString s) {
        if (s == null) return "null";
        return s.get();
    }
}
