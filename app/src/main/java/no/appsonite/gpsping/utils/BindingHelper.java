package no.appsonite.gpsping.utils;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.model.Tracker;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class BindingHelper {
    public static int getTrackerImage(Tracker.Type type) {
        switch (type) {
            case TK_ANYWHERE:
                return R.drawable.image_tk_anywhere_normal;
            case TK_STAR:
                return R.drawable.image_tk_star_normal;
            case TK_STAR_PET:
                return R.drawable.image_tk_star_pet_normal;
        }
        return 0;
    }

    public static int getFriendStatusIcon(Friend.Status status) {
        switch (status) {
            case invisible:
                return R.drawable.ic_invisible;
            case visible:
                return R.drawable.ic_visible;
            case not_confirmed:
                return R.drawable.ic_warning;
            case not_added:
                return R.drawable.ic_add_friend;
        }
        return 0;
    }

    public static void bindAdapter(final RecyclerView.Adapter adapter, ObservableArrayList observable) {
        observable.addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
                adapter.notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                adapter.notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
                adapter.notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }
}
