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
    public static int getTrackerImage(ObservableString type) {
        try {
            Tracker.Type trType = Tracker.Type.valueOf(type.get());
            switch (trType) {
                case TK_ANYWHERE:
                    return R.drawable.image_tk_anywhere_normal;
                case TK_STAR:
                    return R.drawable.image_tk_star_normal;
                case TK_STAR_PET:
                    return R.drawable.image_tk_star_pet_normal;
                case TK_BIKE:
                case TK_STAR_BIKE:
                    return R.drawable.image_tk_bike;
                case LK209:
                case LK330:
                    return R.drawable.lk_209;
                case VT600:
                    return R.drawable.vt_600;
                case S1:
                case A9:
                    return R.drawable.s1;
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    public static boolean isFriendAdded(Friend friend) {
        return friend.confirmed.get() != null;
    }

    public static int getFriendStatusIcon(Boolean isSeeingTrackers, Boolean confirmed) {
        if (confirmed == null)
            return R.drawable.ic_add_friend;

        if (!confirmed) {
            return R.drawable.ic_warning;
        }

        if (isSeeingTrackers) {
            return R.drawable.ic_visible;
        }

        return R.drawable.ic_invisible;
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
