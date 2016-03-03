package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import edu.dartmouth.cs.gracemiller.jumpstartnav.Classes.Recording;


public class SoundListFragment extends Fragment
        implements android.app.LoaderManager.LoaderCallbacks<ArrayList<Recording>> {

    // have static variables for maintaining context when switching
    // tabs and orientation
    public static ArrayAdapter<String> myAdapter;
    public static ListView mListView;
    RecordingEntryDbHelper helper;
    Recording mRecording;
    ArrayList<Recording> myRecordings;
    public static android.app.LoaderManager loaderManager;
    public static Context mContext;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        //create new view
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//Make sure you have this line of code.


        // set the static variables when created
        mContext = getActivity();
        loaderManager = getActivity().getLoaderManager();
        loaderManager.initLoader(1, null, this).forceLoad();
        View mInflateView = inflater.inflate(R.layout.fragment_soundlist, container, false);
        mListView = (ListView) mInflateView.findViewById(R.id.recordingEntries);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //creates db helper
                helper = new RecordingEntryDbHelper(mContext);

                // gets rowId for exercise
                Recording myRecording = myRecordings.get(position);
                long exerciseId = myRecording.getId();

                //gets exercise entry from the rowID
                mRecording = helper.fetchRecordingByIndex(exerciseId);

//                if (mRecording.getInputType() == 0) {
//
//
//                    //creates an intent and adds all data from exercise
//                    Intent startDisplayActivity = new Intent(mContext, DisplayEntryActivity.class);
//                    startDisplayActivity.putExtra("id", mRecording.getId());
//                    startDisplayActivity.putExtra("input_type", mRecording.getInputType());
//                    startDisplayActivity.putExtra("activity_type", mRecording.getActivityType());
//                    startDisplayActivity.putExtra("date_time", mRecording.getDateTime());
//                    startDisplayActivity.putExtra("duration", mRecording.getDuration());
//                    startDisplayActivity.putExtra("distance", mRecording.getDistance());
//                    startDisplayActivity.putExtra("calories", mRecording.getCalories());
//                    startDisplayActivity.putExtra("heart_rate", mRecording.getHeartRate());
//
//                    //starts the display activity
//                    startActivity(startDisplayActivity);
//                } else {
//
//                    // create the display map intent for gps and manual inputs
//                    Intent mapHistory = new Intent(mContext, MapsActivityHistory.class);
//                    mapHistory.putExtra("id", mRecording.getId());
//                    mapHistory.putExtra("avg_speed", mRecording.getAvgSpeed());
//                    mapHistory.putExtra("activity_type", mRecording.getActivityType());
//                    mapHistory.putExtra("duration", mRecording.getDuration());
//                    mapHistory.putExtra("distance", mRecording.getDistance());
//                    mapHistory.putExtra("climb", mRecording.getClimb());
//                    mapHistory.putExtra("calories", mRecording.getCalories());
//                    //                    Log.d("history", "in history fragment latlongs are: " + mRecording.getLocationList());
//                    mapHistory.putParcelableArrayListExtra("latlng", mRecording.getLocationList());
//                    startActivity(mapHistory);
//                }
            }
        });

        return mInflateView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //reloads the list when onResume is called
        loaderManager.initLoader(1, null, this).forceLoad();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // returns an entry loader using context
        return new RecordingLoader(mContext);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recording>> loader, ArrayList<Recording> data) {

        //sets global variable
        myRecordings = data;

        if(!data.isEmpty()) {
            String[] recordingNames = new String[40];
            int i = 0;
            for (Recording recording : data) {
                //recordingNames.add(recording.getAlarmName());
                recordingNames[i] = recording.getAlarmName();
                i++;
            }

            //sets adapter to array list of exercises

            // Define a new adapter
            myAdapter = new ArrayAdapter<String>(mContext,
                    R.layout.fragment_soundlist, recordingNames);

            // Assign the adapter to ListView
            //setListAdapter(mAdapter);
            //myAdapter = new ExerciseLineArrayAdapter(mContext, data);
            mListView.setAdapter(myAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recording>> loader) {
        //reloads exercises into adapter
         myAdapter.clear();
//        myAdapter.swapCursor(null);
        //myAdapter.notifyDataSetChanged();
        //myAdapter.addAll();
        //myAdapter.setExercises(new ArrayList<Recording>());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflate menu for delete button
        inflater.inflate(R.menu.menudisplay, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //delete entry
        if (item.getItemId() == R.id.add) {
            Intent intent = new Intent(mContext, RecordActivity.class);
            startActivity(intent);
        }

        return true;
    }


}