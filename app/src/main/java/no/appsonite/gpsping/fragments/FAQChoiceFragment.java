package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import no.appsonite.gpsping.databinding.FragmentFaqChoiceBinding;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;

/**
 * Created by taras on 10/10/17.
 */

public class FAQChoiceFragment extends BaseBindingFragment<FragmentFaqChoiceBinding, BaseFragmentViewModel> {
    private static final String TAG = FAQChoiceFragment.class.getSimpleName();

    public static FAQChoiceFragment newInstance() {
        Bundle args = new Bundle();
        FAQChoiceFragment fragment = new FAQChoiceFragment();
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBinding().originalBtn.setOnClickListener(v -> getBaseActivity().replaceFragment(FAQFragment.newInstance(), true));
    }
}
