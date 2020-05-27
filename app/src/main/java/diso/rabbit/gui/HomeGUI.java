package diso.rabbit.gui;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Calendar;

import diso.rabbit.R;
import diso.rabbit.adapters.HomeAdapter;
import diso.rabbit.service.AlarmReceiver;

public class HomeGUI extends FragmentActivity {
    ViewPager Tab;
    HomeAdapter homeAdapter;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SetContentTabs();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        int currentTab = actionBar.getSelectedNavigationIndex();
        menu.clear();

        if (currentTab == 0) {
            inflater.inflate(R.menu.menu_home, menu);
        } else {
            inflater.inflate(R.menu.menu_home_task, menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {
            case R.id.courses:
                intent = new Intent(HomeGUI.this, CoursesGUI.class);
                startActivity(intent);
                return true;
            case R.id.days:
                intent = new Intent(HomeGUI.this, DaysGUI.class);
                startActivity(intent);
                return true;
            case R.id.tasks:
                intent = new Intent(HomeGUI.this, TasksGUI.class);
                startActivity(intent);
                return true;
            case R.id.settings:
                intent = new Intent(HomeGUI.this, SettingsGUI.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void SetContentTabs(){
        homeAdapter = new HomeAdapter(getSupportFragmentManager(), this);
        actionBar = getActionBar();

        Tab = (ViewPager)findViewById(R.id.pager);
        Tab.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });
        Tab.setAdapter(homeAdapter);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener(){
            @Override
            public void onTabReselected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {}

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                Tab.setCurrentItem(tab.getPosition());
                invalidateOptionsMenu();
            }

            @Override
            public void onTabUnselected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {}
        };

        actionBar.addTab(actionBar.newTab().setText(R.string.home_tab_1).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText(R.string.home_tab_2).setTabListener(tabListener));

        getActionBar().getSelectedTab().setText("");
    }
}
