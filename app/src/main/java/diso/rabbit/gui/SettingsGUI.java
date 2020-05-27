package diso.rabbit.gui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import diso.rabbit.R;
import diso.rabbit.data.Alarm;
import diso.rabbit.data.AlarmDao;
import diso.rabbit.data.CourseDao;
import diso.rabbit.helper.Utils;
import diso.rabbit.model.TaskModel;
import diso.rabbit.service.AlarmUtil;

/**
 * Created by pabdiava on 20/03/2016.
 */
public class SettingsGUI extends Activity {

    AlarmDao alarmDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.settings);

        alarmDao = Utils.GetDaoSession(this).getAlarmDao();
        setButtonAction();
    }

    void setButtonAction(){
        final Button b = (Button)findViewById(R.id.btnRestart);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(v.getContext());
            }
        });
    }

    void openDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.settings))
                .setMessage(context.getResources().getString(R.string.settings_delete_message))
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(context.getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancelAlarms();
                        deleteData();
                        dialog.dismiss();
                    }
                });
        builder.setCancelable(false); // Sets whether this dialog is cancelable with the BACK key.
        builder.setNegativeButton(context.getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false); // Prevent Cancel outside dialog
        alert.show();
    }

    void deleteData(){
        Utils.GetDaoSession(this).getAlarmDao().deleteAll();
        Utils.GetDaoSession(this).getScheduleDao().deleteAll();
        Utils.GetDaoSession(this).getCourseDao().deleteAll();

        Utils.ShowMessage(this, getString(R.string.settings_message));
    }

    void cancelAlarms(){
        List<Alarm> data = alarmDao.queryBuilder()
                            .where(AlarmDao.Properties.AlarmDate.ge(Calendar.getInstance().getTime()))
                            .list();

        if(data != null) {
            for (Alarm alarm : data) {
                AlarmUtil.cancelAlarm(this, getSystemService(ALARM_SERVICE), alarm.getId());
            }
        }

        AlarmUtil.cancelAllNotifications(this);
    }

    void openUrl(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url)));
        startActivity(browserIntent);
    }
}
