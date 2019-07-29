package no.appsonite.gpsping.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 29.01.2016
 */
public class PushHelper {

    private static final String PREF_PUSH_TOKEN = "pushToken";
    private static final String PREF_PUSH_SETTINGS = "Settings";

    private static Observable<String> getToken() {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                SharedPreferences prefs = Application.getContext().getSharedPreferences(PREF_PUSH_SETTINGS, Context.MODE_PRIVATE);
                String token = prefs.getString(PREF_PUSH_TOKEN, null);
                if (TextUtils.isEmpty(token)) {
                    InstanceID instanceID = InstanceID.getInstance(Application.getContext());
                    try {
                        token = instanceID.getToken(Application.getContext().getString(R.string.sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                        prefs.edit().putString(PREF_PUSH_TOKEN, token).apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return Observable.error(e);
                    }
                }
                return Observable.just(token);
            }
        });
    }

    public static void clearToken() {
        SharedPreferences prefs = Application.getContext().getSharedPreferences(PREF_PUSH_SETTINGS, Context.MODE_PRIVATE);
        prefs.edit().remove(PREF_PUSH_TOKEN).apply();
    }

    public static void sendPushToken() {
        getToken().flatMap(new Func1<String, Observable<ApiAnswer>>() {
            @Override
            public Observable<ApiAnswer> call(String token) {
                return ApiFactory.getService().registerGCM(Utils.getUniqueId(), token)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ApiAnswer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ApiAnswer apiAnswer) {
                        Log.d("PUSH", "push registered");
                    }
                });

    }
}
