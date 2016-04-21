package no.appsonite.gpsping.services.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import no.appsonite.gpsping.Application;
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
            if (type != null) {
                switch (type) {
                    case "friend_request":
                        Friend friend = ApiFactory.getGson().fromJson(data.getString("data"), Friend.class);
                        showNotify(getString(R.string.PUSH_MESSSAGE_FRIEND_REQUEST, friend.getName()), getString(R.string.app_name),
                                PendingIntent.getActivity(this, REQUEST_FRIENDS, MainActivity.getFriendsIntent(), PendingIntent.FLAG_UPDATE_CURRENT));
                        break;
                    case "device_is_froze":
//                        playStandSound();
                        showNotify(getString(R.string.PUSH_MESSSAGE_CHECK_FOR_STAND), getString(R.string.app_name),
                                PendingIntent.getActivity(this, 1, new Intent(Application.getContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playStandSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(Application.getContext(), R.raw.bleep);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
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