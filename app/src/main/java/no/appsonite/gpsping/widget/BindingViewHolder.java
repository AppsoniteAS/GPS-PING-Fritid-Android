package no.appsonite.gpsping.widget;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 23.12.2015
 */
public class BindingViewHolder extends RecyclerView.ViewHolder {
    public ViewDataBinding viewDataBinding;

    public BindingViewHolder(View itemView) {
        super(itemView);
        viewDataBinding = DataBindingUtil.bind(itemView);
    }
}
