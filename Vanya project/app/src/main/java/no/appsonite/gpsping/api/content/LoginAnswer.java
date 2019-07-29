package no.appsonite.gpsping.api.content;

import com.google.gson.annotations.SerializedName;

import no.appsonite.gpsping.utils.ObservableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class LoginAnswer extends ApiAnswer {
    private ObservableString cookie = new ObservableString();
    @SerializedName("cookie_name")
    private ObservableString cookieName = new ObservableString();
    private Profile user;

    public ObservableString getCookie() {
        return cookie;
    }

    public void setCookie(ObservableString cookie) {
        this.cookie = cookie;
    }

    public ObservableString getCookieName() {
        return cookieName;
    }

    public void setCookieName(ObservableString cookieName) {
        this.cookieName = cookieName;
    }

    public Profile getUser() {
        return user;
    }

    public void setUser(Profile user) {
        this.user = user;
    }
}
