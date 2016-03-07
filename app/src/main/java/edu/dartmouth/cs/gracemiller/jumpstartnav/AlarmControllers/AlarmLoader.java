package edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmControllers;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Alarm;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.AlarmEntryDbHelper;

/**
 * Created by gracemiller on 1/31/16.
 * create an entryloader which extends asynctaskloader in order to load
 * the exercises from the database
 */
public class AlarmLoader extends AsyncTaskLoader<ArrayList<Alarm>>{
    public AlarmLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Alarm> loadInBackground() {
        Log.d("loader", "in loader");
            //get a database helper
            AlarmEntryDbHelper helper = new AlarmEntryDbHelper(getContext());
            //fetch all of the exercise entries
            ArrayList<Alarm> recordings = helper.fetchAlarms();
        Log.d("loader", "end of loader");
        helper.close();

        return recordings;
    }

}
