package diso.rabbit.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import java.util.Calendar;

import diso.rabbit.R;

/**
 * Created by pabdiava on 10/03/2016.
 */
public class ButtonExtends extends Button {

    private Calendar c = Calendar.getInstance();

    public ButtonExtends(Context context, AttributeSet attrs) {
        super(context, attrs);
        c.set(Calendar.SECOND, 0);
    }

    public Calendar getCalendar() {
        c.set(getResources().getInteger(R.integer.default_year), Calendar.JANUARY, getResources().getInteger(R.integer.default_day));
        return c;
    }

    public Calendar getCalendar(boolean setDefaultTime) {
        if(setDefaultTime){ return getCalendar(); }
        else{ return c; }
    }

    public void setCalendar(Calendar c) {
        this.c = c;
    }
}
