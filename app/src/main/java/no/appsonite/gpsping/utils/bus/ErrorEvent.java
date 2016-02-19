package no.appsonite.gpsping.utils.bus;

import no.appsonite.gpsping.api.content.ApiAnswer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.02.2016
 */
public class ErrorEvent {
    private ApiAnswer apiAnswer;

    public ApiAnswer getApiAnswer() {
        return apiAnswer;
    }

    public void setApiAnswer(ApiAnswer apiAnswer) {
        this.apiAnswer = apiAnswer;
    }
}
