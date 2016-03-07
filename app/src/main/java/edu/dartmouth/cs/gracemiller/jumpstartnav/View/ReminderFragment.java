package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

import android.app.Fragment;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

import edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmControllers.AlarmLoader;
import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Alarm;
import edu.dartmouth.cs.gracemiller.jumpstartnav.R;


public class ReminderFragment extends Fragment implements android.app.LoaderManager.LoaderCallbacks<ArrayList<Alarm>> {

    // have static variables for maintaining context when switching
    // tabs and orientation
    public static ArrayAdapter<String> myAdapter;
    public static ListView mListView;

    ArrayList<Alarm> myAlarms;
    public static android.app.LoaderManager loaderManager;
    public static Context mContext;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        Log.d("onCreateView()", "onCreateView()");

        //create new view
        super.onCreate(savedInstanceState);

        // set the static variables when created
        mContext = getActivity();

        loaderManager = getActivity().getLoaderManager();
        loaderManager.initLoader(5, null, this).forceLoad();

        View mInflateView = inflater.inflate(R.layout.fragment_reminder, container, false);
        mListView = (ListView) mInflateView.findViewById(R.id.reminderEntries);

        return mInflateView;
    }

    @Override
    public void onResume() {
        Log.d("onResume()", "onResume()");

        super.onResume();

        //reloads the list when onResume is called
        loaderManager.initLoader(5, null, this).forceLoad();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d("onCreateLoader()", "onCreateLoader()");

        // returns an entry loader using context
        return new AlarmLoader(mContext);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Alarm>> loader, ArrayList<Alarm> data) {
        Log.d("onLoadFinished()", "onLoadFinished()");


        //sets global variable
        myAlarms = data;

        if(!data.isEmpty()) {
            Log.d("onLoadFinished()", "not empty");

            //String[] recordingNames = new String[40];
            ArrayList<String> alarmReminders = new ArrayList<String>();
            for (Alarm alarm : myAlarms) {
                //Log.d("in recordings", "recording: " + recording.getAlarmName());
                //recordingNames.add(recording.getAlarmName());
                //recordingNames[i] = recording.getAlarmName();
                //i++;
                if((alarm.getmActive()) == 1 && !alarm.getmReminder().isEmpty()) {
                    Calendar time = alarm.getmDateTime();
                    String date = android.text.format.DateFormat.format("MMM dd yyyy", time).toString();
                    String reminderString = (date + ": " + alarm.getmReminder());
                    alarmReminders.add(reminderString);
                }
                //Log.d("in recordings", "recording: " + recordingNames[i]);
                //Log.d("in recordings", "recording: " + recordingNames.toArray());
            }

            //sets adapter to array list of exercises

            // Define a new adapter
            myAdapter = new ArrayAdapter<String>(mContext,
                    R.layout.listview_layout, alarmReminders);
            Log.d("onLoadFinished()", "got adapter");


            // Assign the adapter to ListView
            //setListAdapter(mAdapter);
            //myAdapter = new ExerciseLineArrayAdapter(mContext, data);
            //mListView.setListAdapter(myAdapter);
            mListView.setAdapter(myAdapter);
            Log.d("onLoadFinished()", "set adapter");
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Alarm>> loader) {
        Log.d("onLoaderReset()", "onLoaderReset()");

        //reloads exercises into adapter
        myAdapter.clear();
        myAdapter.notifyDataSetChanged();
    }
}
