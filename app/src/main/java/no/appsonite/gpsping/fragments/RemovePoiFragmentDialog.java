package no.appsonite.gpsping.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.api.content.Poi;
import no.appsonite.gpsping.databinding.DialogFragmentRemovePoiBinding;
import no.appsonite.gpsping.viewmodel.RemovePoiFragmentViewModel;
import rx.Observable;
import rx.Observer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 10.02.2016
 */
public class RemovePoiFragmentDialog extends BaseBindingDialogFragment<DialogFragmentRemovePoiBinding, RemovePoiFragmentViewModel> {
    public static final String TAG = "RemovePoiFragmentDialog";
    private static final String ARG_POI = "arg_poi";

    public static RemovePoiFragmentDialog newInstance(Poi poi) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_POI, poi);
        RemovePoiFragmentDialog fragment = new RemovePoiFragmentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onViewModelCreated(RemovePoiFragmentViewModel model) {
        super.onViewModelCreated(model);
        model.poi.set((Poi) getArguments().getSerializable(ARG_POI));
    }

    @Override
    protected void onBeforeDialogCreated(AlertDialog.Builder dialogBuilder) {
        super.onBeforeDialogCreated(dialogBuilder);
        dialogBuilder.setPositiveButton(R.string.remove, null);
        dialogBuilder.setNegativeButton(android.R.string.cancel, null);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removePoi();
                    }
                });
            }
        });

        return dialog;
    }

    private void removePoi() {
        Observable<ApiAnswer> observable = getModel().removePoi();
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
                dismiss();
            }
        });
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.removePoiTitle);
    }


}
