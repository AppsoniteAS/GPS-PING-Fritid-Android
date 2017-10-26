package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.databinding.FragmentProfileBinding;
import no.appsonite.gpsping.viewmodel.ProfileFragmentViewModel;
import rx.Observable;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class ProfileFragment extends BaseBindingFragment<FragmentProfileBinding, ProfileFragmentViewModel> {
    private static final String TAG = ProfileFragment.class.getSimpleName();

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void onViewModelCreated(ProfileFragmentViewModel model) {
        super.onViewModelCreated(model);
        getBinding().buttonSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        Observable<ApiAnswer> answer = getModel().onSaveClick();
        if (answer != null) {
            showProgress();
            answer.subscribe(apiAnswer -> {

            }, this::showError, this::saveProfileCompleted);
        }
    }

    private void saveProfileCompleted() {
        if (getActivity() == null)
            return;
        Toast.makeText(getActivity(), getString(R.string.profileSaved), Toast.LENGTH_SHORT).show();
        hideProgress();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        showProgress();
        getModel().logout();
    }
}
