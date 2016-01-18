package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.text.TextUtils;
import android.view.View;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.model.Profile;

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

    private ActionsListener actionsListener;

    public void onSaveClick(View v) {
        if (validateData()) {
            if (actionsListener != null) {
                actionsListener.onSave();
            }
        }
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        Profile profile = new Profile();
        profile.email.set("hello@gmail.com");
        profile.username.set("JSilver");
        profile.fullName.set("John Silver");
        this.profile.set(profile);
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
        if (TextUtils.isEmpty(profile.get().username.get())) {
            usernameError.set(getContext().getString(R.string.loginCanNotBeEmpty));
            return false;
        }
        usernameError.set(null);
        if (TextUtils.isEmpty(profile.get().email.get())) {
            emailError.set(getContext().getString(R.string.emailCanNotBeEmpty));
            return false;
        }
        emailError.set(null);
        return true;
    }
}
