package diso.rabbit.gui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import diso.rabbit.R;
import diso.rabbit.adapters.fragments.TimePickerFragment;
import diso.rabbit.controls.ButtonExtends;
import diso.rabbit.data.CourseDao;
import diso.rabbit.data.Schedule;
import diso.rabbit.data.ScheduleDao;
import diso.rabbit.helper.Enums;
import diso.rabbit.helper.Utils;
import diso.rabbit.model.CourseModel;

/**
 * Created by pabdiava on 25/02/2016.
 */
public class ScheduleAddEditGUI extends FragmentActivity implements TimePickerDialog.OnTimeSetListener {

    CourseDao coursesDao;
    ScheduleDao scheduleDao;
    ArrayAdapter<CourseModel> adapter;
    ArrayList<CourseModel> courses;
    ButtonExtends currentButton;
    ButtonExtends btnStar, btnEnd;
    Spinner spinner;
    long day;
    Schedule schedule;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_add);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.schedule);

        btnStar = (ButtonExtends) findViewById(R.id.btnStartHour);
        btnEnd = (ButtonExtends) findViewById(R.id.btnEndHour);
        btnEnd.getCalendar().add(Calendar.MINUTE, getResources().getInteger(R.integer.schedule_add_minutes));

        coursesDao = Utils.GetDaoSession(this).getCourseDao();
        scheduleDao = Utils.GetDaoSession(this).getScheduleDao();

        populateCourses();
        if(courses.size() == 0){
            Utils.ShowMessage(getBaseContext(), getString(R.string.schedule_course_validation));
            setResult();
        }
        loadStartData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_cancel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {
            case R.id.cancel:
                break;
            case R.id.add:
                if(!save()){ return false; }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        setResult();
        return true;
    }

    void populateCourses() {
        courses = Utils.GetCourses(coursesDao);

        spinner = (Spinner) findViewById(R.id.spCourses);
        adapter = new ArrayAdapter<CourseModel>(this, android.R.layout.simple_spinner_item, courses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    void setButtonsAction() {
        setButtonProperties(btnStar);
        setButtonProperties(btnEnd);
    }

    void setButtonProperties(final ButtonExtends b) {
        b.setText(Utils.getHour(b.getCalendar()));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialog(b);
            }
        });
    }

    void openTimeDialog(ButtonExtends b) {
        currentButton = b;
        TimePickerFragment dialog = new TimePickerFragment();
        dialog.setHourAndMinute(b.getCalendar().get(Calendar.HOUR_OF_DAY), b.getCalendar().get(Calendar.MINUTE));
        dialog.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        currentButton.setCalendar(Utils.getCalendar(hourOfDay, minute));
        currentButton.setText(Utils.getHour(currentButton.getCalendar()));
    }

    boolean save() {
        if (!validateDate()){
            Utils.ShowMessage(getBaseContext(), getString(R.string.schedule_hour_validation));
            return false;
        }

        if(schedule == null) {
            schedule = new Schedule(null, getDate(btnStar), getDate(btnEnd), getDay(), getCourse());
            scheduleDao.insert(schedule);
            Utils.ShowMessage(this, String.format(getString(R.string.schedule_add), schedule.getCourse().getName(),Utils.GetDayName(this, schedule.getDay().getName())));
        }
        else{
            schedule.setStartHour(getDate(btnStar));
            schedule.setEndHour(getDate(btnEnd));
            schedule.setIdCourse(getCourse());
            scheduleDao.update(schedule);
            Utils.ShowMessage(this, String.format(getString(R.string.schedule_edit), schedule.getCourse().getName(), Utils.GetDayName(this, schedule.getDay().getName())));
        }

        return true;
    }

    boolean validateDate(){
        int result = btnStar.getCalendar().compareTo(btnEnd.getCalendar());
        if(result > 0){ return false; }
        return true;
    }

    long getDay() {
        return day;
    }

    long getCourse() {
        CourseModel c = (CourseModel) spinner.getSelectedItem();
        return c.getId();
    }

    Date getDate(ButtonExtends b) {
        return b.getCalendar().getTime();
    }

    void loadStartData(){
        Intent i = this.getIntent();

        if (i.getExtras() != null && i.getExtras().containsKey(Enums.Fields.ScheduleId.toString())) {
            long id = i.getExtras().getLong(Enums.Fields.ScheduleId.toString());

            schedule = scheduleDao.queryBuilder().where(ScheduleDao.Properties.Id.eq(id)).unique();
            setCourse(schedule.getIdCourse());
            btnStar.setCalendar(Utils.getCalendar(schedule.getStartHour()));
            btnEnd.setCalendar(Utils.getCalendar(schedule.getEndHour()));
        }
        else{
            day = i.getExtras().getLong(Enums.Fields.DayId.toString());
        }

        setButtonsAction();
    }

    void setCourse(long id){
        for(int i = 0; i < adapter.getCount(); i++) {
            if(adapter.getItem(i).getId() == id){
                spinner.setSelection(i);
                break;
            }
        }
    }

    void setResult(){
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
