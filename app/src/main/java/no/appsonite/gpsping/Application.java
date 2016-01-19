package no.appsonite.gpsping;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 25.12.2015
 */
public class Application extends android.app.Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Application.context = this;
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
    }
}
