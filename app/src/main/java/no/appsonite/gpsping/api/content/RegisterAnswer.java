package no.appsonite.gpsping.api.content;

import android.databinding.ObservableLong;

import com.google.gson.annotations.SerializedName;

import no.appsonite.gpsping.utils.ObservableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class RegisterAnswer extends ApiAnswer {
    private ObservableString cookie = new ObservableString();
    @SerializedName("user_id")
    private ObservableLong userId = new ObservableLong();

    public ObservableString getCookie() {
        return cookie;
    }

    public void setCookie(ObservableString cookie) {
        this.cookie = cookie;
    }

    public ObservableLong getUserId() {
        return userId;
    }

    public void setUserId(ObservableLong userId) {
        this.userId = userId;
    }
}
