package no.appsonite.gpsping.api.content;

import com.google.gson.annotations.SerializedName;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class LoginAnswer extends ApiAnswer {
    private String cookie;
    @SerializedName("cookie_name")
    private String cookieName;
    private Profile user;

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public Profile getUser() {
        return user;
    }

    public void setUser(Profile user) {
        this.user = user;
    }
}
