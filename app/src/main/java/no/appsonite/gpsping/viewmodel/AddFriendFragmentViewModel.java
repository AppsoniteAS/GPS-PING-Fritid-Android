package no.appsonite.gpsping.viewmodel;

import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.text.TextUtils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.utils.ObservableString;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public class AddFriendFragmentViewModel extends BaseFragmentViewModel {

    public ObservableArrayList<Friend> searchResults = new ObservableArrayList<>();
    public ObservableString searchString = new ObservableString();

    @Override
    public void onModelAttached() {
        super.onModelAttached();
        initSearch();
    }

    private void initSearch() {
        final PublishSubject<String> searchObservable = PublishSubject.create();
        searchString.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                searchObservable.onNext(searchString.get());
            }
        });
        searchObservable
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        searchForFriends(s);
                    }
                });
    }

    private void searchForFriends(String s) {
        if (TextUtils.isEmpty(s) || s.length() < 2) {
            searchResults.clear();
        } else {
            searchResults.clear();
            for (int i = 0; i < new Random().nextInt(7); i++) {
                Friend friend = new Friend();
                friend.name.set(s);
                friend.userName.set(s);
                friend.status.set(Friend.Status.not_added);
                searchResults.add(friend);
            }
        }
    }

    public void addFriend(Friend friend) {

    }
}
