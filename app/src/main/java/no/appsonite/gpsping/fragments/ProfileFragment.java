package no.appsonite.gpsping.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.activities.LoginActivity;
import no.appsonite.gpsping.databinding.FragmentProfileBinding;
import no.appsonite.gpsping.viewmodel.ProfileFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class ProfileFragment extends BaseBindingFragment<FragmentProfileBinding, ProfileFragmentViewModel> {
    private static final String TAG = "ProfileFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.profile);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void onViewModelCreated(ProfileFragmentViewModel model) {
        super.onViewModelCreated(model);
        getBinding().buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getModel().onSaveClick();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            getModel().logout();
            getContext().startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
