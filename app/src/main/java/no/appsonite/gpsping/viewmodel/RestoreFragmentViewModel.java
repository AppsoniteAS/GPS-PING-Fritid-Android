package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.text.TextUtils;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.utils.ObservableString;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 29.01.2016
 */
public class RestoreFragmentViewModel extends BaseFragmentViewModel {
    public ObservableString email = new ObservableString();
    public ObservableField<String> emailError = new ObservableField<>();

    private boolean validateData() {
        if (TextUtils.isEmpty(email.get())) {
            emailError.set(getContext().getString(R.string.loginCanNotBeEmpty));
            return false;
        }
        emailError.set(null);
        return true;
    }

    public Observable<ApiAnswer> restorePassword() {
        if (validateData()) {
            return execute(ApiFactory.getService().restorePassword(email.get()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();
        }
        return null;
    }
}
