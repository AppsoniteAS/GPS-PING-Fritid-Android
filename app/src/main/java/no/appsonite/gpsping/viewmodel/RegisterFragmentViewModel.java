package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.text.TextUtils;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.api.content.Profile;
import no.appsonite.gpsping.utils.ObservableString;
import rx.Observable;
import rx.Observer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class RegisterFragmentViewModel extends BaseFragmentViewModel {
    public ObservableField<Profile> profile = new ObservableField<>(new Profile());
    public ObservableString passwordRepeat = new ObservableString();

    public ObservableField<String> userNameError = new ObservableField<>();
    public ObservableField<String> fullNameError = new ObservableField<>();
    public ObservableField<String> emailError = new ObservableField<>();
    public ObservableField<String> passwordError = new ObservableField<>();
    public ObservableField<String> passwordRepeatError = new ObservableField<>();

    public ObservableField<String> phoneCodeError = new ObservableField<>();
    public ObservableField<String> phoneNumberError = new ObservableField<>();
    public ObservableField<String> addressError = new ObservableField<>();
    public ObservableField<String> cityError = new ObservableField<>();
    public ObservableField<String> countryError = new ObservableField<>();
    public ObservableField<String> zipCodeError = new ObservableField<>();

    public Observable<LoginAnswer> onRegisterClick() {
        if (validateData()) {
            Observable<LoginAnswer> observable = execute(AuthHelper.register(profile.get())).cache();
            observable.subscribe(new Observer<LoginAnswer>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onNext(LoginAnswer loginAnswer) {
                    if (loginAnswer.isSuccess()) {
                        AuthHelper.putCredentials(loginAnswer);
                    }
                }
            });
            return observable;
        }
        return null;
    }

    private boolean validateData() {
        if (TextUtils.isEmpty(profile.get().username.get())) {
            userNameError.set(getContext().getString(R.string.loginCanNotBeEmpty));
            return false;
        }
        userNameError.set(null);

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

        if (profile.get().phoneCode.get().length() < 2) {
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

        if (TextUtils.isEmpty(profile.get().password.get())) {
            passwordError.set(getContext().getString(R.string.passwordCanNotBeEmpty));
            return false;
        }
        passwordError.set(null);

        if (!profile.get().password.get().equals(passwordRepeat.get())) {
            passwordRepeatError.set(getContext().getString(R.string.passwordNotConfirmed));
            return false;
        }
        passwordRepeatError.set(null);
        return true;
    }
}
