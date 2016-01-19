package no.appsonite.gpsping.widget;

import android.databinding.ObservableArrayList;
import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import no.appsonite.gpsping.BR;
import no.appsonite.gpsping.R;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public abstract class GPSPingBaseRecyclerSwipeAdapter<T> extends RecyclerSwipeAdapter<BindingViewHolder> implements View.OnClickListener {
    private ObservableArrayList<T> items = new ObservableArrayList<>();

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BindingViewHolder(onCreateViewDataBinding(parent, viewType).getRoot());
    }

    public abstract ViewDataBinding onCreateViewDataBinding(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        holder.viewDataBinding.setVariable(BR.item, getItem(position));
        holder.viewDataBinding.setVariable(BR.onClickListener, this);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public T getItem(int position) {
        return items.get(position);
    }

    public void setItems(ObservableArrayList<T> items) {
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