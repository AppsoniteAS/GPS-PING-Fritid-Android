package no.appsonite.gpsping.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import no.appsonite.gpsping.R;


/**
 * Created by Belozerov on 05.02.2015.
 */
public class ProgressDialogFragment extends DialogFragment {
    private static final String ARG_MESSAGE = "message";
    private static final String TAG = "progress_dialog_fragment";
    private static final long TIMEOUT = 15 * 1000;

    public static void show(AppCompatActivity activity) {
        DialogFragment progressDialog =
                (DialogFragment) activity.getSupportFragmentManager().findFragmentByTag(TAG);
        if (progressDialog == null) {
            progressDialog = create();
            progressDialog.setCancelable(false);
            progressDialog.show(activity.getSupportFragmentManager(), TAG);
        }
    }

    public static void showCancelable(AppCompatActivity activity) {
        DialogFragment progressDialog =
                (DialogFragment) activity.getSupportFragmentManager().findFragmentByTag(TAG);
        if (progressDialog == null) {
            progressDialog = create();
            progressDialog.show(activity.getSupportFragmentManager(), TAG);
        }
    }

    public static void hide(AppCompatActivity activity) {
        DialogFragment progressDialog =
                (DialogFragment) activity.getSupportFragmentManager().findFragmentByTag(TAG);
        if (progressDialog != null) {
            progressDialog.dismissAllowingStateLoss();
        }
    }

    public static ProgressDialogFragment create(int message) {
        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MESSAGE, message);
        progressDialogFragment.setArguments(args);
        return progressDialogFragment;
    }

    public static ProgressDialogFragment create() {
        return create(R.string.wait);
    }

    public static boolean isShowing(AppCompatActivity activity) {
        DialogFragment progressDialog =
                (DialogFragment) activity.getSupportFragmentManager().findFragmentByTag(TAG);
        return progressDialog != null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(getArguments().getInt(ARG_MESSAGE)));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    progressDialog.dismiss();
                } catch (Exception ignore) {

                }
            }
        }, TIMEOUT);
        return progressDialog;
    }
}
