package no.appsonite.gpsping.services.push;

import com.google.android.gms.iid.InstanceIDListenerService;

import no.appsonite.gpsping.utils.PushHelper;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 29.01.2016
 */
public class GPInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        PushHelper.clearToken();
    }
}
