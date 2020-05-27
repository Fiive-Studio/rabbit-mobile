package diso.rabbit.adapters.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import diso.rabbit.R;
import diso.rabbit.data.AlarmDao;
import diso.rabbit.gui.TaskAddEditGUI;
import diso.rabbit.helper.Enums;
import diso.rabbit.helper.Utils;
import diso.rabbit.model.TaskModel;

/**
 * Created by pabdiava on 25/02/2016.
 */
public class TaskFragment extends Fragment {

    AlarmDao alarmDao;
    ArrayAdapter<TaskModel> adapterTasks;
    boolean isResume;
    View view;
    private FragmentActivity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.task_frag, container, false);
        alarmDao = Utils.GetDaoSession(view.getContext()).getAlarmDao();
        ShowTasks();

        return view;
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

    private void ShowTasks() {
        adapterTasks = new ArrayAdapter<TaskModel>(view.getContext(), android.R.layout.simple_list_item_1, Utils.GetNextTasks(alarmDao));
        final ListView lstTasks = (ListView) view.findViewById(R.id.lstTasks);
        lstTasks.setAdapter(adapterTasks);

        lstTasks.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TaskModel model = (TaskModel)parent.getItemAtPosition(position);
                        showEditScreen(model.getId());
                    }
                }
        );
    }

    void showEditScreen(long idAlarm){
        Intent intent = new Intent(getContext(), TaskAddEditGUI.class);
        Bundle bundle = new Bundle();
        bundle.putLong(Enums.Fields.AlarmId.toString(), idAlarm);
        intent.putExtras(bundle);

        startActivityForResult(intent, 0);
    }

    public FragmentActivity getContext() {
        return context;
    }

    public void setContext(FragmentActivity context) {
        this.context = context;
    }
}
