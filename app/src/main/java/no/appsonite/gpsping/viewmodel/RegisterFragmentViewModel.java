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
public class RegisterFragmentViewModel extends BaseFragmentViewModel {
    public BindableString userName = new BindableString();
    public BindableString email = new BindableString();
    public BindableString password = new BindableString();
    public BindableString passwordRepeat = new BindableString();

    public ObservableField<String> userNameError = new ObservableField<>();
    public ObservableField<String> emailError = new ObservableField<>();
    public ObservableField<String> passwordError = new ObservableField<>();
    public ObservableField<String> passwordRepeatError = new ObservableField<>();

    public TextView.OnEditorActionListener passwordDone = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                onRegisterClick(null);
                return true;
            }
            return false;
        }
    };

    public void onRegisterClick(View view) {
        if (validateData()) {
            if (actionListener != null) {
                actionListener.onRegister();
            }
        }
    }

    private ActionListener actionListener;

    public ActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public interface ActionListener {
        void onRegister();
    }

    private boolean validateData() {
        if (TextUtils.isEmpty(userName.get())) {
            userNameError.set(getContext().getString(R.string.loginCanNotBeEmpty));
            return false;
        }
        userNameError.set(null);

        if (TextUtils.isEmpty(email.get())) {
            emailError.set(getContext().getString(R.string.emailCanNotBeEmpty));
            return false;
        }
        emailError.set(null);

        if (TextUtils.isEmpty(password.get())) {
            passwordError.set(getContext().getString(R.string.passwordCanNotBeEmpty));
            return false;
        }
        passwordError.set(null);

        if (!password.get().equals(passwordRepeat.get())) {
            passwordRepeatError.set(getContext().getString(R.string.passwordNotConfirmed));
            return false;
        }
        passwordRepeatError.set(null);
        return true;
    }
}
