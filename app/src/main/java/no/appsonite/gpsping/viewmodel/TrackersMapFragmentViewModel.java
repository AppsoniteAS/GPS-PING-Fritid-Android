package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.model.Friend;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class TrackersMapFragmentViewModel extends BaseFragmentViewModel {
    public ObservableArrayList<Friend> friendList = new ObservableArrayList<>();
    public ObservableField<MapType> mapType = new ObservableField<>();

    public void requestFriends() {

        Friend all = new Friend();
        all.userName.set(getContext().getString(R.string.all));
        friendList.add(all);

        Friend friend = new Friend();
        friend.userName.set("McCain");
        friendList.add(friend);

        friend = new Friend();
        friend.userName.set("Putin");
        friendList.add(friend);

        friend = new Friend();
        friend.userName.set("Assad");
        friendList.add(friend);
    }

    public enum MapType {
        Satellite, Standard, Topo
    }
}
