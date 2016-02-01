package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.text.TextUtils;

import io.realm.Realm;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.api.content.Profile;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.utils.Utils;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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


    public Observable<ApiAnswer> onSaveClick() {
        if (validateData()) {
            final LoginAnswer loginAnswer = AuthHelper.getCredentials();
            loginAnswer.setUser(this.profile.get());
            Observable<ApiAnswer> observable = AuthHelper.updateUser(this.profile.get()).cache();
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

        this.profile.set(profile);
    }

    public Observable<ApiAnswer> logout() {
        Observable<ApiAnswer> observable = ApiFactory.getService().unregisterGCM(Utils.getUniqueId())
                .cache()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<ApiAnswer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ApiAnswer apiAnswer) {
                AuthHelper.clearCredentials();
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.clear(RealmTracker.class);
                realm.commitTransaction();
            }
        });
        return observable;
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
