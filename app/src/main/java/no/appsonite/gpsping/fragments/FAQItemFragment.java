package no.appsonite.gpsping.fragments;

import android.os.Bundle;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentFaqDetailsBinding;
import no.appsonite.gpsping.viewmodel.FAQDetailsFragmentViewModel;

/**
 * Created: Belozerov Sergei
 * E-mail: belozerow@gmail.com
 * Company: APPGRANULA LLC
 * Date: 07/09/16
 */
public class FAQItemFragment extends BaseBindingFragment<FragmentFaqDetailsBinding, FAQDetailsFragmentViewModel> {
    private static final String ARG_FAQ = "arg_faq";

    public static FAQItemFragment newInstance(FAQFragment.FAQItem faq) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FAQ, faq);
        FAQItemFragment fragment = new FAQItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getFragmentTag() {
        return "FAQItemFragment";
    }

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    protected void onViewModelCreated(FAQDetailsFragmentViewModel model) {
        super.onViewModelCreated(model);
        getModel().faq.set((FAQFragment.FAQItem) getArguments().getSerializable(ARG_FAQ));
    }
}
