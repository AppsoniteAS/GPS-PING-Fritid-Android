package no.appsonite.gpsping.api.content;

import com.google.gson.annotations.SerializedName;

import no.appsonite.gpsping.utils.ObservableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class Profile {
    public ObservableString username = new ObservableString();
    public ObservableString displayname = new ObservableString();
    public ObservableString password = new ObservableString();
    public ObservableString email = new ObservableString();
    public Long id;
}
