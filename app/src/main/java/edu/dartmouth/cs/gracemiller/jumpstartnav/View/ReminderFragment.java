package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

import android.app.Fragment;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
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
    public static android.app.LoaderManager loaderManager;
    public static Context mContext;
    ArrayList<Alarm> myAlarms;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
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
        super.onResume();

        //reloads the list when onResume is called
        loaderManager.initLoader(5, null, this).forceLoad();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // returns an entry loader using context
        return new AlarmLoader(mContext);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Alarm>> loader, ArrayList<Alarm> data) {
        //sets global variable
        myAlarms = data;

        if (!data.isEmpty()) {
            ArrayList<String> alarmReminders = new ArrayList<String>();

            for (Alarm alarm : myAlarms) {
                if ((alarm.getmActive()) == 1 && !alarm.getmReminder().isEmpty()) {
                    Calendar time = alarm.getmDateTime();
                    String date = android.text.format.DateFormat.format("MMM dd yyyy", time).toString();
                    String reminderString = (date + ": " + alarm.getmReminder());
                    alarmReminders.add(reminderString);
                }
            }

            //sets adapter to array list of reminders
            myAdapter = new ArrayAdapter<String>(mContext,
                    R.layout.listview_layout, alarmReminders);

            mListView.setAdapter(myAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Alarm>> loader) {
        myAdapter.clear();
        myAdapter.notifyDataSetChanged();
    }
}
