package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.text.TextUtils;
import android.view.View;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.utils.BindableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class ProfileFragmentViewModel extends BaseFragmentViewModel {
    public BindableString username = new BindableString();
    public BindableString fullName = new BindableString();
    public BindableString email = new BindableString();

    public ObservableField<String> usernameError = new ObservableField<>();
    public ObservableField<String> fullNameError = new ObservableField<>();
    public ObservableField<String> emailError = new ObservableField<>();

    private ActionsListener actionsListener;

    public void onSaveClick(View v) {
        if (validateData()) {
            if (actionsListener != null) {
                actionsListener.onSave();
            }
        }
    }

    public void setActionsListener(ActionsListener actionsListener) {
        this.actionsListener = actionsListener;
    }

    public void logout() {

    }

    public interface ActionsListener {
        void onSave();
    }

    private boolean validateData() {
        if (TextUtils.isEmpty(username.get())) {
            usernameError.set(getContext().getString(R.string.loginCanNotBeEmpty));
            return false;
        }
        usernameError.set(null);
        if (TextUtils.isEmpty(email.get())) {
            emailError.set(getContext().getString(R.string.emailCanNotBeEmpty));
            return false;
        }
        emailError.set(null);
        return true;
    }
}
