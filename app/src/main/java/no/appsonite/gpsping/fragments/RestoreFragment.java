package no.appsonite.gpsping.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.databinding.FragmentRestoreBinding;
import no.appsonite.gpsping.viewmodel.RestoreFragmentViewModel;
import rx.Observable;
import rx.Observer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 29.01.2016
 */
public class RestoreFragment extends BaseBindingFragment<FragmentRestoreBinding, RestoreFragmentViewModel> {
    private static final String TAG = "RestoreFragment";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.restore);
    }

    public static RestoreFragment newInstance() {
        Bundle args = new Bundle();
        RestoreFragment fragment = new RestoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onViewModelCreated(RestoreFragmentViewModel model) {
        super.onViewModelCreated(model);
        getBinding().restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restorePassword();
            }
        });
    }

    private void restorePassword() {
        Observable<ApiAnswer> observable = getModel().restorePassword();
        if (observable != null) {
            showProgress();
            observable.subscribe(new Observer<ApiAnswer>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    showError(e);
                }

                @Override
                public void onNext(ApiAnswer apiAnswer) {
                    hideProgress();
                    Toast.makeText(getActivity(), getString(R.string.emailSent), Toast.LENGTH_LONG).show();
                    getBaseActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }
}
