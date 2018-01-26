package no.appsonite.gpsping.utils;

import no.appsonite.gpsping.api.content.Profile;

/**
 * Created: Gushul Taras
 * E-mail: gushultaras125@gmail.com
 * Company: APPGRANULA LLC
 * Date: 1/26/18
 */

public class ProfileInitHelper {

    public static void initNullFieldsToProfile(Profile profile) {
        initObservableStrings(profile);
        initValues(profile);
    }

    private static void initObservableStrings(Profile profile) {
        if (profile.username == null) {
            profile.username = new ObservableString("");
        }
        if (profile.displayname == null) {
            profile.displayname = new ObservableString("");
        }
        if (profile.password == null) {
            profile.password = new ObservableString("");
        }
        if (profile.email == null) {
            profile.email = new ObservableString("");
        }
        if (profile.firstName == null) {
            profile.firstName = new ObservableString("");
        }
        if (profile.lastName == null) {
            profile.lastName = new ObservableString("");
        }
        if (profile.phoneCode == null) {
            profile.phoneCode = new ObservableString("");
        }
        if (profile.phoneNumber == null) {
            profile.phoneNumber = new ObservableString("");
        }
        if (profile.address == null) {
            profile.address = new ObservableString("");
        }
        if (profile.city == null) {
            profile.city = new ObservableString("");
        }
        if (profile.country == null) {
            profile.country = new ObservableString("");
        }
        if (profile.zipCode == null) {
            profile.zipCode = new ObservableString("");
        }
    }

    private static void initValues(Profile profile) {
        if (profile.username.get() == null) {
            profile.username.set("");
        }
        if (profile.displayname.get() == null) {
            profile.displayname.set("");
        }
        if (profile.password.get() == null) {
            profile.password.set("");
        }
        if (profile.email.get() == null) {
            profile.email.set("");
        }
        if (profile.firstName.get() == null) {
            profile.firstName.set("");
        }
        if (profile.lastName.get() == null) {
            profile.lastName.set("");
        }
        if (profile.phoneCode.get() == null) {
            profile.phoneCode.set("");
        }
        if (profile.phoneNumber.get() == null) {
            profile.phoneNumber.set("");
        }
        if (profile.address.get() == null) {
            profile.address.set("");
        }
        if (profile.city.get() == null) {
            profile.city.set("");
        }
        if (profile.country.get() == null) {
            profile.country.set("");
        }
        if (profile.zipCode.get() == null) {
            profile.zipCode.set("");
        }
    }

}
