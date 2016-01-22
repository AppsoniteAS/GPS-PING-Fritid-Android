package no.appsonite.gpsping.viewmodel;

import android.databinding.Observable;
import android.databinding.ObservableField;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import no.appsonite.gpsping.utils.ObservableString;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 23.12.2015
 */
public class CalendarDialogFragmentViewModel extends BaseFragmentViewModel {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
    public ArrayList<SimpleDate> eventList = new ArrayList<>();
    public ObservableField<SimpleDate> selectedDate = new ObservableField<>();
    public ObservableString currentMonth = new ObservableString();

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
            }
        });

        initFakeEvents();

        Calendar calendar = Calendar.getInstance();
        selectedDate.set(new SimpleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
    }

    private void initFakeEvents() {
        Calendar calendar = Calendar.getInstance();
        eventList.add(new SimpleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));

        for (int i = 0; i < 30; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, new Random().nextInt(30) * ((i % 2 == 0) ? -1 : 1));
            eventList.add(new SimpleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
        }
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
