package no.appsonite.gpsping.fragments;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.databinding.FragmentFriendsBinding;
import no.appsonite.gpsping.databinding.ItemFriendBinding;
import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.utils.BindingHelper;
import no.appsonite.gpsping.viewmodel.FriendsFragmentViewModel;
import no.appsonite.gpsping.widget.GPSPingBaseRecyclerSwipeAdapter;
import rx.Observer;
import rx.functions.Action1;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public class FriendsFragment extends BaseBindingFragment<FragmentFriendsBinding, FriendsFragmentViewModel> {
    private static final String TAG = "FriendsFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.friends);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_friends, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addFriend) {
            getBaseActivity().replaceFragment(AddFriendFragment.newInstance(), true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onViewModelCreated(final FriendsFragmentViewModel model) {
        super.onViewModelCreated(model);
        GPSPingBaseRecyclerSwipeAdapter<Friend> adapter = new GPSPingBaseRecyclerSwipeAdapter<Friend>() {
            @Override
            public ViewDataBinding onCreateViewDataBinding(ViewGroup parent, int viewType) {
                return ItemFriendBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.removeFriend:
                        removeFriend((Friend) v.getTag());
                        break;
                    case R.id.friendStatus:
                        switchVisibility((Friend) v.getTag());
                        break;
                }
            }
        };
        model.requestFriends().doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                showError(throwable);
            }
        });
        adapter.setItems(model.friends);
        getBinding().friendsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().friendsRecycler.setAdapter(adapter);
        BindingHelper.bindAdapter(adapter, model.friends);
    }

    private void switchVisibility(Friend friend) {
        if (!friend.confirmed.get())
            return;
        showProgress();
        getModel().switchStatus(friend)
                .subscribe(new Observer<ApiAnswer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showError(e);
                    }

                    @Override
                    public void onNext(ApiAnswer apiAnswer) {
                        hideProgress();
                    }
                });
    }

    private void removeFriend(Friend friend) {
        showProgress();
        getModel().removeFriend(friend).subscribe(new Observer<ApiAnswer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                showError(e);
            }

            @Override
            public void onNext(ApiAnswer apiAnswer) {
                hideProgress();
            }
        });
    }


    public static FriendsFragment newInstance() {
        Bundle args = new Bundle();
        FriendsFragment fragment = new FriendsFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
