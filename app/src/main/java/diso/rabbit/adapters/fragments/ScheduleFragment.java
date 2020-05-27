package diso.rabbit.adapters.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import diso.rabbit.R;
import diso.rabbit.data.Schedule;
import diso.rabbit.data.ScheduleDao;
import diso.rabbit.gui.DaysGUI;
import diso.rabbit.gui.ScheduleAddEditGUI;
import diso.rabbit.helper.Enums;
import diso.rabbit.helper.Utils;
import diso.rabbit.model.ScheduleModel;

/**
 * Created by pabdiava on 6/03/2016.
 */
public class ScheduleFragment extends Fragment {
    ArrayAdapter<ScheduleModel> adapter;
    ScheduleDao scheduleDao;
    private long idDay;
    boolean isResume;
    View view;
    private FragmentActivity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.schedule_frag, container, false);
        scheduleDao = Utils.GetDaoSession(view.getContext()).getScheduleDao();
        showSchedule();

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getContext().getMenuInflater();
        inflater.inflate(R.menu.menu_contextual_base, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (getUserVisibleHint()) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            ScheduleModel model = (ScheduleModel) adapter.getItem(info.position);

            switch (item.getItemId()) {
                case R.id.edit:
                    showEditScreen(model.getId());
                    return true;
                case R.id.delete:
                    confirmDelete(info.targetView.getContext(), model);
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        } else {
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isResume) {
            showSchedule();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isResume = true;
    }

    private void showSchedule() {
        adapter = new ArrayAdapter<ScheduleModel>(view.getContext(), android.R.layout.simple_list_item_1, Utils.GetScheduleByIdDay(scheduleDao, getIdDay()));
        final ListView lstCourses = (ListView) view.findViewById(R.id.lstCourses);
        lstCourses.setAdapter(adapter);
        registerForContextMenu(lstCourses);

        lstCourses.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ScheduleModel model = (ScheduleModel)parent.getItemAtPosition(position);
                        showEditScreen(model.getId());
                    }
                }
        );
    }

    private void confirmDelete(Context context, final ScheduleModel scheduleModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.courses))
                .setMessage(context.getResources().getString(R.string.schedule_delete_message))
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(context.getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        delete(scheduleModel);
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

    void delete(ScheduleModel model) {
        String nameCourse = model.getCourseName();
        Log.d("Dao", "Schedule: " + nameCourse);
        Schedule schedule = scheduleDao.queryBuilder().where(ScheduleDao.Properties.Id.eq(model.getId())).unique();

        Log.d("Dao", "delete Schedule, ID: " + model.getId());
        scheduleDao.delete(schedule);

        adapter.remove(model);
        adapter.notifyDataSetChanged();
        Utils.ShowMessage(view.getContext(), String.format("%s '%s'", getString(R.string.courses_delete), nameCourse));
    }

    void showEditScreen(long idSchedule){
        Intent intent = new Intent(getContext(), ScheduleAddEditGUI.class);
        Bundle bundle = new Bundle();
        bundle.putLong(Enums.Fields.ScheduleId.toString(), idSchedule);
        intent.putExtras(bundle);

        startActivityForResult(intent, 0);
    }

    // Properties
    public long getIdDay() {
        return idDay;
    }

    public void setIdDay(long idDay) {
        this.idDay = idDay;
    }

    public FragmentActivity getContext() {
        return context;
    }

    public void setContext(FragmentActivity context) {
        this.context = context;
    }
}