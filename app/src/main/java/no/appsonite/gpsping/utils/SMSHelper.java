package no.appsonite.gpsping.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.Toast;

import no.appsonite.gpsping.model.SMS;
import rx.Observable;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 25.01.2016
 */
public class SMSHelper {
    public static final int REQUEST_SMS_SENT = 12;
    public static final String EXTRA_NUMBER = "extra_number";
    public static final String EXTRA_MSG = "extra_msg";
    private static final boolean DEBUG = false;

    public static void sendSMS(Activity activity, SMS sms) {
        Intent intent = new Intent(activity, activity.getClass());
        intent.putExtra(EXTRA_MSG, sms.getMessage());
        intent.putExtra(EXTRA_NUMBER, sms.getNumber());
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity, REQUEST_SMS_SENT, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (pendingIntent != null) {
            if (!DEBUG) {
                SmsManager.getDefault().sendTextMessage(sms.getNumber(), null, sms.getMessage(), pendingIntent, null);
            } else {
                if (sms.getMessage() != null) {
//                    Toast.makeText(activity, sms.getMessage(), Toast.LENGTH_LONG).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }, 300);
            }

        }
    }
}
