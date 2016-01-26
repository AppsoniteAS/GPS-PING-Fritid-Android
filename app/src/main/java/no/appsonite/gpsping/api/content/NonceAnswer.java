package no.appsonite.gpsping.api.content;

import no.appsonite.gpsping.utils.ObservableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class NonceAnswer extends ApiAnswer {
    private ObservableString nonce = new ObservableString();

    public ObservableString getNonce() {
        return nonce;
    }

    public void setNonce(ObservableString nonce) {
        this.nonce = nonce;
    }
}
