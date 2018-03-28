package no.appsonite.gpsping.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tbruyelle.rxpermissions.RxPermissions;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.activities.MainActivity;
import no.appsonite.gpsping.activities.WebViewActivity;
import no.appsonite.gpsping.databinding.FragmentAddTrackerWithPinCodeBinding;
import no.appsonite.gpsping.viewmodel.AddTrackerWithPinCodeViewModel;
import rx.Observable;

import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;

/**
 * Created: Belozerov Sergei
 * E-mail: belozerow@gmail.com
 * Company: APPGRANULA LLC
 * Date: 05/07/16
 */
public class AddTrackerWithPinCodeFragment extends BaseBindingFragment<FragmentAddTrackerWithPinCodeBinding, AddTrackerWithPinCodeViewModel> {
    private static final String ARG_AFTER_REG = "arg_after_reg";
    private static final String SUBSCRIPTION_TERMS_URL = "https://fritid.gpsping.no/subscription_agreement/";
    private static final String TAG = AddTrackerWithPinCodeFragment.class.getSimpleName();
    private static final int PERMISSION_SMS = 1;
    private HelpBottomSheet helpBottomSheet;
    private int bottomMargin;
    private AnimatorSet animatorSet;
    private boolean isOpenBottomView;

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
        getBinding().addTrackerBtn.setOnClickListener(view -> bindPreTracker());

        getBinding().skipAddTrackerBtn.setOnClickListener(view -> skipAddTracker());

        boolean afterReg = getArguments().getBoolean(ARG_AFTER_REG, false);
        getModel().afterReg.set(afterReg);

        getBinding().helpFrame.setOnClickListener(view -> openClosedAnimation());

        getBinding().tvLink.setOnClickListener(v -> WebViewActivity.open(getContext(), SUBSCRIPTION_TERMS_URL, getString(R.string.subscription_agreement), false));

        getBinding().trackerNormal.setOnClickListener(v -> showInfoTrackerNormal());
        
        getBinding().trackerMarcel.setOnClickListener(v -> showInfoTrackerMarcel());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout.LayoutParams params;
        params = (LinearLayout.LayoutParams) getBinding().helpFrame.getLayoutParams();
        bottomMargin = params.bottomMargin;
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
    }

    private void skipAddTracker() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    private void showInfoTrackerNormal() {
        if (helpBottomSheet == null) {
            helpBottomSheet = new HelpBottomSheet();
        }
        helpBottomSheet.layoutId = R.layout.dialog_new_tracker_help;
        helpBottomSheet.show(getChildFragmentManager(), "Help");
    }

    private void showInfoTrackerMarcel() {
        if (helpBottomSheet == null) {
            helpBottomSheet = new HelpBottomSheet();
        }
        helpBottomSheet.layoutId = R.layout.dialog_new_tracker_help_marcel;
        helpBottomSheet.show(getChildFragmentManager(), "Help");
    }

    private void openClosedAnimation() {
        if (isOpenBottomView) {
            isOpenBottomView = false;
            animatorSet.play(ObjectAnimator.ofFloat(getBinding().helpFrame, "Y",
                    getBinding().helpFrame.getY(), getBinding().helpFrame.getY() - bottomMargin));
            animatorSet.start();
        } else {
            isOpenBottomView = true;
            animatorSet.play(ObjectAnimator.ofFloat(getBinding().helpFrame, "Y",
                    getBinding().helpFrame.getY(), getBinding().helpFrame.getY() + bottomMargin));
            animatorSet.start();

        }
    }

    private void bindPreTracker() {
        new RxPermissions(getActivity())
                .request(SEND_SMS, READ_SMS, RECEIVE_SMS)
                .subscribe(this::onNextBindTracker);
    }

    private void onNextBindTracker(boolean granted) {
        if (granted) {
            bindTracker();
        } else {
            showInfoDeniedPermission(getContext(), PERMISSION_SMS, SEND_SMS, READ_SMS, RECEIVE_SMS);
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSION_SMS:
                bindPreTracker();
                break;
        }
    }

    public static class HelpBottomSheet extends DialogFragment {
        private int layoutId;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view = layoutInflater.inflate(layoutId, null);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog_FullScreen)
                    .setTitle(null)
                    .setView(view);
            view.findViewById(R.id.closeDialog).setOnClickListener(view1 -> dismiss());
            return alertDialog.create();
        }
    }
}
