package no.appsonite.gpsping.api.content;

import android.databinding.Observable;
import android.databinding.ObservableLong;
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
    public ObservableString firstName = new ObservableString();
    @SerializedName("lastname")
    public ObservableString lastName = new ObservableString();
    public ObservableLong id = new ObservableLong();
    @SerializedName("Phone_pref")
    public ObservableString phoneCode = new ObservableString("+");
    @SerializedName("Phone_num")
    public ObservableString phoneNumber = new ObservableString();

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
                    firstName.set(fullName.substring(0, splitIndex).trim());
                    lastName.set(fullName.substring(splitIndex, fullName.length()).trim());
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

        if (!TextUtils.isEmpty(firstName.get())) {
            if (TextUtils.isEmpty(lastName.get())) {
                displayname.set(firstName.get());
            } else {
                displayname.set(firstName.get() + " " + lastName.get());
            }
        }

        phoneCode.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                String code = phoneCode.get();
                if (code.lastIndexOf("+") > 0 || code.lastIndexOf("+") == -1) {
                    String result = code.replace("+", "");
                    phoneCode.set("+" + result);
                }
            }
        });
    }
}
