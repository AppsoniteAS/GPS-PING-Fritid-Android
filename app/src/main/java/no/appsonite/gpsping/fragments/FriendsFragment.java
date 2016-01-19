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
import no.appsonite.gpsping.databinding.FragmentFriendsBinding;
import no.appsonite.gpsping.databinding.ItemFriendBinding;
import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.utils.BindingHelper;
import no.appsonite.gpsping.viewmodel.FriendsFragmentViewModel;
import no.appsonite.gpsping.widget.GPSPingBaseRecyclerSwipeAdapter;

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
                        model.removeFriend((Friend) v.getTag());
                        break;
                    case R.id.friendStatus:
                        model.switchStatus((Friend) v.getTag());
                        break;
                }
            }
        };
        model.initFakeFriend();
        adapter.setItems(model.friends);
        getBinding().friendsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().friendsRecycler.setAdapter(adapter);
        BindingHelper.bindAdapter(adapter, model.friends);
    }


    public static FriendsFragment newInstance() {
        Bundle args = new Bundle();
        FriendsFragment fragment = new FriendsFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
