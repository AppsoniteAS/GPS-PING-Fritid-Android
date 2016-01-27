package no.appsonite.gpsping.api.content.geo;

import java.util.ArrayList;

import no.appsonite.gpsping.api.content.ApiAnswer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 27.01.2016
 */
public class GeoPointsAnswer extends ApiAnswer {
    private ArrayList<GeoItem> users;

    public ArrayList<GeoItem> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<GeoItem> users) {
        this.users = users;
    }
}
