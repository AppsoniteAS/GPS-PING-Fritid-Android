package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.widget.Toast;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.FragmentRegisterBinding;
import no.appsonite.gpsping.viewmodel.RegisterFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class RegisterFragment extends BaseBindingFragment<FragmentRegisterBinding, RegisterFragmentViewModel> implements RegisterFragmentViewModel.ActionListener {
    @Override
    public String getFragmentTag() {
        return "RegisterFragment";
    }

    @Override
    protected String getTitle() {
        return getString(R.string.register);
    }

    public static RegisterFragment newInstance() {
        Bundle args = new Bundle();
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onViewModelCreated(RegisterFragmentViewModel model) {
        super.onViewModelCreated(model);
        model.setActionListener(this);
    }

    @Override
    public void onRegister() {
        Toast.makeText(getActivity(), getModel().email.get(), Toast.LENGTH_SHORT).show();
    }
}
