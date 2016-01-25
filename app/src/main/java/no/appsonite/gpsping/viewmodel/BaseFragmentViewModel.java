package no.appsonite.gpsping.viewmodel;

import android.app.Activity;
import android.content.Context;

import java.io.Serializable;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.model.SMS;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 23.12.2015
 */
public class BaseFragmentViewModel implements Serializable {

    private Activity activity;

    public Context getContext() {
        return Application.getContext();
    }

    public void onViewCreated() {

    }

    public void onDestroyView() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onModelAttached() {

    }
}
