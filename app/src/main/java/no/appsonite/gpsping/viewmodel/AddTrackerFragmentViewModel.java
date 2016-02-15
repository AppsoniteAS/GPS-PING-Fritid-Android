package no.appsonite.gpsping.viewmodel;

import android.app.Activity;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.TextUtils;

import java.util.ArrayList;

import io.realm.Realm;
import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
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
    private static final String TRACCAR_IP = "52.49.162.223";
    public ObservableField<Tracker> tracker = new ObservableField<>();
    public ObservableString nameError = new ObservableString();
    public ObservableString imeiNumberError = new ObservableString();
    public ObservableString trackerNumberError = new ObservableString();
    public ObservableBoolean editMode = new ObservableBoolean();

    public Observable<Boolean> saveTracker(Activity activity) {
        if (validateData()) {
            if (!editMode.get()) {
                return addNewTracker(activity);
            } else {
                return editTracker(activity);
            }
        }
        return null;
    }

    private Observable<String> resolveAddress() {
        return Observable.just(TRACCAR_IP).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
        //http://appgranula.mooo.com
//        return Observable.defer(new Func0<Observable<String>>() {
//            @Override
//            public Observable<String> call() {
//                String ipAddress;
//                InetAddress address = null;
//                try {
//                    address = InetAddress.getByName("appgranula.mooo.com");
//                    ipAddress = address.getHostAddress();
//                } catch (UnknownHostException e) {
//                    ipAddress = "null";
//                }
//                return Observable.just(ipAddress).subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread());
//            }
//        });
    }

    private Observable<Boolean> editTracker(Activity activity) {
        return ApiFactory.getService().updateTracker(
                tracker.get().imeiNumber.get(),
                tracker.get().trackerName.get(),
                tracker.get().getRepeatTime(),
                tracker.get().checkForStand.get())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<ApiAnswer, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(ApiAnswer apiAnswer) {
                        Realm realm = Realm.getInstance(getContext());
                        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.get().imeiNumber.get()).findFirst();
                        realm.beginTransaction();
                        RealmTracker.initWithTracker(realmTracker, tracker.get());
                        realm.copyToRealm(realmTracker);
                        realm.commitTransaction();
                        realm.close();
                        return Observable.just(false);
                    }
                });
    }

    private Observable<Boolean> addNewTracker(final Activity activity) {
        return resolveAddress().flatMap(new Func1<String, Observable<SMS>>() {
            @Override
            public Observable<SMS> call(String address) {
                return sendSmses(activity, getSMSes(address));
            }
        })
                .last()
                .cache()
                .flatMap(new Func1<SMS, Observable<ApiAnswer>>() {
                    @Override
                    public Observable<ApiAnswer> call(SMS sms) {
                        return ApiFactory.getService().addTracker(
                                tracker.get().trackerName.get(),
                                tracker.get().imeiNumber.get(),
                                tracker.get().trackerNumber.get(),
                                tracker.get().getRepeatTime(),
                                tracker.get().checkForStand.get(),
                                tracker.get().type.get()
                        ).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .flatMap(new Func1<ApiAnswer, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(ApiAnswer apiAnswer) {
                        Realm realm = Realm.getInstance(getContext());
                        realm.beginTransaction();
                        RealmTracker realmTracker = realm.createObject(RealmTracker.class);
                        RealmTracker.initWithTracker(realmTracker, tracker.get());
                        realm.copyToRealm(realmTracker);
                        realm.commitTransaction();
                        realm.close();
                        return Observable.just(true);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private ArrayList<SMS> getSMSes(String address) {
        ArrayList<SMS> smses = new ArrayList<>();
        String trackerNumber = tracker.get().trackerNumber.get();
        smses.add(new SMS(trackerNumber, "Begin123456"));
        smses.add(new SMS(trackerNumber, "gprs123456"));
        smses.add(new SMS(trackerNumber, "apn123456 netcom"));
        switch (Tracker.Type.valueOf(tracker.get().type.get())) {
            case TK_STAR_PET:
                break;
            case TK_ANYWHERE:
                //46.137.82.251
                smses.add(new SMS(trackerNumber, String.format("adminip123456 %s 5000", address)));
                break;
            case TK_STAR:
                smses.add(new SMS(trackerNumber, String.format("adminip123456 %s 5013", address)));
                break;
        }
        smses.add(new SMS(trackerNumber, "sleep123456 off"));
        return smses;
    }


    @Override
    public void onModelAttached() {
        Realm realm = Realm.getInstance(Application.getContext());
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.get().imeiNumber.get()).findFirst();
        editMode.set(realmTracker != null);
        realm.close();
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
