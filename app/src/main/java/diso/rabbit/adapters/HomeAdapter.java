package diso.rabbit.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import diso.rabbit.adapters.fragments.CourseFragment;
import diso.rabbit.adapters.fragments.TaskFragment;

/**
 * Created by pabdiava on 25/02/2016.
 */
public class HomeAdapter extends FragmentStatePagerAdapter {
    FragmentActivity context;

    public HomeAdapter(FragmentManager fm, FragmentActivity context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                CourseFragment c = new CourseFragment();
                c.setContext(context);
                return c;
            case 1:
                TaskFragment t = new TaskFragment();
                t.setContext(context);
                return t;
        }
        return null;

    }

    @Override
    public int getCount() {
        return 2; //No of Tabs
    }
}