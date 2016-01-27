package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableArrayList;

import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.api.content.FriendsAnswer;
import no.appsonite.gpsping.model.Friend;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public class FriendsFragmentViewModel extends BaseFragmentViewModel {
    public ObservableArrayList<Friend> friends = new ObservableArrayList<>();

    public Observable<ApiAnswer> removeFriend(final Friend friend) {
        Observable<ApiAnswer> observable = ApiFactory.getService().removeFriend(friend.id.get())
                .cache()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<ApiAnswer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(ApiAnswer apiAnswer) {
                friends.remove(friend);
            }
        });
        return observable;
    }

    public Observable<ApiAnswer> switchStatus(final Friend friend) {
        final boolean newState = !friend.isSeeingTrackers.get();
        Observable<ApiAnswer> observable = ApiFactory.getService().setSeeingTrackers(friend.id.get(), newState)
                .cache()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<ApiAnswer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(ApiAnswer apiAnswer) {
                friend.isSeeingTrackers.set(newState);
            }
        });
        return observable;
    }

    public Observable<FriendsAnswer> requestFriends() {
        Observable<FriendsAnswer> observable = ApiFactory.getService().getFriends()
                .cache()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<FriendsAnswer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(FriendsAnswer friendsAnswer) {
                friends.clear();
                friends.addAll(friendsAnswer.getFriends());
                friends.addAll(friendsAnswer.getRequests());
            }
        });
        return observable;
    }
}
