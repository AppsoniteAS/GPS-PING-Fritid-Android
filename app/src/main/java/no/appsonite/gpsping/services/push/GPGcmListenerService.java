package no.appsonite.gpsping.services.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.activities.MainActivity;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.model.Friend;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 29.01.2016
 */
public class GPGcmListenerService extends GcmListenerService {
    private static final int REQUEST_FRIENDS = 7;
    private static int notification_id = 1;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        try {
            String type = data.getString("type");
            if ("friend_request".equals(type)) {
                Friend friend = ApiFactory.getGson().fromJson(data.getString("data"), Friend.class);
                showNotify(getString(R.string.friendShipRequestNotifyMessage), friend.getName(),
                        PendingIntent.getActivity(this, REQUEST_FRIENDS, MainActivity.getFriendsIntent(), PendingIntent.FLAG_UPDATE_CURRENT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNotify(String message, String title, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(message)
                .setAutoCancel(true);
        NotificationManager nm = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(notification_id++, builder.build());
    }
}