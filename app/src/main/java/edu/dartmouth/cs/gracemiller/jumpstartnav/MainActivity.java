package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


//        // instantiate grid view
//        Fragment fragment = null;
//        fragment = new GridViewFragment();
//
//        // create grid view fragment
//        if (fragment != null) {
//            FragmentManager fragmentManager = getFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.fragment_holder, fragment).commit();
//        } else {
//            // error in creating fragment
//            Log.d("MainActivity", "Error in creating fragment");
//        }
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    // handles menu selection
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        // if StressMeter chosen
        if (id == R.id.nav_alarms) {
            Fragment mAlarmListFragment = null;
            mAlarmListFragment = new AlarmFragment();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, mAlarmListFragment).commit();
        // else if results is chosen
        } else if (id == R.id.nav_reminder) {
//            Fragment mReminderFragment = null;
//            mReminderFragment = new ReminderFragment();
//
//            FragmentManager fragmentManager = getFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.fragment_holder, mReminderFragment).commit();


        } else if (id == R.id.nav_addsound){
            // create results fragment
            Fragment mAddSoundFrag = null;
            mAddSoundFrag = new SoundListFragment();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, mAddSoundFrag).commit();

        } else if (id == R.id.nav_testWake){
            // create results fragment
            Fragment testFrag = null;
            testFrag = new TestWakeFragment();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, testFrag).commit();

        } else if (id == R.id.nav_alarmTest){
            // create results fragment
            Fragment alarmTestFrag = null;
            alarmTestFrag = new AlarmTestFragment();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, alarmTestFrag).commit();

        }else if (id == R.id.nav_dreams){
            // create results fragment
            Fragment dreamFrag = null;
            dreamFrag = new DreamFragment();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, dreamFrag).commit();

        }

        // close navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    

//    //when the service is running a notification appears to tell if the tracking is running
//    private void setUpNotification() {
//        int notifyId = 1;
//
//
//        //set up intent filter
//        IntentFilter mFilter = new IntentFilter();
//        mFilter.addAction("TrackingServiceAction");
//        registerReceiver(tsReceiver, mFilter);
//
//        //create pending intent
//        Intent mapIntent = new Intent(this,MapDisplayActivity.class);
//        PendingIntent returnIntent = PendingIntent.getActivity(this, 0, mapIntent, 0);
//
//        //create the notificaiton
//        Notification notification = new Notification.Builder(this)
//                .setOngoing(true) //cant swipe the notification away
//                .setContentTitle("My Runs 4").setContentText("Recording your path now")
//                .setSmallIcon(R.drawable.default_profile).setContentIntent(returnIntent).build();
//
//
//        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        // set flags
//        notification.flags = notification.flags
//                | Notification.FLAG_ONGOING_EVENT;
//
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        //notify the user that the app is running in the background
//        mNotificationManager.notify(notifyId, notification);
//
//    }


}
