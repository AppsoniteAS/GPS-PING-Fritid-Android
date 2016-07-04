package no.appsonite.gpsping;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;
import no.appsonite.gpsping.model.Migration1;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 25.12.2015
 */
public class Application extends MultiDexApplication {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Application.context = this;

        RealmConfiguration config = new RealmConfiguration.Builder(this).schemaVersion(1).build();
        Realm.setDefaultConfiguration(config);

        try {
            Realm.getInstance(config);
        } catch (RealmMigrationNeededException e) {
            Realm.migrateRealm(config, new Migration1());
        }

        Realm.setDefaultConfiguration(config);
    }
}
