package no.appsonite.gpsping.viewmodel;

import android.app.Activity;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import no.appsonite.gpsping.Application;
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
    public ObservableField<CharSequence> textLink = new ObservableField<>();
    public ObservableBoolean afterReg = new ObservableBoolean(false);
    public ObservableBoolean permission = new ObservableBoolean(false);
    public ObservableBoolean visibilityErrorPermission = new ObservableBoolean(false);
    public List<Tracker> trackers;

    public AddTrackerWithPinCodeViewModel() {
        addLinkText();
        permission.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable observable, int i) {
                if (permission.get()) {
                    visibilityErrorPermission.set(false);
                }
            }
        });
        trackers = new ArrayList<>();
        RealmTracker.requestTrackersFromRealm(trackers);
    }

    private void addLinkText() {
        SpannableString text = new SpannableString(Application.getContext().getString(R.string.addTrackerInfoCheckBox));
        text.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        textLink.set(text);
    }

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

        if (!permission.get()) {
            visibilityErrorPermission.set(true);
            return false;
        }
        visibilityErrorPermission.set(false);

        return true;
    }

    public Observable<Boolean> addTracker(final Activity activity) {
        if (isTrackerValid()) {
            if (trackerNoExist()) {
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
            } else {
                Toast.makeText(activity, activity.getString(R.string.thisTrackerAlreadyExists), Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    private boolean trackerNoExist() {
        for (Tracker tracker : trackers) {
            if (TextUtils.equals(tracker.imeiNumber.get(), trackerIMEI.get())) {
                return false;
            }
        }
        return true;
    }
}
