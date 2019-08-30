package no.appsonite.gpsping.viewmodel;

import android.databinding.ObservableField;

import no.appsonite.gpsping.fragments.FAQFragment;

/**
 * Created: Belozerov Sergei
 * E-mail: belozerow@gmail.com
 * Company: APPGRANULA LLC
 * Date: 07/09/16
 */
public class FAQDetailsFragmentViewModel extends BaseFragmentViewModel{
    public ObservableField<FAQFragment.FAQItem> faq = new ObservableField<>();
}
