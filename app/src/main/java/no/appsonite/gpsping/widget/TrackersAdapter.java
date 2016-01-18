package no.appsonite.gpsping.widget;

import android.databinding.ObservableArrayList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;
import java.util.List;

import no.appsonite.gpsping.BR;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.ItemTrackerBinding;
import no.appsonite.gpsping.model.Tracker;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class TrackersAdapter extends RecyclerSwipeAdapter<BindingViewHolder> implements View.OnClickListener{
    private ObservableArrayList<Tracker> items = new ObservableArrayList<>();

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BindingViewHolder(ItemTrackerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        holder.viewDataBinding.setVariable(BR.item, getItem(position));
        holder.viewDataBinding.setVariable(BR.onClickListener, this);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Tracker getItem(int position) {
        return items.get(position);
    }

    public void setItems(ObservableArrayList<Tracker> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.bottomWrapper;
    }

    @Override
    public void onClick(View v) {

    }
}
