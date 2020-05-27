package diso.rabbit.model;

import diso.rabbit.data.Schedule;
import diso.rabbit.helper.Utils;

/**
 * Created by pabdiava on 6/03/2016.
 */
public class ScheduleModel {
    private Schedule schedule;

    public ScheduleModel(Schedule obj) {
        schedule = obj;
    }

    public long getId() {
        return schedule.getId();
    }

    public void setId(long id) {
        schedule.setId(id);
    }

    public String getCourseName() {
        return schedule.getCourse().getName();
    }

    @Override
    public String toString() {
        String start = Utils.getHour(schedule.getStartHour().getHours(), schedule.getStartHour().getMinutes());
        String end = Utils.getHour(schedule.getEndHour().getHours(), schedule.getEndHour().getMinutes());

        return String.format("[ %s - %s ] %s", start, end, getCourseName());
    }
}
