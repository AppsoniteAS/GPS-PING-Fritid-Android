package no.appsonite.gpsping.api.content;

import java.util.ArrayList;

import no.appsonite.gpsping.model.Friend;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 26.01.2016
 */
public class FriendsAnswer extends ApiAnswer {
    private ArrayList<Friend> friends;
    private ArrayList<Friend> requests;

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }

    public ArrayList<Friend> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<Friend> requests) {
        this.requests = requests;
    }
}
