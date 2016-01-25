package no.appsonite.gpsping.viewmodel;

import android.app.Activity;
import android.databinding.ObservableField;
import android.text.TextUtils;

import java.util.ArrayList;

import io.realm.Realm;
import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.SMS;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.ObservableString;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class AddTrackerFragmentViewModel extends BaseFragmentSMSViewModel {

    public ObservableField<Tracker> tracker = new ObservableField<>();
    public ObservableString nameError = new ObservableString();
    public ObservableString imeiNumberError = new ObservableString();
    public ObservableString trackerNumberError = new ObservableString();
    public ObservableString buttonText = new ObservableString();

    public Observable<RealmTracker> addTracker(Activity activity) {
        if (validateData()) {
            return sendSmses(activity, getSMSes())
                    .last()
                    .flatMap(new Func1<SMS, Observable<ApiAnswer>>() {
                        @Override
                        public Observable<ApiAnswer> call(SMS sms) {
                            LoginAnswer loginAnswer = AuthHelper.getCredentials();
                            return ApiFactory.getService().addTracker(loginAnswer.getCookie(),
                                    tracker.get().trackerName.get(),
                                    Long.parseLong(tracker.get().imeiNumber.get()),
                                    Long.parseLong(tracker.get().trackerNumber.get()),
                                    tracker.get().getRepeatTime(),
                                    tracker.get().checkForStand.get(),
                                    tracker.get().type.toString()
                            ).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread());
                        }
                    })
                    .flatMap(new Func1<ApiAnswer, Observable<RealmTracker>>() {
                        @Override
                        public Observable<RealmTracker> call(ApiAnswer apiAnswer) {
                            Realm realm = Realm.getInstance(getContext());
                            RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.get().imeiNumber.get()).findFirst();
                            realm.beginTransaction();
                            if (realmTracker == null) {
                                realmTracker = realm.createObject(RealmTracker.class);
                            }
                            RealmTracker.initWithTracker(realmTracker, tracker.get());
                            realm.copyToRealm(realmTracker);
                            realm.commitTransaction();
                            return Observable.just(realmTracker);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return null;
    }

    private ArrayList<SMS> getSMSes() {
        ArrayList<SMS> smses = new ArrayList<>();
        String trackerNumber = tracker.get().trackerNumber.get();
        smses.add(new SMS(trackerNumber, "Begin123456"));
        smses.add(new SMS(trackerNumber, "gprs123456"));
        smses.add(new SMS(trackerNumber, "apn123456 netcom"));
        switch (tracker.get().type.get()) {
            case TK_STAR_PET:
                break;
            case TK_ANYWHERE:
                smses.add(new SMS(trackerNumber, "adminip123456 46.137.82.251 5000"));
                break;
            case TK_STAR:
                smses.add(new SMS(trackerNumber, "adminip123456 46.137.82.251 5013"));
                break;
        }
        smses.add(new SMS(trackerNumber, "sleep123456 off"));
        return smses;
    }


    @Override
    public void onModelAttached() {
        Realm realm = Realm.getInstance(Application.getContext());
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.get().imeiNumber.get()).findFirst();
        buttonText.set(Application.getContext().getString(realmTracker == null ? R.string.addTracker : R.string.update));
    }

    private boolean validateData() {
        if (TextUtils.isEmpty(tracker.get().trackerName.get())) {
            nameError.set(getContext().getString(R.string.trackerNameCanNotBeEmpty));
            return false;
        }
        nameError.set(null);

        if (TextUtils.isEmpty(tracker.get().imeiNumber.get())) {
            imeiNumberError.set(getContext().getString(R.string.imeiCanNotBeEmpty));
            return false;
        }
        imeiNumberError.set(null);

        if (TextUtils.isEmpty(tracker.get().trackerNumber.get())) {
            trackerNumberError.set(getContext().getString(R.string.trackerNumberCanNotBeEmpty));
            return false;
        }
        trackerNumberError.set(null);
        return true;
    }
}
