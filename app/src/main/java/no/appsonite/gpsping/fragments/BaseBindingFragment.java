package no.appsonite.gpsping.fragments;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.activities.BaseActivity;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.utils.DataBindingUtils;
import no.appsonite.gpsping.utils.ProgressDialogFragment;
import no.appsonite.gpsping.utils.TypeResolver;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;
import retrofit.HttpException;


/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 10.11.2015
 */
public abstract class BaseBindingFragment<B extends ViewDataBinding, M extends BaseFragmentViewModel> extends Fragment {
    public static final String ADD_TO_BACK_STACK = "add_to_back_stack";
    private final Class<B> bindingClass;
    private final Class<M> modelClass;
    private B binding;
    private M model;
    private static final String EXTRA_MODEL = "extra_model";


    public BaseBindingFragment() {
        super();
        Class<?>[] typeArguments = TypeResolver.resolveRawArguments(BaseBindingFragment.class, getClass());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = (B) DataBindingUtils.getViewDataBinding(bindingClass, inflater, container);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createModel(savedInstanceState);
        initToolbar();
    }

    protected void initToolbar() {
        Toolbar toolbar = (Toolbar) binding.getRoot().findViewById(R.id.toolbar);
        if (toolbar == null)
            return;
        getBaseActivity().setSupportActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getTitle());
            boolean isBackStack = getArguments().getBoolean(ADD_TO_BACK_STACK, false);
            if (isBackStack) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            }
        }
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    protected ActionBar getActionBar() {
        return getBaseActivity().getSupportActionBar();
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

    public boolean onBackPressed() {
        return false;
    }

    protected abstract String getTitle();

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
                    ApiAnswer apiAnswer = new Gson().fromJson(message, ApiAnswer.class);
                    message = apiAnswer.getError();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }
}
