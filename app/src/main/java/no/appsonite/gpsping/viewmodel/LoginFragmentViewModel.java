package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.utils.BindableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class LoginFragmentViewModel extends BaseFragmentViewModel {
    public BindableString login = new BindableString();
    public BindableString password = new BindableString();
    public ObservableField<String> loginError = new ObservableField<>();
    public ObservableField<String> passwordError = new ObservableField<>();
    private ActionListener actionListener;

    public void onLoginClick(View v) {
        if (validateData()) {
            if (actionListener != null) {
                actionListener.onLogin();
            }
        }
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

    public void onRegisterClick(View v) {
        if (actionListener != null) {
            actionListener.onRegisterClick();
        }
    }

    public TextView.OnEditorActionListener passwordDone = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                onLoginClick(null);
                return true;
            }
            return false;
        }
    };

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public interface ActionListener {
        void onLogin();

        void onRegisterClick();
    }
}
