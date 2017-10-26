package no.appsonite.gpsping.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.activities.MainActivity;
import no.appsonite.gpsping.databinding.FragmentAddTrackerWithPinCodeBinding;
import no.appsonite.gpsping.viewmodel.AddTrackerWithPinCodeViewModel;
import rx.Observable;

/**
 * Created: Belozerov Sergei
 * E-mail: belozerow@gmail.com
 * Company: APPGRANULA LLC
 * Date: 05/07/16
 */
public class AddTrackerWithPinCodeFragment extends BaseBindingFragment<FragmentAddTrackerWithPinCodeBinding, AddTrackerWithPinCodeViewModel> {
    private static final String ARG_AFTER_REG = "arg_after_reg";
    private static final String TAG = AddTrackerWithPinCodeFragment.class.getSimpleName();

    public static AddTrackerWithPinCodeFragment newInstance() {
        return newInstance(false);
    }

    public static AddTrackerWithPinCodeFragment newInstance(boolean afterRegistration) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_AFTER_REG, afterRegistration);
        AddTrackerWithPinCodeFragment fragment = new AddTrackerWithPinCodeFragment();
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
    protected void onViewModelCreated(AddTrackerWithPinCodeViewModel model) {
        super.onViewModelCreated(model);
        getBinding().addTrackerBtn.setOnClickListener(view -> bindTracker());

        getBinding().skipAddTrackerBtn.setOnClickListener(view -> skipAddTracker());

        boolean afterReg = getArguments().getBoolean(ARG_AFTER_REG, false);
        getModel().afterReg.set(afterReg);

        getBinding().newTrackerHelpBtn.setOnClickListener(view -> newTrackerHelp());
    }

    private void skipAddTracker() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    private void newTrackerHelp() {
        HelpBottomSheet helpBottomSheet = new HelpBottomSheet();
        helpBottomSheet.show(getChildFragmentManager(), "Help");
    }

    private void bindTracker() {
        Observable<Boolean> observable = getModel().addTracker(getBaseActivity());
        if (observable == null)
            return;
        showProgress();
        observable.subscribe(aBoolean -> {

        }, this::showError, this::bindTrackerCompleted);
    }

    private void bindTrackerCompleted() {
        hideProgress();
        if (getModel().afterReg.get()) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        } else {
            getBaseActivity().getSupportFragmentManager().popBackStack();
        }
    }

    public static class HelpBottomSheet extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view = layoutInflater.inflate(R.layout.dialog_new_tracker_help, null);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog_FullScreen)
                    .setTitle(null)
                    .setView(view);
            view.findViewById(R.id.closeDialog).setOnClickListener(view1 -> dismiss());
            return alertDialog.create();
        }
    }
}
