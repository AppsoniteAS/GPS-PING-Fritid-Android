package no.appsonite.gpsping.fragments;

import android.databinding.ObservableArrayList;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
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
    private static final String TAG = FAQFragment.class.getSimpleName();
    private static final String KEY_FAQ_CHOOSER = "key_for_chooser";

    public static FAQFragment newInstance(FaqChooser faqChooser) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_FAQ_CHOOSER, faqChooser);
        FAQFragment fragment = new FAQFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    protected void onViewModelCreated(BaseFragmentViewModel model) {
        super.onViewModelCreated(model);
        FaqChooser faqChooser = (FaqChooser) getArguments().getSerializable(KEY_FAQ_CHOOSER);

        String[] faq = getInformationOfTrackers(faqChooser);
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

    private String[] getInformationOfTrackers(FaqChooser faqChooser) {
        switch (faqChooser) {
            case ORIGINAL_GPS_TRACKER:
                return getResources().getStringArray(R.array.faq_questions_answers);
            case GPS_MARCEL_AND_ISABELLA_TRACKER:
                return getResources().getStringArray(R.array.faq_questions_answers_s1_cat);
            default:
                return getResources().getStringArray(R.array.faq_questions_answers_s1_cat);
        }
    }

    public enum FaqChooser {
        ORIGINAL_GPS_TRACKER, GPS_MARCEL_AND_ISABELLA_TRACKER
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
