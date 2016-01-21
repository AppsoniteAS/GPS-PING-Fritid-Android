package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.api.content.Profile;
import no.appsonite.gpsping.utils.ObservableString;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;

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

    public Observable<LoginAnswer> onRegisterClick() {
        if (validateData()) {
            Observable<LoginAnswer> observable = AuthHelper.register(profile.get());
            observable.subscribe(new Observer<LoginAnswer>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

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
