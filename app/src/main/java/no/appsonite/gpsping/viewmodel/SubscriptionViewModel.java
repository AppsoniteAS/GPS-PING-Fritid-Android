package no.appsonite.gpsping.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import no.appsonite.gpsping.Application;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.02.2016
 */
public class SubscriptionViewModel extends BaseFragmentViewModel implements BillingProcessor.IBillingHandler {
    private static final String SUBSCRIPTION_ID = "abononnementmnd";
    //    private static final String SUBSCRIPTION_ID = "monthsub";
    //    private static final String SUBSCRIPTION_ID = "android.test.purchased";
    BillingProcessor billingProcessor;
    private static final String APP_KEY = null;
    private final Subject<Object, Object> inAppSubject = new SerializedSubject<>(PublishSubject.create());

    public boolean isSubscribed() {
        return getPrefs().getBoolean("subscription", false);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        billingProcessor = new BillingProcessor(Application.getContext(), APP_KEY, this);
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        getPrefs().edit().putBoolean("subscription", true).apply();
    }

    private SharedPreferences getPrefs() {
        return getContext().getSharedPreferences("Purchase", Context.MODE_PRIVATE);
    }

    @Override
    public void onPurchaseHistoryRestored() {
        updateSubscribePrefs();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {
        inAppSubject.onNext("Initialized");
        billingProcessor.getSubscriptionListingDetails(SUBSCRIPTION_ID);
        updateSubscribePrefs();
        if (billingListener != null)
            billingListener.onInit();
    }

    public boolean isBillingInit() {
        return billingProcessor.isInitialized();
    }

    public interface BillingListener {
        void onInit();
    }

    private BillingListener billingListener;

    public void setBillingListener(BillingListener billingListener) {
        this.billingListener = billingListener;
    }

    private void updateSubscribePrefs() {
        getPrefs().edit().putBoolean("subscription", billingProcessor.isSubscribed(SUBSCRIPTION_ID)).apply();
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            return billingProcessor.handleActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            return false;
        }
    }

    public void requestSubscription(final Activity activity) {
        if (billingProcessor.isInitialized()) {
            subscribe(activity);
        } else {
            inAppSubject.subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    subscribe(activity);
                }
            });
        }
    }

    private void subscribe(Activity activity) {
        billingProcessor.subscribe(activity, SUBSCRIPTION_ID);
    }
}
