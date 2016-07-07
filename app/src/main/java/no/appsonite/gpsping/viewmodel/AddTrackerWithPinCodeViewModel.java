package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableBoolean;
import android.text.TextUtils;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.utils.ObservableString;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created: Belozerov Sergei
 * E-mail: belozerow@gmail.com
 * Company: APPGRANULA LLC
 * Date: 05/07/16
 */
public class AddTrackerWithPinCodeViewModel extends BaseFragmentViewModel {
    public ObservableString trackerIMEI = new ObservableString();
    public ObservableString trackerIMEIError = new ObservableString();
    public ObservableString trackerLast4DigitsOfNumber = new ObservableString();
    public ObservableString trackerLast4DigitsOfNumberError = new ObservableString();
    public ObservableBoolean afterReg = new ObservableBoolean(false);

    public boolean isTrackerValid() {
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

    public Observable<Boolean> addTracker() {
        if (isTrackerValid()) {
            return execute(ApiFactory.getService().bindTracker(trackerIMEI.get(), trackerLast4DigitsOfNumber.get()))
                    .flatMap(new Func1<ApiAnswer, Observable<Boolean>>() {
                        @Override
                        public Observable<Boolean> call(ApiAnswer apiAnswer) {
                            return Observable.just(true);
                        }
                    });
        }
        return null;
    }
}
