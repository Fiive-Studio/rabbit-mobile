package diso.rabbit.gui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import diso.rabbit.R;
import diso.rabbit.controls.ButtonExtends;
import diso.rabbit.data.Alarm;
import diso.rabbit.data.AlarmDao;
import diso.rabbit.data.CourseDao;
import diso.rabbit.helper.Enums;
import diso.rabbit.helper.Utils;
import diso.rabbit.model.CourseModel;
import diso.rabbit.service.AlarmReceiver;
import diso.rabbit.service.AlarmUtil;

/**
 * Created by pabdiava on 12/03/2016.
 */
public class TaskAddEditGUI extends FragmentActivity {

    CourseDao coursesDao;
    AlarmDao alarmDao;
    Alarm alarm;
    ArrayAdapter<CourseModel> adapter;
    ArrayList<CourseModel> courses;
    Spinner spinner;
    ButtonExtends btnAlarmDate;
    EditText txtTitle, txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_add);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.task2);

        btnAlarmDate = (ButtonExtends)findViewById(R.id.btnAlarmDate);
        txtTitle = (EditText)findViewById(R.id.txtTitle);
        txtDescription = (EditText)findViewById(R.id.txtDescription);

        coursesDao = Utils.GetDaoSession(this).getCourseDao();
        alarmDao = Utils.GetDaoSession(this).getAlarmDao();

        populateCourses();
        if(courses.size() == 0){
            Utils.ShowMessage(this, getString(R.string.task_course_validation));
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

    void setResult(){
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    void setButtonProperties() {
        btnAlarmDate.setText(Utils.getDateAndHour(btnAlarmDate.getCalendar(false)));
        btnAlarmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    void openDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_date_time, null);

        Calendar c = btnAlarmDate.getCalendar(false);
        final DatePicker dpAlarm = (DatePicker) dialogView.findViewById(R.id.dpAlarm);
        final TimePicker tpAlarm = (TimePicker) dialogView.findViewById(R.id.tpAlarm);

        dpAlarm.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        tpAlarm.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        tpAlarm.setCurrentMinute(c.get(Calendar.MINUTE));

        builder.setView(dialogView);
        builder.setTitle(this.getResources().getString(R.string.task_select_alarm))
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAlarmDate.setCalendar(Utils.getCalendar(dpAlarm.getYear(), dpAlarm.getMonth(), dpAlarm.getDayOfMonth(), tpAlarm.getCurrentHour(), tpAlarm.getCurrentMinute()));
                btnAlarmDate.setText(Utils.getDateAndHour(btnAlarmDate.getCalendar(false)));
                dialog.dismiss();
            }
        });
}

    void loadStartData(){
        Intent i = this.getIntent();

        if (i.getExtras() != null && i.getExtras().containsKey(Enums.Fields.AlarmId.toString())) {
            long id = i.getExtras().getLong(Enums.Fields.AlarmId.toString());

            alarm = alarmDao.queryBuilder().where(AlarmDao.Properties.Id.eq(id)).unique();
            setCourse(alarm.getIdCourse());
            btnAlarmDate.setCalendar(Utils.getCalendar(alarm.getAlarmDate()));
            txtTitle.setText(alarm.getTitle());
            txtDescription.setText(alarm.getDescription());
        }
        else{
            btnAlarmDate.getCalendar(false).add(Calendar.MINUTE, getResources().getInteger(R.integer.schedule_add_minutes));
        }

        setButtonProperties();
    }

    void setCourse(long id){
        for(int i = 0; i < adapter.getCount(); i++) {
            if(adapter.getItem(i).getId() == id){
                spinner.setSelection(i);
                break;
            }
        }
    }

    boolean save() {
        if (!validateTitle()){
            Utils.ShowMessage(getBaseContext(), getString(R.string.task_title_validation));
            return false;
        }

        if(alarm == null) {
            alarm = new Alarm(null, txtTitle.getText().toString(), txtDescription.getText().toString(), btnAlarmDate.getCalendar(false).getTime(), getCourse());
            alarmDao.insert(alarm);
            Utils.ShowMessage(this, String.format(getString(R.string.task_add), alarm.getCourse().getName()));
        }
        else{
            alarm.setTitle(txtTitle.getText().toString());
            alarm.setDescription(txtDescription.getText().toString());
            alarm.setAlarmDate(btnAlarmDate.getCalendar(false).getTime());
            alarm.setIdCourse(getCourse());
            alarmDao.update(alarm);
            Utils.ShowMessage(this, String.format(getString(R.string.task_edit), alarm.getCourse().getName()));
        }

        int result = btnAlarmDate.getCalendar(false).compareTo(Calendar.getInstance());
        if(result > 0) {
            AlarmUtil.setAlarm(this, getSystemService(ALARM_SERVICE), btnAlarmDate.getCalendar(false).getTimeInMillis(), alarm.getId());
        }

        return true;
    }

    long getCourse() {
        CourseModel c = (CourseModel) spinner.getSelectedItem();
        return c.getId();
    }

    boolean validateTitle(){
        if(txtTitle.getText().toString().trim().length() == 0){ return false; }
        return true;
    }
}
