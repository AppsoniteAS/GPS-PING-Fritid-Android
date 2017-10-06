package no.appsonite.gpsping.viewmodel;

import android.app.Activity;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.db.Geofence;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.SMS;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.ObservableString;
import no.appsonite.gpsping.utils.TrackingHistoryTime;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class EditTrackerFragmentViewModel extends BaseFragmentSMSViewModel {
    //fritid
//    public static final String TRACCAR_IP = "52.19.58.234";
    public static final String TRACCAR_IP = "54.77.4.166";
    //industri
//    public static final String TRACCAR_IP = "52.51.183.199";
    public ObservableField<Tracker> tracker = new ObservableField<>();
    public ObservableString nameError = new ObservableString();
    public ObservableBoolean sleepModeVisible = new ObservableBoolean();
    public ObservableBoolean geofenceVisibility = new ObservableBoolean(false);
    public ObservableString historyTime = new ObservableString();

    public ObservableString yards = new ObservableString();
    public ObservableString phone = new ObservableString();
    public ObservableString yardsError = new ObservableString();

    @Override
    public void onModelAttached() {
        Realm realm = Realm.getDefaultInstance();
        requestActiveTracker(realm);

        setSleepModeVisibility();
        initGeofenceVisibility(realm);
        initTrackingHistory();
        realm.close();
    }

    private void requestActiveTracker(Realm realm) {
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.get().imeiNumber.get()).findFirst();
        if (realmTracker != null) {
            tracker.set(new Tracker(realmTracker));
        }
    }

    private void setSleepModeVisibility() {
        switch (Tracker.Type.valueOf(tracker.get().type.get())) {
            case LK209:
            case VT600:
            case LK330:
            case S1:
            case A9:
                sleepModeVisible.set(false);
                break;
            default:
                sleepModeVisible.set(true);
                break;
        }
    }

    private void initGeofenceVisibility(Realm realm) {
        if (Tracker.Type.TK_STAR_PET.toString().equals(tracker.get().type.get())) {
            geofenceVisibility.set(true);
            initGeofence(realm);
        }
    }

    private void initTrackingHistory() {
        historyTime.set(TrackingHistoryTime.getHTrackingHistory());
    }

    private void initGeofence(Realm realm) {
        Geofence geofence = realm.where(Geofence.class).findFirst();
        if (geofence != null) {
            yards.set(geofence.getYards());
            phone.set(tracker.get().trackerNumber.get());
        }
    }

    public Observable<Boolean> updateTracker(Activity activity) {
        if (validateData()) {
            return editTracker(activity);
        }
        return null;
    }

    private boolean validateData() {
        if (TextUtils.isEmpty(tracker.get().trackerName.get())) {
            nameError.set(getContext().getString(R.string.trackerNameCanNotBeEmpty));
            return false;
        }
        nameError.set(null);
        return true;
    }

    private Observable<Boolean> editTracker(Activity activity) {
        Observable<SMS> observable;
        if (isBikeTracker()) {
            observable = Observable.just(new SMS());
        } else {
            switch (Tracker.Type.valueOf(tracker.get().type.get())) {
                case LK209:
                case VT600:
                case LK330:
                case S1:
                case A9:
                    observable = Observable.just(new SMS());
                    break;
                default:
                    observable = sendSmses(activity, tracker.get().getUpdateSms());
                    break;
            }
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

    public boolean isBikeTracker() {
        return Tracker.Type.TK_BIKE.toString().equalsIgnoreCase(tracker.get().type.get()) ||
                Tracker.Type.TK_STAR_BIKE.toString().equalsIgnoreCase(tracker.get().type.get());
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

    private void saveTrackerToDb() {
        Realm realm = Realm.getDefaultInstance();
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.get().imeiNumber.get()).findFirst();
        realm.beginTransaction();
        RealmTracker.initWithTracker(realmTracker, tracker.get());
        realm.copyToRealm(realmTracker);
        realm.commitTransaction();
        realm.close();
    }

    public Observable<SMS> checkBattery(Activity activity) {
        ArrayList<SMS> smses = new ArrayList<>();
        smses.add(new SMS(tracker.get().trackerNumber.get(), "G123456#"));
        return sendSmses(activity, smses);
    }

    public Observable<SMS> switchState(Activity activity) {
        if (tracker.get().isRunning.get()) {
            return stopTracker(activity);
        } else {
            return startTracker(activity);
        }
    }

    private Observable<SMS> stopTracker(Activity activity) {
        Tracker tracker = this.tracker.get();
        setTrackerRunning(tracker, false);
        String message;
        switch (Tracker.Type.valueOf(tracker.type.get())) {
            case TK_STAR_PET:
            case TK_STAR:
                message = "nogprs123456";
                break;
            case VT600:
                message = "W000000,013,0";
                break;
            case LK209:
            case LK330:
                message = "gpsloc123456,1";
                break;
            case S1:
            case A9:
                message = "pw,123456,upload,000#";
                break;
            default:
                message = "Notn123456";
                break;
        }
        return sendSmsToTracker(tracker, message, activity);
    }

    private Observable<SMS> startTracker(Activity activity) {
        Tracker tracker = this.tracker.get();
        setTrackerRunning(tracker, true);
        String message;
        switch (Tracker.Type.valueOf(tracker.type.get())) {
            case TK_BIKE:
            case TK_STAR_BIKE:
                message = String.format("Upload123456 %s", tracker.getRepeatTime());
                break;
            case TK_STAR_PET:
            case TK_STAR:
                message = "gprs123456";
                break;
            case LK209:
            case LK330: {
                long timeHours = tracker.getRepeatTime() / 60 / 60;
                String formattedTime = timeHours + "";
                while (formattedTime.length() < 2) {
                    formattedTime = "0" + formattedTime;
                }
                message = String.format("DW005,%s", formattedTime);
                break;
            }
            case VT600: {
                String formattedTime = tracker.getRepeatTime() / 10 + "";
                while (formattedTime.length() < 5) {
                    formattedTime = "0" + formattedTime;
                }
                message = String.format("W00000,014,%s", formattedTime);
                break;
            }
            case S1:
            case A9:
                String repeatTime = tracker.getRepeatTime() + "";
                if (repeatTime.length() == 2) {
                    repeatTime = "0" + repeatTime;
                }
                message = String.format("pw,123456,upload,%s#", repeatTime);
                break;
            default:
                message = String.format("T%ss***n123456", tracker.getRepeatTime());
                break;
        }
        return sendSmsToTracker(tracker, message, activity);
    }

    private void setTrackerRunning(Tracker tracker, boolean isRunning) {
        this.tracker.get().isRunning.set(isRunning);
        Realm realm = Realm.getDefaultInstance();
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.imeiNumber.get()).findFirst();
        realm.beginTransaction();
        realmTracker.setIsRunning(isRunning);
        realm.commitTransaction();
    }

    private Observable<SMS> sendSmsToTracker(Tracker tracker, String message, Activity activity) {
        ArrayList<SMS> smses = new ArrayList<>();
        smses.add(new SMS(tracker.trackerNumber.get(), message));
        return sendSmses(activity, smses).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .last()
                .cache();
    }

    public void saveTrackingHistory() {
        TrackingHistoryTime.saveTrackingHistory(historyTime.get());
    }

    public Observable<SMS> switchGeofence(Activity activity) {
        if (!validateGeofence())
            return null;
        saveGeofence();
        if (tracker.get().isGeofenceRunning.get()) {
            return stopGeofence(activity);
        } else {
            return startGeofence(activity);
        }
    }

    private boolean validateGeofence() {
        if (TextUtils.isEmpty(yards.get())) {
            yardsError.set(getContext().getString(R.string.yardsCanNotBeEmpty));
            return false;
        }
        yardsError.set(null);
        return true;
    }

    private void saveGeofence() {
        Realm realm = Realm.getDefaultInstance();
        Geofence geofence = realm.where(Geofence.class).findFirst();
        realm.beginTransaction();
        if (geofence == null) {
            geofence = realm.createObject(Geofence.class);
        }
        geofence.setYards(yards.get());
        realm.commitTransaction();
        realm.close();
    }

    private Observable<SMS> stopGeofence(Activity activity) {
        Tracker tracker = this.tracker.get();
        setTrackerGeofenceRunning(tracker, false);
        ArrayList<SMS> smses = new ArrayList<>();

        switch (Tracker.Type.valueOf(tracker.type.get())) {
            case VT600:
                smses.add(new SMS(tracker.trackerNumber.get(), String.format("W000000,006,0", 0)));
                break;
            default:
                smses.add(new SMS(tracker.trackerNumber.get(), "move123456 0"));
                break;
        }
        return sendSmses(activity, smses).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .last()
                .cache();
    }

    private Observable<SMS> startGeofence(Activity activity) {
        Tracker tracker = this.tracker.get();
        setTrackerGeofenceRunning(tracker, true);
        ArrayList<SMS> smses = new ArrayList<>();

        switch (Tracker.Type.valueOf(tracker.type.get())) {
            case VT600:
                String yardValue = yards.get();
                String[] yardsArray = Application.getContext().getResources().getStringArray(R.array.geofenceValues);
                int index = 0;
                for (; index < yardsArray.length; index++) {
                    if (yardsArray[index].equals(yardValue)) {
                        break;
                    }
                }
                String[] yardsKey = Application.getContext().getResources().getStringArray(R.array.geofenceKeys);
                String key = yardsKey[index];
                smses.add(new SMS(tracker.trackerNumber.get(), String.format("W000000,006,%s", key)));
                break;
            default:
                smses.add(new SMS(tracker.trackerNumber.get(), String.format("move123456 %s", yards.get())));
                break;
        }
        //smses.add(new SMS(tracker.trackerNumber.get(), String.format("admin123456 %s", activeTracker.get().trackerNumber.get())));

        return sendSmses(activity, smses).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .last()
                .cache();
    }

    private void setTrackerGeofenceRunning(Tracker tracker, boolean isRunning) {
        this.tracker.get().isGeofenceRunning.set(isRunning);
        Realm realm = Realm.getDefaultInstance();
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("imeiNumber", tracker.imeiNumber.get()).findFirst();
        realm.beginTransaction();
        realmTracker.setIsGeofenceRunning(isRunning);
        realm.commitTransaction();
    }

//    public Observable<SMS> resetTracker(final Activity activity) {
//        return resolveAddress().flatMap(new Func1<String, Observable<SMS>>() {
//            @Override
//            public Observable<SMS> call(String address) {
//                return sendSmses(activity, tracker.get().getResetSms(address));
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .last()
//                .cache();
//    }

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
}
