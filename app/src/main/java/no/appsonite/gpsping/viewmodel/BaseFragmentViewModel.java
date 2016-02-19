package no.appsonite.gpsping.viewmodel;

import android.content.Context;

import java.io.Serializable;

import io.realm.Realm;
import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.utils.ErrorCode;
import no.appsonite.gpsping.utils.RxBus;
import no.appsonite.gpsping.utils.Utils;
import no.appsonite.gpsping.utils.bus.LogoutEvent;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 23.12.2015
 */
public class BaseFragmentViewModel implements Serializable {

    public BaseFragmentViewModel() {
    }

    public Context getContext() {
        return Application.getContext();
    }

    public void onViewCreated() {

    }

    public void onDestroyView() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onModelAttached() {

    }

    public <T extends ApiAnswer> Observable<T> execute(Observable<T> observable) {
        Observable<T> result = observable.cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        return result.flatMap(new Func1<T, Observable<T>>() {
            @Override
            public Observable<T> call(T t) {
                if (t.isError()) {
                    return parseError(t);
                }
                return Observable.just(t);
            }
        });
    }

    private Observable parseError(ApiAnswer error) {
        long code = error.getCode().get();
        if (code == ErrorCode.INVALID_AUTH_COOKIE || code == ErrorCode.INVALID_AUTH_COOKIE_USER) {
            logout();
            return Observable.just(error);
        }
        String errorText = error.getError().get();
        try {
            String packageName = Application.getContext().getPackageName();
            int resId = Application.getContext().getResources().getIdentifier("error_code_" + code, "string", packageName);
            errorText = Application.getContext().getString(resId);
        } catch (Exception ignore) {

        }
        return Observable.error(new Throwable(errorText));
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
                forceLogout();
            }

            @Override
            public void onNext(ApiAnswer apiAnswer) {
                forceLogout();
            }
        });
        return observable;
    }

    private void forceLogout() {
        AuthHelper.clearCredentials();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.clear(RealmTracker.class);
        realm.commitTransaction();
        RxBus.getInstance().post(new LogoutEvent());
    }
}
