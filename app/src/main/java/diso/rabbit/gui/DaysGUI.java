package diso.rabbit.gui;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import diso.rabbit.R;
import diso.rabbit.adapters.DaysAdapter;
import diso.rabbit.helper.Enums;
import diso.rabbit.helper.Utils;
import diso.rabbit.model.DayModel;

/**
 * Created by pabdiava on 16/04/2015.
 */
public class DaysGUI extends FragmentActivity {
    ViewPager Tab;
    DaysAdapter daysAdapter;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.days);

        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.schedule);

        SetContentTabs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {
            case R.id.addCourse:
                intent = new Intent(DaysGUI.this, ScheduleAddEditGUI.class);
                Bundle bundle = new Bundle();
                bundle.putLong(Enums.Fields.DayId.toString(), (actionBar.getSelectedNavigationIndex() + 1));
                intent.putExtras(bundle);

                startActivityForResult(intent, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void SetContentTabs() {
        daysAdapter = new DaysAdapter(getSupportFragmentManager(), this);
        actionBar = getActionBar();

        Tab = (ViewPager) findViewById(R.id.pager);
        Tab.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });
        Tab.setAdapter(daysAdapter);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabReselected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {
            }

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                Tab.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {
            }
        };

        for (DayModel day : daysAdapter.getDays()) {
            actionBar.addTab(actionBar.newTab().setText(Utils.GetDayName(this, day.getName())).setTabListener(tabListener));
        }
    }
}