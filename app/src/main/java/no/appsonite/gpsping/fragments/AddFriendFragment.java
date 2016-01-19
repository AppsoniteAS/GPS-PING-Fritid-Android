package no.appsonite.gpsping.fragments;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentAddFriendBinding;
import no.appsonite.gpsping.databinding.ItemFriendBinding;
import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.utils.BindingHelper;
import no.appsonite.gpsping.utils.Utils;
import no.appsonite.gpsping.viewmodel.AddFriendFragmentViewModel;
import no.appsonite.gpsping.widget.GPSPingBaseRecyclerSwipeAdapter;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public class AddFriendFragment extends BaseBindingFragment<FragmentAddFriendBinding, AddFriendFragmentViewModel> {
    private static final String TAG = "AddFriendFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.addFriend);
    }

    public static AddFriendFragment newInstance() {

        Bundle args = new Bundle();

        AddFriendFragment fragment = new AddFriendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onViewModelCreated(final AddFriendFragmentViewModel model) {
        super.onViewModelCreated(model);
        GPSPingBaseRecyclerSwipeAdapter<Friend> adapter = new GPSPingBaseRecyclerSwipeAdapter<Friend>() {
            @Override
            public ViewDataBinding onCreateViewDataBinding(ViewGroup parent, int viewType) {
                return ItemFriendBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.friendStatus:
                        model.addFriend((Friend) v.getTag());
                        break;
                }
            }
        };
        adapter.setItems(model.searchResults);
        getBinding().friendsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().friendsRecycler.setAdapter(adapter);
        BindingHelper.bindAdapter(adapter, model.searchResults);

        Utils.showKeyboard(getActivity(), getBinding().searchFriend);
    }
}
