package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.widget.Toast;

import no.appsonite.gpsping.databinding.FragmentLoginBinding;
import no.appsonite.gpsping.viewmodel.LoginFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class LoginFragment extends BaseBindingFragment<FragmentLoginBinding, LoginFragmentViewModel> implements LoginFragmentViewModel.ActionListener {
    private static final String TAG = "LoginFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return null;
    }

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onViewModelCreated(LoginFragmentViewModel model) {
        super.onViewModelCreated(model);
        model.setActionListener(this);
    }

    @Override
    public void onLogin() {
        Toast.makeText(getContext(), getModel().login.get() + " - " + getModel().password.get(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRegisterClick() {
        getBaseActivity().replaceFragment(RegisterFragment.newInstance(), true);
    }
}
