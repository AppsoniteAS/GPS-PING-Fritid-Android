package no.appsonite.gpsping.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import no.appsonite.gpsping.activities.MainActivity;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.databinding.FragmentLoginBinding;
import no.appsonite.gpsping.viewmodel.LoginFragmentViewModel;
import rx.Observable;
import rx.Observer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class LoginFragment extends BaseBindingFragment<FragmentLoginBinding, LoginFragmentViewModel> {
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
        getBinding().registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().replaceFragment(RegisterFragment.newInstance(), true);
            }
        });

        getBinding().loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });
    }

    private void startLogin() {
        Observable<LoginAnswer> observable = getModel().onLoginClick();
        if (observable != null) {
            showProgress();
            observable.subscribe(new Observer<LoginAnswer>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    showError(e);
                }

                @Override
                public void onNext(LoginAnswer loginAnswer) {
                    if (loginAnswer.isError()) {
                        showError(new Throwable(loginAnswer.getError().get()));
                    } else {
                        hideProgress();
                        if (getActivity() != null) {
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();
                        }
                    }
                }
            });
        }
    }
}
