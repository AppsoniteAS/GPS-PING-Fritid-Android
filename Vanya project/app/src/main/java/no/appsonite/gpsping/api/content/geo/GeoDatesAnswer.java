package no.appsonite.gpsping.api.content.geo;

import java.util.ArrayList;

import no.appsonite.gpsping.api.content.ApiAnswer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public class GeoDatesAnswer extends ApiAnswer {
    private ArrayList<Long> dates;

    public ArrayList<Long> getDates() {
        return dates;
    }

    public void setDates(ArrayList<Long> dates) {
        this.dates = dates;
    }
}
