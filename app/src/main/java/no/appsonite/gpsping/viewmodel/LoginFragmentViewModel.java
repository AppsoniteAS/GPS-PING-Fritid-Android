package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.utils.ObservableString;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class LoginFragmentViewModel extends BaseFragmentViewModel {
    public ObservableString login = new ObservableString();
    public ObservableString password = new ObservableString();
    public ObservableField<String> loginError = new ObservableField<>();
    public ObservableField<String> passwordError = new ObservableField<>();

    public Observable<LoginAnswer> onLoginClick() {
        if (validateData()) {
            Observable<LoginAnswer> observable = execute(AuthHelper.login(login.get(), password.get())).cache();
            observable.subscribe(new Action1<LoginAnswer>() {
                @Override
                public void call(LoginAnswer loginAnswer) {
                    if (loginAnswer.isSuccess()) {
                        AuthHelper.putCredentials(loginAnswer);
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
            return observable;
        }
        return null;
    }

    private boolean validateData() {
        if (TextUtils.isEmpty(login.get())) {
            loginError.set(getContext().getString(R.string.loginCanNotBeEmpty));
            return false;
        }
        loginError.set(null);
        if (TextUtils.isEmpty(password.get())) {
            passwordError.set(getContext().getString(R.string.passwordCanNotBeEmpty));
            return false;
        }
        passwordError.set(null);
        return true;
    }

    public TextView.OnEditorActionListener passwordDone = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                onLoginClick();
                return true;
            }
            return false;
        }
    };

}
