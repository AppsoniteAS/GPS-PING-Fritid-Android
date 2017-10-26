package no.appsonite.gpsping.viewmodel;

import android.app.Activity;
import android.databinding.ObservableBoolean;
import android.text.TextUtils;

import java.util.ArrayList;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.ObservableString;
import rx.Observable;

/**
 * Created: Belozerov Sergei
 * E-mail: belozerow@gmail.com
 * Company: APPGRANULA LLC
 * Date: 05/07/16
 */
public class AddTrackerWithPinCodeViewModel extends BaseFragmentSMSViewModel {
    public ObservableString trackerIMEI = new ObservableString();
    public ObservableString trackerIMEIError = new ObservableString();
    public ObservableString trackerLast4DigitsOfNumber = new ObservableString();
    public ObservableString trackerLast4DigitsOfNumberError = new ObservableString();
    public ObservableBoolean afterReg = new ObservableBoolean(false);

    private boolean isTrackerValid() {
        if (TextUtils.isEmpty(trackerIMEI.get())) {
            trackerIMEIError.set(getContext().getString(R.string.imeiCanNotBeEmpty));
            return false;
        }
        trackerIMEIError.set(null);

        if (TextUtils.getTrimmedLength(trackerLast4DigitsOfNumber.get()) != 4) {
            trackerLast4DigitsOfNumberError.set(getContext().getString(R.string.last4DigitsError));
            return false;
        }
        trackerLast4DigitsOfNumberError.set(null);
        return true;
    }

    public Observable<Boolean> addTracker(final Activity activity) {
        if (isTrackerValid()) {
            return execute(ApiFactory.getService().bindTracker(trackerIMEI.get(), trackerLast4DigitsOfNumber.get()))
                    .flatMap(apiAnswer -> execute(ApiFactory.getService().getTrackers())).flatMap(trackersAnswer -> {
                        ArrayList<Tracker> trackers = trackersAnswer.getTrackers();
                        RealmTracker.sync(trackers);
                        return Observable.just(trackers);
                    }).flatMap(trackers -> {
                        for (Tracker tracker : trackers) {
                            if (TextUtils.equals(trackerIMEI.get(), tracker.imeiNumber.get())) {
                                return sendSmses(activity, tracker.getResetSms(EditTrackerFragmentViewModel.TRACCAR_IP));
                            }
                        }
                        Observable.error(new Throwable("No tracker"));
                        return null;
                    }).flatMap(sms -> Observable.just(true));
        }
        return null;
    }
}
