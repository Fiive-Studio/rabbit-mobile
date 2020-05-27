package diso.rabbit.adapters.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import diso.rabbit.R;
import diso.rabbit.data.DayDao;
import diso.rabbit.data.ScheduleDao;
import diso.rabbit.helper.Utils;
import diso.rabbit.model.NextScheduleModel;
import diso.rabbit.model.ScheduleModel;

/**
 * Created by pabdiava on 25/02/2016.
 */
public class CourseFragment extends Fragment {

    ScheduleDao schedulesDao;
    DayDao dayDao;
    ArrayAdapter<ScheduleModel> adapterCourses;
    boolean isResume;
    View view;
    FragmentActivity context;

    public void setContext(FragmentActivity context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.courses_frag, container, false);
        schedulesDao = Utils.GetDaoSession(view.getContext()).getScheduleDao();
        dayDao = Utils.GetDaoSession(view.getContext()).getDayDao();
        ShowCourses();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isResume) {
            ShowCourses();
            isResume = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isResume = true;
    }

    private void ShowCourses() {
        NextScheduleModel result = Utils.GetNextSchedule(schedulesDao, dayDao);

        adapterCourses = new ArrayAdapter<ScheduleModel>(view.getContext(), android.R.layout.simple_list_item_1, result.getSchedule());
        final ListView lstCourses = (ListView) view.findViewById(R.id.lstCourses);
        lstCourses.setAdapter(adapterCourses);

        if(result.getSchedule().size() == 0){
            context.getActionBar().getTabAt(0).setText(R.string.home_tab_1_1);
        }
        else{
            context.getActionBar().getTabAt(0).setText(String.format("%s (%s)", getString(R.string.home_tab_1), Utils.GetDayName(context, result.getDay().getName())));
        }
    }
}