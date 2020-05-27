package diso.rabbit.model;

import java.util.Calendar;
import diso.rabbit.data.Alarm;
import diso.rabbit.helper.Utils;

/**
 * Created by pabdiava on 19/03/2016.
 */
public class TaskModel {
    Alarm alarm;
    boolean showCourse;

    public TaskModel(Alarm obj) {
        alarm = obj;
        showCourse = true;
    }

    public void showCourseInToString(boolean show){showCourse = show; }

    public Alarm getAlarm(){ return alarm; }

    public long getId() {
        return alarm.getId();
    }

    public String toString() {
        return toString(showCourse);
    }

    public String toString(boolean showCourse) {
        Calendar c =  Calendar.getInstance();
        c.setTime(alarm.getAlarmDate());
        String time = Utils.getDateAndHour(c);
        if(showCourse) {
            return String.format("%s%n[ %s ] %s", alarm.getTitle(), time, alarm.getCourse().getName());
        }
        else{
            return String.format("%s%n[ %s ]", alarm.getTitle(), time);
        }
    }
}
