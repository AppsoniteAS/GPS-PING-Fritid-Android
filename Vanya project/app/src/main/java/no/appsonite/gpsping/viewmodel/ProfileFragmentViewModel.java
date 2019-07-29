package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.text.TextUtils;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.api.content.Profile;
import no.appsonite.gpsping.utils.ProfileInitHelper;
import rx.Observable;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class ProfileFragmentViewModel extends BaseFragmentViewModel {
    public ObservableField<String> username = new ObservableField<>();
    public ObservableField<String> fullName = new ObservableField<>();
    public ObservableField<String> address = new ObservableField<>();
    public ObservableField<String> zipCode = new ObservableField<>();
    public ObservableField<String> city = new ObservableField<>();
    public ObservableField<String> country = new ObservableField<>();
    public ObservableField<String> phoneCode = new ObservableField<>();
    public ObservableField<String> phoneNumber = new ObservableField<>();
    public ObservableField<String> email = new ObservableField<>();

    public ObservableField<String> usernameError = new ObservableField<>();
    public ObservableField<String> fullNameError = new ObservableField<>();
    public ObservableField<String> addressError = new ObservableField<>();
    public ObservableField<String> zipCodeError = new ObservableField<>();
    public ObservableField<String> cityError = new ObservableField<>();
    public ObservableField<String> countryError = new ObservableField<>();
    public ObservableField<String> phoneCodeError = new ObservableField<>();
    public ObservableField<String> phoneNumberError = new ObservableField<>();
    public ObservableField<String> emailError = new ObservableField<>();

    private Profile profile;

    public Observable<ApiAnswer> onSaveClick() {
        if (validateData()) {
            final LoginAnswer loginAnswer = AuthHelper.getCredentials();
            setFirstAndLastNameToProfile();
            setAllValuesToProfile();

            loginAnswer.setUser(profile);
            Observable<ApiAnswer> observable = execute(AuthHelper.updateUser(profile)).cache();

            observable.subscribe(apiAnswer -> {
            }, Throwable::printStackTrace, () -> AuthHelper.putCredentials(loginAnswer));
            return observable;
        }
        return null;
    }

    private void setFirstAndLastNameToProfile() {
        String fullName = this.fullName.get().trim();

        int spaceIndex = fullName.indexOf(" ");
        if (spaceIndex == -1) {
            profile.firstName.set(fullName);
            profile.lastName.set(" ");
        } else {
            String firstName = fullName.substring(0, spaceIndex).trim();
            String lastName = fullName.substring(spaceIndex, fullName.length()).trim();
            profile.firstName.set(firstName);
            profile.lastName.set(lastName);
        }
    }

    private void setAllValuesToProfile() {
        profile.username.set(username.get());
        profile.displayname.set(fullName.get().trim());
        profile.address.set(address.get());
        profile.zipCode.set(zipCode.get());
        profile.city.set(city.get());
        profile.country.set(country.get());
        profile.phoneCode.set(phoneCode.get());
        profile.phoneNumber.set(phoneNumber.get());
        profile.email.set(email.get());
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        bindProfile();
    }

    private void bindProfile() {
        Profile profile = AuthHelper.getCredentials().getUser();
        initNullFieldsToProfile(profile);
        bind(profile);
        this.profile = profile;
    }

    private void initNullFieldsToProfile(Profile profile) {
        ProfileInitHelper.initNullFieldsToProfile(profile);
    }

    private void bind(Profile profile) {
        username.set(profile.username.get());
        if (TextUtils.isEmpty(profile.lastName.get().trim())) {
            fullName.set(profile.firstName.get());
        } else {
            fullName.set(profile.firstName.get() + " " + profile.lastName.get());
        }
        address.set(profile.address.get());
        zipCode.set(profile.zipCode.get());
        city.set(profile.city.get());
        country.set(profile.country.get());
        phoneCode.set(profile.phoneCode.get());
        phoneNumber.set(profile.phoneNumber.get());
        email.set(profile.email.get());
    }

    private boolean validateData() {
        if (TextUtils.isEmpty(username.get())) {
            usernameError.set(getContext().getString(R.string.loginCanNotBeEmpty));
            return false;
        }
        usernameError.set(null);

        if (TextUtils.isEmpty(fullName.get())) {
            fullNameError.set(getContext().getString(R.string.nameCanNotBeEmpty));
            return false;
        }
        fullNameError.set(null);

        if (TextUtils.isEmpty(address.get())) {
            addressError.set(getContext().getString(R.string.addressCanNotBeEmpty));
            return false;
        }
        addressError.set(null);

        if (TextUtils.isEmpty(zipCode.get())) {
            zipCodeError.set(getContext().getString(R.string.zipCodeCanNotBeEmpty));
            return false;
        }
        zipCodeError.set(null);

        if (TextUtils.isEmpty(city.get())) {
            cityError.set(getContext().getString(R.string.cityCanNotBeEmpty));
            return false;
        }
        cityError.set(null);

        if (TextUtils.isEmpty(country.get())) {
            countryError.set(getContext().getString(R.string.countryCanNotBeEmpty));
            return false;
        }
        countryError.set(null);

        if (TextUtils.isEmpty(email.get())) {
            emailError.set(getContext().getString(R.string.emailCanNotBeEmpty));
            return false;
        }
        emailError.set(null);

        if (TextUtils.isEmpty(phoneCode.get())) {
            phoneCodeError.set(getContext().getString(R.string.phoneCodeCanNotBeEmpty));
            return false;
        }
        phoneCodeError.set(null);

        if (TextUtils.isEmpty(phoneNumber.get())) {
            phoneNumberError.set(getContext().getString(R.string.phoneNumberCanNotBeEmpty));
            return false;
        }
        phoneNumberError.set(null);

        return true;
    }
}
