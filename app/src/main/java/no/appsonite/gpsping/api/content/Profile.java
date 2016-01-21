package no.appsonite.gpsping.api.content;

import android.databinding.Observable;
import android.text.TextUtils;

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
    @SerializedName("firstname")
    public String firstName;
    @SerializedName("lastname")
    public String lastName;
    public Long id;

    public Profile() {
        super();
        init();
    }

    public void updateLastAndFirstNames() {
        try {
            String fullName = displayname.get();
            if (!TextUtils.isEmpty(fullName)) {
                int splitIndex = fullName.indexOf(" ");
                if (splitIndex != -1) {
                    firstName = fullName.substring(0, splitIndex).trim();
                    lastName = fullName.substring(splitIndex, fullName.length()).trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        displayname.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                updateLastAndFirstNames();
            }
        });

        if (!TextUtils.isEmpty(firstName)) {
            if (TextUtils.isEmpty(lastName)) {
                displayname.set(firstName);
            } else {
                displayname.set(firstName + " " + lastName);
            }
        }
    }
}
