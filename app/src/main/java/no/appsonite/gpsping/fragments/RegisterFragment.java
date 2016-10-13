package no.appsonite.gpsping.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.activities.MainActivity;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.databinding.FragmentRegisterBinding;
import no.appsonite.gpsping.viewmodel.RegisterFragmentViewModel;
import rx.Observable;
import rx.Observer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 15.01.2016
 */
public class RegisterFragment extends BaseBindingFragment<FragmentRegisterBinding, RegisterFragmentViewModel> {
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

    public TextView.OnEditorActionListener passwordDone = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                startRegister();
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onViewModelCreated(RegisterFragmentViewModel model) {
        super.onViewModelCreated(model);

        getBinding().registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

        getBinding().phoneNumber.setOnEditorActionListener(passwordDone);
    }

    private void startRegister() {
        Observable<LoginAnswer> observable = getModel().onRegisterClick();
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
//                            startActivity(new Intent(getActivity(), MainActivity.class));
//                            getActivity().finish();
                            getBaseActivity().replaceFragment(AddTrackerWithPinCodeFragment.newInstance(true), false);
                        }
                    }
                }
            });
        }
    }
}
