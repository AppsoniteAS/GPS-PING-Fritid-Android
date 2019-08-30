package no.appsonite.gpsping.api.content;

import java.util.ArrayList;

import no.appsonite.gpsping.model.Tracker;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 25.01.2016
 */
public class TrackersAnswer extends ApiAnswer {
    private ArrayList<Tracker> trackers;

    public ArrayList<Tracker> getTrackers() {
        return trackers;
    }

    public void setTrackers(ArrayList<Tracker> trackers) {
        this.trackers = trackers;
    }
}
