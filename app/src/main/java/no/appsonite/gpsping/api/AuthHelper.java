package no.appsonite.gpsping.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import no.appsonite.gpsping.Application;
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
                                profile.displayname.get(),
                                nonceAnswer.getNonce()
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
        return ApiFactory.getGson().fromJson(serializedCredentials, LoginAnswer.class);
    }

    public static void clearCredentials() {
        Context context = Application.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}
