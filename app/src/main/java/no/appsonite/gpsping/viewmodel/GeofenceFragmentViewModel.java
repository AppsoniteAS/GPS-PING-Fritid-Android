package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;
import android.text.TextUtils;
import android.view.View;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.utils.BindableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class GeofenceFragmentViewModel extends BaseFragmentViewModel {
    public BindableString yards = new BindableString();
    public ObservableField<String> yardsError = new ObservableField<>();

    public BindableString phoneNumber = new BindableString();
    public ObservableField<String> phoneNumberError = new ObservableField<>();

    public void onSaveClick(View v) {
        if (validateData()) {
            if (actionsListener != null) {
                actionsListener.onSave();
            }
        }
    }

    private ActionsListener actionsListener;

    public void setActionsListener(ActionsListener actionsListener) {
        this.actionsListener = actionsListener;
    }

    public interface ActionsListener {
        void onSave();
    }

    private boolean validateData() {
        if (TextUtils.isEmpty(yards.get())) {
            yardsError.set(getContext().getString(R.string.yardsCanNotBeEmpty));
            return false;
        }
        yardsError.set(null);
        if (TextUtils.isEmpty(phoneNumber.get())) {
            phoneNumberError.set(getContext().getString(R.string.phoneNumberCanNotBeEmpty));
            return false;
        }
        phoneNumberError.set(null);
        return true;
    }
}
