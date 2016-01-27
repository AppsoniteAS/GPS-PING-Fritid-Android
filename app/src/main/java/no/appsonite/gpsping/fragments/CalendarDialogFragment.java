package no.appsonite.gpsping.fragments;

import android.app.Activity;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.p_v.flexiblecalendar.FlexibleCalendarView;
import com.p_v.flexiblecalendar.entity.Event;
import com.p_v.flexiblecalendar.view.BaseCellView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.databinding.DialogFragmentCalendarBinding;
import no.appsonite.gpsping.viewmodel.CalendarDialogFragmentViewModel;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 22.01.2016
 */
public class CalendarDialogFragment extends BaseBindingDialogFragment<DialogFragmentCalendarBinding, CalendarDialogFragmentViewModel> {
    public static final String TAG = "CalendarDialogFragment";
    private static final String ARG_FRIEND_ID = "arg_friend_id";
    private static final String ARG_DATE = "arg_date";

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    public interface CalendarListener {
        void onDateSelected(Date date);
    }

    private CalendarListener calendarListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof CalendarListener) {
            calendarListener = (CalendarListener) activity;
        } else if (getParentFragment() instanceof CalendarListener) {
            calendarListener = (CalendarListener) getParentFragment();
        }
    }

    @Override
    protected String getTitle() {
        return null;
    }

    public static CalendarDialogFragment newInstance(Long friendId, Date date) {
        Bundle args = new Bundle();
        CalendarDialogFragment fragment = new CalendarDialogFragment();
        args.putLong(ARG_FRIEND_ID, friendId);
        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onViewModelCreated(CalendarDialogFragmentViewModel model) {
        super.onViewModelCreated(model);

        getBinding().touchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        initCalendar();

        initButtons();
    }

    private void initButtons() {
        getBinding().closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getBinding().confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarListener != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, getModel().selectedDate.get().year);
                    calendar.set(Calendar.MONTH, getModel().selectedDate.get().month);
                    calendar.set(Calendar.DAY_OF_MONTH, getModel().selectedDate.get().day);
                    calendarListener.onDateSelected(calendar.getTime());
                }
                dismiss();
            }
        });

        getBinding().nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBinding().calendarView.moveToNextMonth();
            }
        });

        getBinding().prevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBinding().calendarView.moveToPreviousMonth();
            }
        });
    }

    private void initCalendar() {
        getModel().friendId.set(getArguments().getLong(ARG_FRIEND_ID));

        getBinding().calendarView.setShowDatesOutsideMonth(true);

        getBinding().calendarView.setOnDateClickListener(new FlexibleCalendarView.OnDateClickListener() {
            @Override
            public void onDateClick(int year, int month, int day) {
                getModel().setDate(year, month, day);
            }
        });

        getBinding().calendarView.setOnMonthChangeListener(new FlexibleCalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month, int direction) {
                getModel().setDate(year, month, 1);
            }
        });

        getBinding().calendarView.setEventDataProvider(new FlexibleCalendarView.EventDataProvider() {
            @Override
            public List<? extends Event> getEventsForTheDay(int year, int month, int day) {
                boolean isEvent = getModel().isEvent(year, month, day);
                if (isEvent) {
                    List<CustomEvent> customEvents = new ArrayList<>();
                    customEvents.add(new CustomEvent(R.color.colorEvent));
                    return customEvents;
                }
                return null;
            }
        });

        getBinding().calendarView.setCalendarView(new FlexibleCalendarView.CalendarView() {
            @Override
            public BaseCellView getCellView(int position, View convertView, ViewGroup parent, int cellType) {
                BaseCellView cellView = (BaseCellView) convertView;
                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    cellView = (BaseCellView) inflater.inflate(R.layout.calendar3_date_cell_view, null);
                    if (cellType == BaseCellView.OUTSIDE_MONTH) {
                        cellView.setTextColor(getContext().getResources().getColor(R.color.calendarOutMonth));
                    } else {
                        cellView.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
                    }
                }
                return cellView;
            }

            @Override
            public BaseCellView getWeekdayCellView(int position, View convertView, ViewGroup parent) {
                return null;
            }

            @Override
            public String getDayOfWeekDisplayValue(int dayOfWeek, String defaultValue) {
                return null;
            }
        });

        getModel().eventList.addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {

            }

            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
                getBinding().calendarView.refresh();
            }

            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {

            }

            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {

            }

            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
                getBinding().calendarView.refresh();
            }
        });

        getBinding().calendarView.post(new Runnable() {
            @Override
            public void run() {
                Date date = (Date) getArguments().getSerializable(ARG_DATE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                getModel().setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                getBinding().calendarView.selectDate(date);
                getBinding().calendarView.selectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            }
        });
    }

    public class CustomEvent implements Event {

        private int color;

        public CustomEvent(int color) {
            this.color = color;
        }

        @Override
        public int getColor() {
            return color;
        }
    }

    @Override
    protected int getStyle() {
        return R.style.AppTheme_Dialog_FullScreen;
    }
}
