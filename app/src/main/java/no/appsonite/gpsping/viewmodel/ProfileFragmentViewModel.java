package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.text.TextUtils;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.api.content.Profile;
import rx.Observable;
import rx.Observer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class ProfileFragmentViewModel extends BaseFragmentViewModel {
    public ObservableField<Profile> profile = new ObservableField<>();

    public ObservableField<String> usernameError = new ObservableField<>();
    public ObservableField<String> fullNameError = new ObservableField<>();
    public ObservableField<String> emailError = new ObservableField<>();
    public ObservableField<String> phoneCodeError = new ObservableField<>();
    public ObservableField<String> phoneNumberError = new ObservableField<>();
    public ObservableField<String> addressError = new ObservableField<>();
    public ObservableField<String> cityError = new ObservableField<>();
    public ObservableField<String> countryError = new ObservableField<>();
    public ObservableField<String> zipCodeError = new ObservableField<>();

    public Observable<ApiAnswer> onSaveClick() {
        if (validateData()) {
            final LoginAnswer loginAnswer = AuthHelper.getCredentials();
            String fullName = profile.get().displayname.get().trim();
            if (fullName != null) {
                int spaceIndex = fullName.indexOf(" ");
                if (spaceIndex == -1) {
                    profile.get().firstName.set(fullName);
                } else {
                    String firstName = fullName.substring(0, spaceIndex).trim();
                    String lastName = fullName.substring(spaceIndex, fullName.length()).trim();
                    profile.get().firstName.set(firstName);
                    profile.get().lastName.set(lastName);
                }
            }

            if (TextUtils.isEmpty(profile.get().firstName.get())) {
                profile.get().firstName.set(" ");
            }

            if (TextUtils.isEmpty(profile.get().lastName.get())) {
                profile.get().lastName.set(" ");
            }

            loginAnswer.setUser(this.profile.get());
            Observable<ApiAnswer> observable = execute(AuthHelper.updateUser(this.profile.get())).cache();
            observable.subscribe(new Observer<ApiAnswer>() {
                @Override
                public void onCompleted() {
                    AuthHelper.putCredentials(loginAnswer);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(ApiAnswer apiAnswer) {

                }
            });
            return observable;
        }
        return null;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        Profile profile = AuthHelper.getCredentials().getUser();
        if (TextUtils.isEmpty(profile.phoneCode.get())) {
            profile.phoneCode.set("+");
        }
        profile.displayname.set(profile.firstName.get() + " " + profile.lastName.get());
        this.profile.set(profile);
    }


    private boolean validateData() {
        if (TextUtils.isEmpty(profile.get().username.get())) {
            usernameError.set(getContext().getString(R.string.loginCanNotBeEmpty));
            return false;
        }
        usernameError.set(null);

        if (TextUtils.isEmpty(profile.get().displayname.get())) {
            fullNameError.set(getContext().getString(R.string.nameCanNotBeEmpty));
            return false;
        }
        fullNameError.set(null);

        if (TextUtils.isEmpty(profile.get().email.get())) {
            emailError.set(getContext().getString(R.string.emailCanNotBeEmpty));
            return false;
        }
        emailError.set(null);

        if (profile.get().phoneCode.get().length() == 0) {
            phoneCodeError.set(getContext().getString(R.string.phoneCodeCanNotBeEmpty));
            return false;
        }
        phoneCodeError.set(null);

        if (TextUtils.isEmpty(profile.get().phoneNumber.get())) {
            phoneNumberError.set(getContext().getString(R.string.phoneNumberCanNotBeEmpty));
            return false;
        }
        phoneNumberError.set(null);

        if (TextUtils.isEmpty(profile.get().address.get())) {
            addressError.set(getContext().getString(R.string.addressCanNotBeEmpty));
            return false;
        }
        addressError.set(null);

        if (TextUtils.isEmpty(profile.get().city.get())) {
            cityError.set(getContext().getString(R.string.cityCanNotBeEmpty));
            return false;
        }
        cityError.set(null);

        if (TextUtils.isEmpty(profile.get().country.get())) {
            countryError.set(getContext().getString(R.string.countryCanNotBeEmpty));
            return false;
        }
        countryError.set(null);

        if (TextUtils.isEmpty(profile.get().zipCode.get())) {
            zipCodeError.set(getContext().getString(R.string.zipCodeCanNotBeEmpty));
            return false;
        }
        zipCodeError.set(null);

        return true;
    }
}
