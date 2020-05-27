package diso.rabbit.gui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import diso.rabbit.R;
import diso.rabbit.adapters.ListAdapter;
import diso.rabbit.data.Alarm;
import diso.rabbit.data.AlarmDao;
import diso.rabbit.data.Course;
import diso.rabbit.data.CourseDao;
import diso.rabbit.helper.Enums;
import diso.rabbit.helper.Utils;
import diso.rabbit.model.CourseModel;
import diso.rabbit.model.TaskModel;
import diso.rabbit.service.AlarmUtil;

/**
 * Created by pabdiava on 19/03/2016.
 */
public class TasksGUI extends Activity {

    AlarmDao alarmDao;
    CourseDao courseDao;
    boolean isResume;
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.task);

        alarmDao = Utils.GetDaoSession(this).getAlarmDao();
        courseDao = Utils.GetDaoSession(this).getCourseDao();
        ShowTasks();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isResume) {
            ShowTasks();
            isResume = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isResume = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addCourse:
                Intent intent = new Intent(TasksGUI.this, TaskAddEditGUI.class);
                startActivityForResult(intent, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);

        if (type == 1) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_contextual_base, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);
        TaskModel task = (TaskModel)adapter.getChild(groupPosition, childPosition);

        switch (item.getItemId()) {
            case R.id.edit:
                showEditScreen(task.getId());
                return true;
            case R.id.delete:
                confirmDelete(info.targetView.getContext(), task, groupPosition, childPosition);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    void ShowTasks() {

        List<Course> courses = courseDao.loadAll();
        List<CourseModel> parents = new ArrayList<CourseModel>();
        List<List<?>> children = new ArrayList<>();
        int i = 0;

        for (Course course : courses) {
            List<TaskModel> tasks = Utils.GetTasks(alarmDao.queryBuilder().where(AlarmDao.Properties.IdCourse.eq(course.getId())).orderAsc(AlarmDao.Properties.AlarmDate).list(), false);

            if (tasks.size() != 0) {
                parents.add(new CourseModel(course));
                children.add(tasks);
                i++;
            }
        }

        adapter = new ListAdapter(this, this, parents, children);
        final ExpandableListView lstTasks = (ExpandableListView) findViewById(R.id.lstAlarms);
        lstTasks.setAdapter(adapter);
        registerForContextMenu(lstTasks);

        lstTasks.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                TaskModel t = (TaskModel)adapter.getChild(groupPosition, childPosition);
                showEditScreen(t.getId());
                return true;
            }
        });
    }

    private void confirmDelete(Context context, final TaskModel taskModel, final int groupPosition, final int childPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.courses))
                .setMessage(context.getResources().getString(R.string.task_delete_message))
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(context.getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        delete(taskModel, groupPosition, childPosition);
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

    void delete(TaskModel model, int groupPosition, int childPosition) {
        String course = model.getAlarm().getCourse().getName();
        Log.d("Dao", "delete Alarm, ID: " + model.getId());
        long id = model.getAlarm().getId();
        alarmDao.delete(model.getAlarm());
        AlarmUtil.cancelAlarm(this, getSystemService(ALARM_SERVICE), id);
        AlarmUtil.cancelNotification(this, id);

        adapter.remove(groupPosition, childPosition);
        adapter.notifyDataSetChanged();
        Utils.ShowMessage(getBaseContext(), String.format(getString(R.string.task_delete), course));
    }

    void showEditScreen(long idAlarm){
        Intent intent = new Intent(getBaseContext(), TaskAddEditGUI.class);
        Bundle bundle = new Bundle();
        bundle.putLong(Enums.Fields.AlarmId.toString(), idAlarm);
        intent.putExtras(bundle);

        startActivityForResult(intent, 0);
    }
}
