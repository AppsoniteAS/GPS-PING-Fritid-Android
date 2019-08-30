package no.appsonite.gpsping.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import no.appsonite.gpsping.R;

public class PermissionsDialogFragment extends DialogFragment {

    private DialogInterface.OnClickListener positiveButtonClickListener;
    private DialogInterface.OnClickListener negativeButtonClickListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setView(R.layout.fragment_permissions_explanation)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, positiveButtonClickListener)
                .setNegativeButton(R.string.no_thanks, negativeButtonClickListener)
                .create();
    }

    public void setPositiveButtonClickListener(DialogInterface.OnClickListener onClickListener) {
        this.positiveButtonClickListener = onClickListener;
    }

    public void setNegativeButtonClickListener(DialogInterface.OnClickListener onClickListener) {
        this.negativeButtonClickListener = onClickListener;
    }
}
