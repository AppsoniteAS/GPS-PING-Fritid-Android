package no.appsonite.gpsping.viewmodel;

import android.content.Context;


import java.io.Serializable;

import no.appsonite.gpsping.Application;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 23.12.2015
 */
public class BaseFragmentViewModel implements Serializable {

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

    public void onModelAttached(){

    }
}
