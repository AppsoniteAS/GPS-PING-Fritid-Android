package no.appsonite.gpsping.api.content;

import android.databinding.ObservableLong;

import no.appsonite.gpsping.utils.ObservableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class ApiAnswer {
    private ObservableString status = new ObservableString();
    private ObservableString error = new ObservableString();
    private ObservableLong code = new ObservableLong();

    public ObservableLong getCode() {
        return code;
    }

    public void setCode(ObservableLong code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return "ok".equals(status.get());
    }

    public boolean isError() {
        return !isSuccess();
    }

    public ObservableString getStatus() {
        return status;
    }

    public void setStatus(ObservableString status) {
        this.status = status;
    }

    public ObservableString getError() {
        return error;
    }

    public void setError(ObservableString error) {
        this.error = error;
    }
}
