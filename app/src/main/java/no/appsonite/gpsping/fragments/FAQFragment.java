package no.appsonite.gpsping.fragments;

import android.databinding.ObservableArrayList;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentFaqBinding;
import no.appsonite.gpsping.databinding.ItemFaqBinding;
import no.appsonite.gpsping.utils.BindingHelper;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;
import no.appsonite.gpsping.widget.GPSPingBaseRecyclerSwipeAdapter;

/**
 * Created: Belozerov Sergei
 * E-mail: belozerow@gmail.com
 * Company: APPGRANULA LLC
 * Date: 06/09/16
 */
public class FAQFragment extends BaseBindingFragment<FragmentFaqBinding, BaseFragmentViewModel> {
    @Override
    public String getFragmentTag() {
        return "FAQFragment";
    }

    @Override
    protected String getTitle() {
        return getString(R.string.faq);
    }

    @Override
    protected void onViewModelCreated(BaseFragmentViewModel model) {
        super.onViewModelCreated(model);
        String[] faq = getResources().getStringArray(R.array.faq_questions_answers);
        GPSPingBaseRecyclerSwipeAdapter adapter = new GPSPingBaseRecyclerSwipeAdapter() {
            @Override
            public ViewDataBinding onCreateViewDataBinding(ViewGroup parent, int viewType) {
                return ItemFaqBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            }

            @Override
            public void onClick(View v) {
                super.onClick(v);
                getBaseActivity().replaceFragment(FAQItemFragment.newInstance((FAQItem) v.getTag()), true);
            }
        };

        ObservableArrayList<FAQItem> items = new ObservableArrayList<>();
        for (int i = 0; i < faq.length; i++) {
            try {
                if (i % 2 == 0) {
                    items.add(new FAQItem(faq[i], faq[i + 1]));
                }
            } catch (Exception ignore) {

            }
        }
        BindingHelper.bindAdapter(adapter, items);
        adapter.setItems(items);
        getBinding().recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().recyclerView.setAdapter(adapter);
    }

    public static FAQFragment newInstance() {
        Bundle args = new Bundle();
        FAQFragment fragment = new FAQFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static class FAQItem implements Serializable {
        public String question;
        public String answer;

        public FAQItem(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }
}
