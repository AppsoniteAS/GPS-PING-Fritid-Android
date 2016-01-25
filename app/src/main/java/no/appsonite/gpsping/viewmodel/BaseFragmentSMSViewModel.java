package no.appsonite.gpsping.viewmodel;

import android.app.Activity;

import java.util.ArrayList;

import no.appsonite.gpsping.model.SMS;
import no.appsonite.gpsping.utils.SMSHelper;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 25.01.2016
 */
public class BaseFragmentSMSViewModel extends BaseFragmentViewModel {
    private PublishSubject<SMS> smsPublishSubject;

    protected PublishSubject<SMS> sendSms(Activity activity, SMS sms) {
        smsPublishSubject = PublishSubject.create();
        smsPublishSubject.subscribe(new Action1<SMS>() {
            @Override
            public void call(SMS sms) {
                smsPublishSubject.onCompleted();
            }
        });
        SMSHelper.sendSMS(activity, sms);
        return smsPublishSubject;
    }

    public void onNewSms(SMS sms) {
        if (smsPublishSubject != null) {
            smsPublishSubject.onNext(sms);
        }
    }

    public PublishSubject<SMS> sendSmses(final Activity activity, final ArrayList<SMS> smses) {
        smsPublishSubject = PublishSubject.create();
        smsPublishSubject.
                subscribe(new Action1<SMS>() {
                    @Override
                    public void call(SMS sms) {
                        smses.remove(sms);
                        if (smses.size() > 0) {
                            SMSHelper.sendSMS(activity, smses.get(0));
                        } else {
                            smsPublishSubject.onCompleted();
                        }
                    }
                });
        SMSHelper.sendSMS(activity, smses.get(0));
        return smsPublishSubject;
    }
}
