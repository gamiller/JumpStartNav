package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import edu.dartmouth.cs.gracemiller.jumpstartnav.View.AlarmFragment;
import edu.dartmouth.cs.gracemiller.jumpstartnav.View.DreamFragment;
import edu.dartmouth.cs.gracemiller.jumpstartnav.View.GettingStartedFragment;
import edu.dartmouth.cs.gracemiller.jumpstartnav.View.ReminderFragment;
import edu.dartmouth.cs.gracemiller.jumpstartnav.View.SoundListFragment;

/*
Main view of the app, creates the navigation view of the app and loads each fragment
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment mAlarmListFragment = null;
        mAlarmListFragment = new AlarmFragment();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_holder, mAlarmListFragment).commit();

        //create drawer layout to open and close navigation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // handles menu selection
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        //opens the alarms fragment
        if (id == R.id.nav_alarms) {
            Fragment mAlarmListFragment = null;
            mAlarmListFragment = new AlarmFragment();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, mAlarmListFragment).commit();

        } else if (id == R.id.nav_reminder) {
            //opens the reminders fragment
            Fragment mReminderFragment = null;
            mReminderFragment = new ReminderFragment();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, mReminderFragment).commit();

        } else if (id == R.id.nav_addsound) {
            //opens the recordings fragment
            Fragment mAddSoundFrag = null;
            mAddSoundFrag = new SoundListFragment();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, mAddSoundFrag).commit();

        } else if (id == R.id.nav_dreams) {
            //opens the dreams fragment
            Fragment dreamFrag = null;
            dreamFrag = new DreamFragment();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, dreamFrag).commit();

        } else if (id == R.id.nav_instructions) {
            //opens the instructions fragment
            Fragment instructfrag = null;
            instructfrag = new GettingStartedFragment();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, instructfrag).commit();

        }

        // close navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
