package diso.rabbit.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.ArrayList;

import diso.rabbit.adapters.fragments.ScheduleFragment;
import diso.rabbit.data.DayDao;
import diso.rabbit.helper.Utils;
import diso.rabbit.model.DayModel;

/**
 * Created by pabdiava on 6/03/2016.
 */
public class DaysAdapter extends FragmentStatePagerAdapter {
    DayDao daysDao;
    ArrayList<DayModel> days;
    FragmentActivity context;

    public DaysAdapter(FragmentManager fm, FragmentActivity context) {
        super(fm);
        this.context = context;
        daysDao = Utils.GetDaoSession(context).getDayDao();
        days = Utils.GetDays(daysDao);
    }

    @Override
    public Fragment getItem(int i) {
        ScheduleFragment scheduleFragment = new ScheduleFragment();
        scheduleFragment.setIdDay((i + 1));
        scheduleFragment.setContext(context);

        return scheduleFragment;
    }

    @Override
    public int getCount() {
        return days.size(); //No of Tabs
    }

    public ArrayList<DayModel> getDays() {
        return days;
    }
}