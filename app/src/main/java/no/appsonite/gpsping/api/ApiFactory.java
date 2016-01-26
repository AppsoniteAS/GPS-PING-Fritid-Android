package no.appsonite.gpsping.api;

import android.databinding.ObservableInt;
import android.databinding.ObservableLong;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.api.typeadapters.ObservableBooleanTypeAdapter;
import no.appsonite.gpsping.api.typeadapters.ObservableIntTypeAdapter;
import no.appsonite.gpsping.api.typeadapters.ObservableLongTypeAdapter;
import no.appsonite.gpsping.api.typeadapters.ObservableStringTypeAdapter;
import no.appsonite.gpsping.utils.ObservableBoolean;
import no.appsonite.gpsping.utils.ObservableString;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class ApiFactory {
    private static final String BASE_URL = "http://192.168.139.201/api/";
    private static final int CONNECT_TIMEOUT = 15;
    private static final int WRITE_TIMEOUT = 60;
    private static final int TIMEOUT = 15;

    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static Retrofit INSTANCE = null;

    static {
        CLIENT.setConnectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        CLIENT.setWriteTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        CLIENT.setReadTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @NonNull
    public static ApiService getService() {
        return getRetrofit().create(ApiService.class);
    }

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder()
                .serializeNulls()
//                .setExclusionStrategies(new ExclusionStrategy() {
//                    @Override
//                    public boolean shouldSkipField(FieldAttributes f) {
//                        return f.hasModifier(Modifier.TRANSIENT);
//                                || f.getDeclaredClass().equals(ModelAdapter.class);
//                    }
//
//                    @Override
//                    public boolean shouldSkipClass(Class<?> clazz) {
//                        return false;
//                    }
//                })
                .registerTypeAdapter(ObservableString.class, new ObservableStringTypeAdapter())
                .registerTypeAdapter(ObservableBoolean.class, new ObservableBooleanTypeAdapter())
                .registerTypeAdapter(ObservableLong.class, new ObservableLongTypeAdapter())
                .registerTypeAdapter(ObservableInt.class, new ObservableIntTypeAdapter());

        return builder.create();
    }

    @NonNull
    private static Retrofit getRetrofit() {
        if (INSTANCE != null) {
            return INSTANCE;
        } else {
            INSTANCE = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .client(CLIENT)
                    .build();
            if (INSTANCE.client().interceptors().size() == 0) {
                INSTANCE.client().interceptors().add(new CookieInterceptor());
            }
            return INSTANCE;
        }
    }

    public static class CookieInterceptor implements Interceptor {
        private static final String TAG = "API";

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            LoginAnswer loginAnswer = AuthHelper.getCredentials();
            if (loginAnswer != null) {
                HttpUrl url = request.httpUrl().newBuilder().addQueryParameter("cookie", loginAnswer.getCookie().get()).build();
                request = request.newBuilder().url(url).build();
            }
            Response response = chain.proceed(request);
//            Log.d(TAG, response.body().string());
            return response;
        }
    }
}
