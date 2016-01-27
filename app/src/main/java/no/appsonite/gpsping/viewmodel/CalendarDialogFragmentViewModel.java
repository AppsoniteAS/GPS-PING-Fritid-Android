package no.appsonite.gpsping.viewmodel;

import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableLong;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.content.geo.GeoDatesAnswer;
import no.appsonite.gpsping.utils.ObservableString;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 23.12.2015
 */
public class CalendarDialogFragmentViewModel extends BaseFragmentViewModel {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
    public ObservableArrayList<SimpleDate> eventList = new ObservableArrayList<>();
    public ObservableField<SimpleDate> selectedDate = new ObservableField<>();
    public ObservableString currentMonth = new ObservableString();
    public ObservableField<Date> currentDate = new ObservableField<>(new Date());
    public ObservableLong friendId = new ObservableLong(-1);

    public CalendarDialogFragmentViewModel() {
        super();

        selectedDate.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Calendar currentMonthCalendar = Calendar.getInstance();
                currentMonthCalendar.set(Calendar.YEAR, selectedDate.get().year);
                currentMonthCalendar.set(Calendar.MONTH, selectedDate.get().month);
                currentMonthCalendar.set(Calendar.DAY_OF_MONTH, selectedDate.get().day);
                currentMonth.set(dateFormat.format(currentMonthCalendar.getTime()));
                currentDate.set(currentMonthCalendar.getTime());
            }
        });

        initFakeEvents();

        Calendar calendar = Calendar.getInstance();
        selectedDate.set(new SimpleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
    }

    private void initFakeEvents() {
//        Calendar calendar = Calendar.getInstance();
//        eventList.add(new SimpleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
//
//        for (int i = 0; i < 30; i++) {
//            calendar.add(Calendar.DAY_OF_MONTH, new Random().nextInt(30) * ((i % 2 == 0) ? -1 : 1));
//            eventList.add(new SimpleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
//        }
    }

    @Override
    public void onModelAttached() {
        super.onModelAttached();
        requestDates();

        currentDate.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                requestDates();
            }
        });
    }

    private void requestDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate.get());
        calendar.add(Calendar.MONTH, -3);
        long from = calendar.getTimeInMillis() / 1000l;
        calendar.add(Calendar.MONTH, 6);
        long to = calendar.getTimeInMillis() / 1000l;
        rx.Observable<GeoDatesAnswer> observable;
        if (friendId.get() == -1) {
            observable = ApiFactory.getService().getGeoDates(from, to);
        } else {
            observable = ApiFactory.getService().getGeoDates(from, to, friendId.get());
        }
        observable
                .cache()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeoDatesAnswer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(GeoDatesAnswer geoDatesAnswer) {
                        ArrayList<Long> timeStamps = geoDatesAnswer.getDates();
                        Calendar calendar = Calendar.getInstance();
                        eventList.clear();
                        for (Long timeStamp : timeStamps) {
                            calendar.setTimeInMillis(timeStamp * 1000l);
                            eventList.add(new SimpleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
                        }
                    }
                });

    }

    public boolean isEvent(int year, int month, int day) {
        return eventList.contains(new SimpleDate(year, month, day));
    }

    public void setDate(int year, int month, int day) {
        selectedDate.set(new SimpleDate(year, month, day));
    }

    public class SimpleDate {
        public int year;
        public int month;
        public int day;

        public SimpleDate(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SimpleDate that = (SimpleDate) o;

            if (year != that.year) return false;
            if (month != that.month) return false;
            return day == that.day;

        }

        @Override
        public int hashCode() {
            int result = year;
            result = 31 * result + month;
            result = 31 * result + day;
            return result;
        }
    }
}
