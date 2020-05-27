package diso.rabbit.model;

import java.util.ArrayList;

import diso.rabbit.data.Day;

/**
 * Created by pabdiava on 1/04/2016.
 */
public class NextScheduleModel {
    private Day day;
    private ArrayList<ScheduleModel> schedule;

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public ArrayList<ScheduleModel> getSchedule() {
        return schedule;
    }

    public void setSchedule(ArrayList<ScheduleModel> schedule) {
        this.schedule = schedule;
    }
}
