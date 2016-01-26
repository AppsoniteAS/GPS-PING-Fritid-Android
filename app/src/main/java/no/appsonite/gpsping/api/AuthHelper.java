package no.appsonite.gpsping.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.api.content.NonceAnswer;
import no.appsonite.gpsping.api.content.Profile;
import no.appsonite.gpsping.api.content.RegisterAnswer;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class AuthHelper {
    private static final String PREFS_NAME = "register";

    public static Observable<LoginAnswer> register(final Profile profile) {
        final ApiService apiService = ApiFactory.getService();
        return apiService.getNonce().
                flatMap(new Func1<NonceAnswer, Observable<RegisterAnswer>>() {
                    @Override
                    public Observable<RegisterAnswer> call(NonceAnswer nonceAnswer) {
                        return apiService.registerUser(
                                profile.username.get(),
                                profile.password.get(),
                                profile.email.get(),
                                profile.firstName.get(),
                                profile.lastName.get(),
                                profile.displayname.get(),
                                nonceAnswer.getNonce().get()
                        );
                    }
                })
                .flatMap(new Func1<RegisterAnswer, Observable<LoginAnswer>>() {
                    @Override
                    public Observable<LoginAnswer> call(RegisterAnswer registerAnswer) {
                        return apiService.login(profile.username.get(), profile.password.get());
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<ApiAnswer> updateUser(final Profile profile) {
        final ApiService apiService = ApiFactory.getService();
        HashMap<String, String> params = new HashMap<>();
        params.put("first_name", profile.firstName.get());
        params.put("last_name", profile.lastName.get());
        return Observable.from(params.entrySet())
                .flatMap(new Func1<Map.Entry<String, String>, Observable<ApiAnswer>>() {
                    @Override
                    public Observable<ApiAnswer> call(Map.Entry<String, String> entry) {
                        return apiService.updateUser(entry.getKey(), entry.getValue());
                    }
                }).flatMap(new Func1<ApiAnswer, Observable<ApiAnswer>>() {
                    @Override
                    public Observable<ApiAnswer> call(ApiAnswer apiAnswer) {
                        if (apiAnswer.isError()) {
                            return Observable.error(new Throwable(apiAnswer.getError().get()));
                        }
                        return Observable.just(apiAnswer);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<LoginAnswer> login(String userName, String password) {
        final ApiService apiService = ApiFactory.getService();
        return apiService.login(userName, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static void putCredentials(LoginAnswer profile) {
        Context context = Application.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("credentials", ApiFactory.getGson().toJson(profile)).apply();
    }

    public static LoginAnswer getCredentials() {
        Context context = Application.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String serializedCredentials = sharedPreferences.getString("credentials", "");
        if (TextUtils.isEmpty(serializedCredentials))
            return null;
        LoginAnswer loginAnswer = ApiFactory.getGson().fromJson(serializedCredentials, LoginAnswer.class);
        if (loginAnswer != null) {
            loginAnswer.getUser().init();
        }
        return loginAnswer;
    }

    public static void clearCredentials() {
        Context context = Application.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}
