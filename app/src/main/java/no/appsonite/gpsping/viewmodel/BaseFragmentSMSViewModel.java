package no.appsonite.gpsping.viewmodel;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.model.SMS;
import no.appsonite.gpsping.utils.SMSHelper;
import rx.subjects.PublishSubject;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 25.01.2016
 */
public class BaseFragmentSMSViewModel extends BaseFragmentViewModel {
    private PublishSubject<SMS> smsPublishSubject;
    private static final CharSequence FINLAND_CODE = "358";

    public void onNewSms(SMS sms) {
        if (smsPublishSubject != null) {
            smsPublishSubject.onNext(sms);
        }
    }

    public PublishSubject<SMS> sendSmses(final Activity activity, final ArrayList<SMS> smses) {
        fixFinlandTracker(smses);
        smsPublishSubject = PublishSubject.create();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            return smsPublishSubject;
        }
        smsPublishSubject.
                subscribe(sms -> {
                    smses.remove(sms);
                    if (smses.size() > 0) {
                        SMSHelper.sendSMS(activity, smses.get(0));
                    } else {
                        smsPublishSubject.onCompleted();
                    }
                });
        SMSHelper.sendSMS(activity, smses.get(0));
        return smsPublishSubject;
    }

    private void fixFinlandTracker(ArrayList<SMS> smses) {
        LoginAnswer loginAnswer = AuthHelper.getCredentials();
        try {
            String phoneCode = loginAnswer.getUser().phoneCode.get().replaceAll("[^\\d.]", "");
            if (phoneCode.equals(FINLAND_CODE))
                for (SMS sms : smses) {
                    if (sms.getNumber().startsWith("00")) {
                        sms.setNumber(sms.getNumber().replaceFirst("00", "+"));
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
