package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.view.View;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentChooseTrackerBinding;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class ChooseTrackerFragment extends BaseBindingFragment<FragmentChooseTrackerBinding, BaseFragmentViewModel> {
    private static final String TAG = "ChooseTrackerFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.addTracker);
    }

    @Override
    protected void onViewModelCreated(BaseFragmentViewModel model) {
        super.onViewModelCreated(model);
        View.OnClickListener onTrackerTypeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tkAnywhere:
                        showAddTrackerFragment(Tracker.Type.TK_ANYWHERE);
                        break;
                    case R.id.tkStar:
                        showAddTrackerFragment(Tracker.Type.TK_STAR);
                        break;
                    case R.id.tkStarPet:
                        showAddTrackerFragment(Tracker.Type.TK_STAR_PET);
                        break;
                    case R.id.lk209:
                        showAddTrackerFragment(Tracker.Type.LK209);
                        break;
                    case R.id.vt600:
                        showAddTrackerFragment(Tracker.Type.VT600);
                        break;
                    case R.id.s1:
                        showAddTrackerFragment(Tracker.Type.S1);
                        break;
                }
            }
        };
        getBinding().tkAnywhere.setOnClickListener(onTrackerTypeClickListener);
        getBinding().tkStar.setOnClickListener(onTrackerTypeClickListener);
        getBinding().tkStarPet.setOnClickListener(onTrackerTypeClickListener);
    }

    private void showAddTrackerFragment(Tracker.Type type) {
        Tracker tracker = new Tracker();
        tracker.type.set(type.toString());
        getBaseActivity().replaceFragment(AddTrackerFragment.newInstance(tracker), true);
    }

    public static ChooseTrackerFragment newInstance() {
        Bundle args = new Bundle();
        ChooseTrackerFragment fragment = new ChooseTrackerFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
