package no.appsonite.gpsping.api.content;

import java.util.ArrayList;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 11.02.2016
 */
public class PoiAnswer extends ApiAnswer {
    private ArrayList<Poi> poi;

    public ArrayList<Poi> getPoi() {
        return poi;
    }

    public void setPoi(ArrayList<Poi> poi) {
        this.poi = poi;
    }
}
