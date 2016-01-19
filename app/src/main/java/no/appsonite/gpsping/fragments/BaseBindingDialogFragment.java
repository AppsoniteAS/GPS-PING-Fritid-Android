package no.appsonite.gpsping.fragments;

import android.app.Dialog;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.utils.DataBindingUtils;
import no.appsonite.gpsping.utils.TypeResolver;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = (B) DataBindingUtils.getViewDataBinding(bindingClass, LayoutInflater.from(getActivity()), null);
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog)
                .setTitle(getTitle())
                .setView(binding.getRoot())
                .create();
        createModel(savedInstanceState);
        return alertDialog;
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
