package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableArrayList;

import io.realm.Realm;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.api.content.FriendsAnswer;
import no.appsonite.gpsping.db.RealmTracker;
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
public class FriendsFragmentViewModel extends SubscriptionViewModel {
    public ObservableArrayList<Friend> friends = new ObservableArrayList<>();

    public Observable<ApiAnswer> removeFriend(final Friend friend) {
        Observable<ApiAnswer> observable = execute(ApiFactory.getService().removeFriend(friend.id.get()))
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
        Observable<ApiAnswer> observable = execute(ApiFactory.getService().setSeeingTrackers(friend.id.get(), newState))
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
        Observable<FriendsAnswer> observable = execute(ApiFactory.getService().getFriends())
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
                for (Friend friend : friendsAnswer.getFriends()) {
                    if (friend.username != null) {
                        friends.add(friend);
                    }
                }
                for (Friend friend : friendsAnswer.getRequests()) {
                    if (friend.username != null) {
                        friends.add(friend);
                    }
                }
            }
        });
        return observable;
    }

    public Observable<ApiAnswer> confirmFriendShip(final Friend friend) {
        Observable<ApiAnswer> observable = execute(ApiFactory.getService().confirmFriendship(friend.id.get()))
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
                friend.isSeeingTrackers.set(false);
                friend.confirmed.set(true);
            }
        });
        return observable;
    }

    public boolean isSubscriptionRequired() {
        Realm realm = Realm.getDefaultInstance();
        RealmTracker realmTracker = realm.where(RealmTracker.class).findFirst();
        return realmTracker == null && !isSubscribed();
    }


}
