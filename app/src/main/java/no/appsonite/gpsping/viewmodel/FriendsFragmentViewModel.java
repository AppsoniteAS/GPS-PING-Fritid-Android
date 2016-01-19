package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableArrayList;

import no.appsonite.gpsping.model.Friend;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public class FriendsFragmentViewModel extends BaseFragmentViewModel {
    public ObservableArrayList<Friend> friends = new ObservableArrayList<>();

    public void removeFriend(Friend friend) {
        friends.remove(friend);
    }

    public void initFakeFriend() {
        if (!friends.isEmpty())
            return;
        Friend friend = new Friend();
        friend.name.set("John");
        friend.userName.set("McCain");
        friend.status.set(Friend.Status.not_confirmed);

        friends.add(friend);

        friend = new Friend();
        friend.name.set("Vladimir");
        friend.userName.set("Putin");
        friend.status.set(Friend.Status.visible);

        friends.add(friend);

        friend = new Friend();
        friend.name.set("Bashar");
        friend.userName.set("Assad");
        friend.status.set(Friend.Status.invisible);

        friends.add(friend);
    }

    public void switchStatus(Friend friend) {
        if (Friend.Status.invisible.equals(friend.status.get())) {
            friend.status.set(Friend.Status.visible);
        } else if (Friend.Status.visible.equals(friend.status.get())) {
            friend.status.set(Friend.Status.invisible);
        }
    }
}
