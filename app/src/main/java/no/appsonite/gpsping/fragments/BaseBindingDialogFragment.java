package no.appsonite.gpsping.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.activities.BaseActivity;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.utils.DataBindingUtils;
import no.appsonite.gpsping.utils.ProgressDialogFragment;
import no.appsonite.gpsping.utils.TypeResolver;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;
import retrofit.HttpException;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public abstract class BaseBindingDialogFragment<B extends ViewDataBinding, M extends BaseFragmentViewModel> extends DialogFragment {
    private final Class<B> bindingClass;
    private final Class<M> modelClass;
    private B binding;
    private M model;
    private static final String EXTRA_MODEL = "extra_model";

    private DialogInterface.OnDismissListener onDismissListener;

    public BaseBindingDialogFragment() {
        Class<?>[] typeArguments = TypeResolver.resolveRawArguments(BaseBindingDialogFragment.class, getClass());
        this.bindingClass = (Class<B>) typeArguments[0];
        this.modelClass = (Class<M>) typeArguments[1];
    }

    @Override
    public void onResume() {
        super.onResume();
        model.onResume();
    }

    public abstract String getFragmentTag();

    protected B getBinding() {
        return binding;
    }

    protected M getModel() {
        return model;
    }

    public DialogInterface.OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = (B) DataBindingUtils.getViewDataBinding(bindingClass, LayoutInflater.from(getActivity()), null);
        createModel(savedInstanceState);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), getStyle())
                .setTitle(getTitle())
                .setView(binding.getRoot());
        onBeforeDialogCreated(alertDialog);
        AlertDialog dialog = alertDialog.create();
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null)
            onDismissListener.onDismiss(dialog);
    }

    protected void hideProgress() {
        if (getBaseActivity() == null)
            return;
        ProgressDialogFragment.hide(getBaseActivity());
    }

    protected void showProgress() {
        if (getBaseActivity() == null)
            return;
        ProgressDialogFragment.show(getBaseActivity());
    }

    protected void showError(Throwable e) {
        if (getActivity() != null) {
            hideProgress();
            String message = e.getMessage();
            if (e instanceof HttpException) {
                try {
                    message = ((HttpException) e).response().errorBody().string();
                    ApiAnswer apiAnswer = ApiFactory.getGson().fromJson(message, ApiAnswer.class);
                    message = apiAnswer.getError().get();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            if (TextUtils.isEmpty(message)) {
                message = getString(R.string.error_code_208);
            }
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }


    protected int getStyle() {
        return R.style.AppTheme_Dialog;
    }

    protected abstract String getTitle();


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void createModel(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            model = (M) savedInstanceState.getSerializable(EXTRA_MODEL);
        }
        if (model == null) {
            try {
                model = modelClass.newInstance();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        binding.setVariable(no.appsonite.gpsping.BR.viewModel, model);
        model.onViewCreated();
        onViewModelCreated(model);
        model.onModelAttached();
    }

    protected void onBeforeDialogCreated(AlertDialog.Builder dialog) {

    }

    protected void onViewModelCreated(M model) {

    }

    @Override
    public void onPause() {
        super.onPause();
        model.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        model.onDestroyView();
    }
}
