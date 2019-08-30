package no.appsonite.gpsping.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

import no.appsonite.gpsping.activities.MainActivity;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.databinding.FragmentLoginBinding;
import no.appsonite.gpsping.utils.Utils;
import no.appsonite.gpsping.viewmodel.LoginFragmentViewModel;
import rx.Observable;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class LoginFragment extends BaseBindingFragment<FragmentLoginBinding, LoginFragmentViewModel> {
    private static final String TAG = LoginFragment.class.getSimpleName();

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
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
    protected void onViewModelCreated(LoginFragmentViewModel model) {
        super.onViewModelCreated(model);
        getBinding().registerButton.setOnClickListener(v -> getBaseActivity().replaceFragment(RegisterFragment.newInstance(), true));

        getBinding().loginButton.setOnClickListener(v -> startLogin());

        getBinding().forgetPassword.setOnClickListener(v -> getBaseActivity().replaceFragment(RestoreFragment.newInstance(), true));
        getBinding().password.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                startLogin();
                Utils.hideKeyboard(getActivity());
                return true;
            }
            return false;
        });
    }

    private void startLogin() {
        Utils.hideKeyboard(getActivity());
        Observable<LoginAnswer> observable = getModel().onLoginClick();
        if (observable != null) {
            showProgress();
            observable.subscribe(loginAnswer -> startLoginCompleted()
                    , this::showError);
        }
    }

    private void startLoginCompleted() {
        hideProgress();
        if (getActivity() != null) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }
}
