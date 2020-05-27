package diso.rabbit.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import diso.rabbit.R;
import diso.rabbit.data.Alarm;
import diso.rabbit.data.AlarmDao;
import diso.rabbit.data.CourseDao;
import diso.rabbit.data.Course;
import diso.rabbit.data.DaoMaster;
import diso.rabbit.data.DaoSession;
import diso.rabbit.data.Day;
import diso.rabbit.data.DayDao;
import diso.rabbit.data.Schedule;
import diso.rabbit.data.ScheduleDao;
import diso.rabbit.model.CourseModel;
import diso.rabbit.model.DayModel;
import diso.rabbit.model.NextScheduleModel;
import diso.rabbit.model.ScheduleModel;
import diso.rabbit.model.TaskModel;

/**
 * Created by pabdiava on 5/02/2016.
 */
public class Utils {
    public static String GetText(Context context, int id) {
        return context.getResources().getString(id);
    }

    public static void ShowMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getHour() {
        final Calendar c = Calendar.getInstance();
        return getHour(c);
    }

    public static String getHour(int hourOfDay, int minute) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        return getHour(c);
    }

    public static String getDateAndHour(Calendar c) {
        String sHour = getHour(c);

        String sDay = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        if (sDay.length() == 1) {
            sDay = "0" + sDay;
        }

        String sMonth = Integer.toString(c.get(Calendar.MONTH) + 1); // Add 1 because the months start in 0
        if (sMonth.length() == 1) {
            sMonth = "0" + sMonth;
        }

        String sYear = Integer.toString(c.get(Calendar.YEAR));

        return String.format("%s/%s/%s %s", sDay, sMonth, sYear, sHour);
    }

    public static String getHour(Calendar c) {
        String sHour = Integer.toString(c.get(Calendar.HOUR));
        if (sHour == "0") {
            sHour = "12";
        }

        String sMinute = Integer.toString(c.get(Calendar.MINUTE));
        if (sMinute.length() == 1) {
            sMinute = "0" + sMinute;
        }

        String ampm = "AM";
        if (c.get(Calendar.AM_PM) == Calendar.PM) {
            ampm = "PM";
        }

        return String.format("%s:%s %s", sHour, sMinute, ampm);
    }

    public static Calendar getCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        final Calendar c = getCalendar(hourOfDay, minute);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        return c;
    }

    public static Calendar getCalendar(int hourOfDay, int minute) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        return c;
    }

    public static Calendar getCalendar(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c;
    }

    public static ArrayList<CourseModel> GetCourses(CourseDao coursesDao) {
        List<Course> data = coursesDao.loadAll();
        ArrayList<CourseModel> result = new ArrayList<CourseModel>();

        for (Course courses : data) {
            result.add(new CourseModel(courses));
        }

        return result;
    }

    public static ArrayList<DayModel> GetDays(DayDao daysDao) {
        List<Day> data = daysDao.loadAll();
        ArrayList<DayModel> result = new ArrayList<DayModel>();

        for (Day days : data) {
            result.add(new DayModel(days));
        }

        return result;
    }

    public static ArrayList<TaskModel> GetTasks(List<Alarm> tasks, boolean showCourse) {
        ArrayList<TaskModel> result = new ArrayList<TaskModel>();

        for (Alarm task : tasks) {
            TaskModel t = new TaskModel(task);
            t.showCourseInToString(false);
            result.add(t);
        }

        return result;
    }

    public static ArrayList<ScheduleModel> GetScheduleByIdDay(ScheduleDao schedulesDao, long dayId) {
        List<Schedule> data = schedulesDao.queryBuilder()
                .where(ScheduleDao.Properties.IdDay.eq(dayId))
                .orderAsc(ScheduleDao.Properties.StartHour)
                .list();

        ArrayList<ScheduleModel> result = new ArrayList<ScheduleModel>();
        for (Schedule schedule : data) {
            result.add(new ScheduleModel(schedule));
        }

        return result;
    }

    public static NextScheduleModel GetNextSchedule(ScheduleDao schedulesDao, DayDao daysDao) {
        final Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DAY_OF_WEEK);

        // this logic is because in Rabbit the week start in monday
        if (currentDay == Calendar.SUNDAY) {
            currentDay = 7;
        } else {
            currentDay--;
        }

        List<Schedule> data = null;
        for (int i = 0; i < 7; i++) {
            data = schedulesDao.queryBuilder()
                    .where(ScheduleDao.Properties.IdDay.eq(currentDay))
                    .orderAsc(ScheduleDao.Properties.StartHour)
                    .list();

            if (data.size() != 0) {
                break;
            } else {
                if(currentDay == 7){ currentDay = 1; }
                else{ currentDay++; }
            }
        }

        NextScheduleModel scheduleModel = new NextScheduleModel();
        ArrayList<ScheduleModel> result = new ArrayList<ScheduleModel>();

        if(data != null) {
            for (Schedule schedule : data) {
                result.add(new ScheduleModel(schedule));
            }

            if(result.size() != 0){
                scheduleModel.setDay(daysDao.queryBuilder().where(DayDao.Properties.Id.eq(currentDay)).unique());
            }
        }

        scheduleModel.setSchedule(result);
        return scheduleModel;
    }

    public static ArrayList<TaskModel> GetNextTasks(AlarmDao alarmDao) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        Date startDay = c.getTime();

        c.add(Calendar.DATE, 7);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        Date finalDay = c.getTime();

        ArrayList<TaskModel> result = new ArrayList<TaskModel>();
        List<Alarm> data = alarmDao.queryBuilder()
                .where(AlarmDao.Properties.AlarmDate.between(startDay, finalDay))
                .orderAsc(AlarmDao.Properties.AlarmDate)
                .list();

        if(data != null) {
            for (Alarm alarm : data) {
                result.add(new TaskModel(alarm));
            }
        }

        return result;
    }

    public static String GetDayName(Context context, String key){
        String dayName = "";

        switch (key){
            case "LU":
                dayName = context.getString(R.string.LU);
                break;
            case "MA":
                dayName = context.getString(R.string.MA);
                break;
            case "MI":
                dayName = context.getString(R.string.MI);
                break;
            case "JU":
                dayName = context.getString(R.string.JU);
                break;
            case "VI":
                dayName = context.getString(R.string.VI);
                break;
            case "SA":
                dayName = context.getString(R.string.SA);
                break;
            case "DO":
                dayName = context.getString(R.string.DO);
                break;
        }

        return dayName;
    }

    // Vars
    static DaoMaster.DevOpenHelper _daoHelper;
    static SQLiteDatabase _dbLite;
    static DaoMaster _daoMaster;
    static DaoSession _daoSession;

    public static DaoSession GetDaoSession(Context context) {
        if (_daoHelper == null) {
            _daoHelper = new DaoMaster.DevOpenHelper(context, Utils.GetText(context, R.string.database_name), null);
            _dbLite = _daoHelper.getWritableDatabase();
            _daoMaster = new DaoMaster(_dbLite);
            _daoSession = _daoMaster.newSession();
        }

        return _daoSession;
    }
}
