package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.text.TextUtils;

import io.realm.Realm;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.api.content.Profile;
import no.appsonite.gpsping.db.RealmTracker;

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


    public void onSaveClick() {
        if (validateData()) {
            LoginAnswer loginAnswer = AuthHelper.getCredentials();
            loginAnswer.setUser(this.profile.get());
            AuthHelper.putCredentials(loginAnswer);
        }
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        Profile profile = AuthHelper.getCredentials().getUser();
        this.profile.set(profile);
    }

    public void logout() {
        AuthHelper.clearCredentials();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.clear(RealmTracker.class);
        realm.commitTransaction();
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
        return true;
    }
}
