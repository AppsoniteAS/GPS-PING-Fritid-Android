package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.text.TextUtils;
import android.view.View;

import io.realm.Realm;
import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.db.RealmTracker;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.ObservableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class AddTrackerFragmentViewModel extends BaseFragmentViewModel {
    public ObservableField<Tracker> tracker = new ObservableField<>();
    public ObservableString nameError = new ObservableString();
    public ObservableString imeiNumberError = new ObservableString();
    public ObservableString trackerNumberError = new ObservableString();
    public ObservableString buttonText = new ObservableString();

    public void addTracker(View v) {
        if (validateData()) {
            Realm realm = Realm.getInstance(getContext());
            RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("uuid", tracker.get().uuid.get()).findFirst();
            realm.beginTransaction();
            if (realmTracker == null) {
                realmTracker = realm.createObject(RealmTracker.class);
            }
            RealmTracker.initWithTracker(realmTracker, tracker.get());
            realm.copyToRealm(realmTracker);
            realm.commitTransaction();
            if (actionListener != null) {
                actionListener.onAddTracker();
            }
        }
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    private ActionListener actionListener;

    public interface ActionListener {
        void onAddTracker();
    }

    @Override
    public void onModelAttached() {
        Realm realm = Realm.getInstance(Application.getContext());
        RealmTracker realmTracker = realm.where(RealmTracker.class).equalTo("uuid", tracker.get().uuid.get()).findFirst();
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
