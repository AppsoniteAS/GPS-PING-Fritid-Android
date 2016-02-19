package no.appsonite.gpsping.viewmodel;

import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.api.content.UsersAnswer;
import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.utils.ObservableString;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
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
            execute(ApiFactory.getService().searchFriends(s))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<UsersAnswer>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(UsersAnswer usersAnswer) {
                            searchResults.clear();
                            searchResults.addAll(usersAnswer.getUsers());
                        }
                    });
        }
    }

    public rx.Observable<ApiAnswer> addFriend(Friend friend) {
        return execute(ApiFactory.getService().addFriend(friend.id.get()))
                .cache()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
