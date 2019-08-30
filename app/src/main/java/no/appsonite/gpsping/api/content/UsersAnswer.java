package no.appsonite.gpsping.api.content;

import java.util.ArrayList;

import no.appsonite.gpsping.model.Friend;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 26.01.2016
 */
public class UsersAnswer extends ApiAnswer{
    private ArrayList<Friend> users;

    public ArrayList<Friend> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<Friend> users) {
        this.users = users;
    }
}
