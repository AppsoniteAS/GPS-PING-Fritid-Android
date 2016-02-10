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
import no.appsonite.gpsping.databinding.DialogFragmentEditPoiBinding;
import no.appsonite.gpsping.utils.Utils;
import no.appsonite.gpsping.viewmodel.PoiFragmentViewModel;
import rx.Observable;
import rx.Observer;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 10.02.2016
 */
public class EditPoiDialogFragment extends BaseBindingDialogFragment<DialogFragmentEditPoiBinding, PoiFragmentViewModel> {
    public static final String TAG = "EditPoiDialogFragment";
    private static final String ARG_POI = "arg_poi";

    public static EditPoiDialogFragment newInstance(Poi poi) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_POI, poi);
        EditPoiDialogFragment fragment = new EditPoiDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return getString(getModel().isNew() ? R.string.createPoiTitle : R.string.editPoiTitle);
    }

    @Override
    protected void onViewModelCreated(PoiFragmentViewModel model) {
        super.onViewModelCreated(model);
        model.poi.set((Poi) getArguments().getSerializable(ARG_POI));
        Utils.showKeyboard(getActivity(), getBinding().poiName);
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
                        savePoi();
                    }
                });
            }
        });

        return dialog;
    }



    private void savePoi() {
        Observable<ApiAnswer> observable = getModel().savePoi();
        if (observable == null)
            return;
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
    protected void onBeforeDialogCreated(AlertDialog.Builder dialogBuilder) {
        super.onBeforeDialogCreated(dialogBuilder);
        dialogBuilder.setPositiveButton(getModel().isNew() ? R.string.save : R.string.update, null);
        dialogBuilder.setNegativeButton(android.R.string.cancel, null);
    }

}
