package diso.rabbit.adapters.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

import diso.rabbit.gui.ScheduleAddEditGUI;

/**
 * Created by pabdiava on 10/03/2016.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    int hourOfDay = 8, minute = 0;

    public void setHourAndMinute(int hourOfDay, int minute){
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        return new TimePickerDialog(getActivity(), (ScheduleAddEditGUI)getActivity(), hourOfDay, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    }
}