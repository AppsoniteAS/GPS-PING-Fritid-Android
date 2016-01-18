package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import no.appsonite.gpsping.R;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout){
            getModel().logout();
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
