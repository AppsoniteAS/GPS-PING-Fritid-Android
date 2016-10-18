package no.appsonite.gpsping.model;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created: Belozerov Sergei
 * E-mail: belozerow@gmail.com
 * Company: APPGRANULA LLC
 * Date: 04/07/16
 */
public class Migration2 implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        RealmObjectSchema trackerSchema = schema.get("RealmTracker");
        if (oldVersion == 0) {
            trackerSchema.addField("ledActive", boolean.class)
                    .addField("shockAlarmActive", boolean.class)
                    .addField("shockFlashActive", boolean.class);
        }
        if (oldVersion == 1) {
            trackerSchema.addField("sleepMode", boolean.class);
        }
    }
}
