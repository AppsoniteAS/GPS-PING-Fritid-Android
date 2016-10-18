package no.appsonite.gpsping.viewmodel;

import android.app.Activity;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
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
    //fritid
    public static final String TRACCAR_IP = "52.19.58.234";
    //industri
//    public static final String TRACCAR_IP = "52.51.183.199";
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

    public boolean isBikeTracker() {
        return Tracker.Type.TK_BIKE.toString().equalsIgnoreCase(tracker.get().type.get());
    }

    private Observable<String> resolveAddress() {
//        if (BuildConfig.DEBUG) {
//            return Observable.defer(new Func0<Observable<String>>() {
//                @Override
//                public Observable<String> call() {
//                    String ipAddress;
//                    InetAddress address = null;
//                    try {
//                        address = InetAddress.getByName("appgranula.mooo.com");
//                        ipAddress = address.getHostAddress();
//                    } catch (UnknownHostException e) {
//                        ipAddress = "null";
//                    }
//                    return Observable.just(ipAddress).subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread());
//                }
//            });
//        } else {
        return Observable.just(TRACCAR_IP).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
//        }
        //http://appgranula.mooo.com

    }

    public Observable<Boolean> updateLed(Activity activity) {
        ArrayList<SMS> smses = new ArrayList<>();
        if (tracker.get().ledActive.get()) {
            smses.add(new SMS(tracker.get().trackerNumber.get(), "Led123456 on"));
        } else {
            smses.add(new SMS(tracker.get().trackerNumber.get(), "Led123456 off"));
        }
        return sendSmses(activity, smses).flatMap(new Func1<SMS, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(SMS sms) {
                saveTrackerToDb();

                return Observable.just(true);
            }
        });
    }

    public Observable<Boolean> updateShockAlarm(Activity activity) {
        ArrayList<SMS> smses = new ArrayList<>();
        if (tracker.get().shockAlarmActive.get()) {
            smses.add(new SMS(tracker.get().trackerNumber.get(), "shock123456"));
        } else {
            smses.add(new SMS(tracker.get().trackerNumber.get(), "sleep123456 time"));
        }
        return sendSmses(activity, smses).flatMap(new Func1<SMS, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(SMS sms) {
                saveTrackerToDb();
                return Observable.just(true);
            }
        });
    }

    public Observable<Boolean> updateShockFlashAlarm(Activity activity) {
        if (tracker.get().shockFlashActive.get()) {
            ArrayList<SMS> smses = new ArrayList<>();
            smses.add(new SMS(tracker.get().trackerNumber.get(), "LED123456 shock"));
            return sendSmses(activity, smses).flatMap(new Func1<SMS, Observable<Boolean>>() {
                @Override
                public Observable<Boolean> call(SMS sms) {
                    saveTrackerToDb();
                    return Observable.just(true);
                }
            });
        } else {
            saveTrackerToDb();
            return Observable.just(true).delay(200, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

    }

    public Observable<Boolean> updateSleepMode(Activity activity) {
        ArrayList<SMS> smses = new ArrayList<>();
        smses.add(new SMS(tracker.get().trackerNumber.get(), tracker.get().sleepMode.get() ? "sleep123456" : "sleep123456 off"));
        return sendSmses(activity, smses).flatMap(new Func1<SMS, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(SMS sms) {
                saveTrackerToDb();
                return Observable.just(true);
            }
        });
    }

    public Observable<SMS> checkBattery(Activity activity) {
        ArrayList<SMS> smses = new ArrayList<>();
        smses.add(new SMS(tracker.get().trackerNumber.get(), "G123456#"));
        return sendSmses(activity, smses);
    }

    private void saveTrackerToDb() {
        Realm realm = Realm.getDefaultInstance();
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.get().imeiNumber.get()).findFirst();
        realm.beginTransaction();
        RealmTracker.initWithTracker(realmTracker, tracker.get());
        realm.copyToRealm(realmTracker);
        realm.commitTransaction();
        realm.close();
    }

    private Observable<Boolean> editTracker(Activity activity) {
        Observable<SMS> observable;
        if (isBikeTracker()) {
            observable = Observable.just(new SMS());
        } else {
            observable = sendSmses(activity, tracker.get().getUpdateSms());
        }
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).last().cache().flatMap(new Func1<SMS, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(SMS sms) {
                        return execute(ApiFactory.getService().updateTracker(
                                tracker.get().imeiNumber.get(),
                                tracker.get().trackerName.get(),
                                tracker.get().getRepeatTime(),
                                tracker.get().checkForStand.get()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .flatMap(new Func1<ApiAnswer, Observable<Boolean>>() {
                                    @Override
                                    public Observable<Boolean> call(ApiAnswer apiAnswer) {
                                        Realm realm = Realm.getDefaultInstance();
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
                });
    }

    private Observable<Boolean> addNewTracker(final Activity activity) {
        return resolveAddress().flatMap(new Func1<String, Observable<SMS>>() {
            @Override
            public Observable<SMS> call(String address) {
                return sendSmses(activity, tracker.get().getResetSms(address));
            }
        })
                .last()
                .cache()
                .flatMap(new Func1<SMS, Observable<ApiAnswer>>() {
                    @Override
                    public Observable<ApiAnswer> call(SMS sms) {
                        return execute(ApiFactory.getService().addTracker(
                                tracker.get().trackerName.get(),
                                tracker.get().imeiNumber.get(),
                                tracker.get().trackerNumber.get(),
                                tracker.get().getRepeatTime(),
                                tracker.get().checkForStand.get(),
                                tracker.get().type.get()
                        )).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .flatMap(new Func1<ApiAnswer, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(ApiAnswer apiAnswer) {
                        Realm realm = Realm.getDefaultInstance();
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


    @Override
    public void onModelAttached() {
        Realm realm = Realm.getDefaultInstance();
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

    public Observable<SMS> resetTracker(final Activity activity) {
        return resolveAddress().flatMap(new Func1<String, Observable<SMS>>() {
            @Override
            public Observable<SMS> call(String address) {
                return sendSmses(activity, tracker.get().getResetSms(address));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .last()
                .cache();
    }
}
