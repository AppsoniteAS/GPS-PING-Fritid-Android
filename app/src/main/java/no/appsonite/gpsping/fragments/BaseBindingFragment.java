package no.appsonite.gpsping.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.activities.BaseActivity;
import no.appsonite.gpsping.activities.MainActivity;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.utils.DataBindingUtils;
import no.appsonite.gpsping.utils.ProgressDialogFragment;
import no.appsonite.gpsping.utils.RxBus;
import no.appsonite.gpsping.utils.TypeResolver;
import no.appsonite.gpsping.utils.bus.LogoutEvent;
import no.appsonite.gpsping.viewmodel.BaseFragmentViewModel;
import retrofit.HttpException;
import rx.Subscription;


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
    private Subscription logoutSubscription;


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

        subscribeForEvents();
    }

    private void subscribeForEvents() {


        logoutSubscription = RxBus.getInstance().register(LogoutEvent.class, logoutEvent -> {
            hideProgress();
            MainActivity.logout(getActivity());
            getActivity().finish();
            logoutSubscription.unsubscribe();
        });
    }

    public abstract String getFragmentTag();

    protected B getBinding() {
        return binding;
    }

    public M getModel() {
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
        Toolbar toolbar = binding.getRoot().findViewById(R.id.toolbar);
        if (toolbar == null)
            return;
        getBaseActivity().setSupportActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getTitle());
            boolean isBackStack = getArguments().getBoolean(ADD_TO_BACK_STACK, false);
            if (isBackStack) {
                setGoneNavigationBottom();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            } else {
                setVisibleNavigationBottom();
            }
        }
    }

    private void setVisibleNavigationBottom() {
        getBaseActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
    }

    private void setGoneNavigationBottom() {
        if (getBaseActivity().findViewById(R.id.bottom_navigation) != null) {
            getBaseActivity().findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
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

        unsubscribeFromEvents();
    }

    private void unsubscribeFromEvents() {
        if (logoutSubscription != null && !logoutSubscription.isUnsubscribed()) {
            logoutSubscription.unsubscribe();
        }
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
                    ApiAnswer apiAnswer = ApiFactory.getGson().fromJson(message, ApiAnswer.class);
                    message = apiAnswer.getError().get();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }

    protected void showInfoDeniedPermission(Context context, int CODE_PERMISSION, String... permissions) {
        boolean checked = true;
        for (String string : permissions) {
            checked = getPermission(string);
            if (!checked) {
                break;
            }
        }

        if (checked) {
            Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_SHORT).show();
        } else {
            showAlertDialogOfDeniedPermission(CODE_PERMISSION);
        }
    }

    private boolean getPermission(String string) {
        return shouldShowRequestPermissionRationale(string);
    }

    protected void showAlertDialogOfDeniedPermission(int CODE_PERMISSION) {
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle(R.string.permission_denied)
                .setMessage(R.string.permission_denied_info)
                .setPositiveButton(R.string.allow, (dialog, which) -> goToApplicationSettings(CODE_PERMISSION))
                .setNegativeButton(R.string.imsure, null)
                .show();
    }

    private void goToApplicationSettings(int CODE_PERMISSION) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, CODE_PERMISSION);
    }
}
