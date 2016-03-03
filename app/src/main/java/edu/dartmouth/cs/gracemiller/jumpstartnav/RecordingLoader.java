package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import edu.dartmouth.cs.gracemiller.jumpstartnav.Classes.Recording;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.RecordingEntryDbHelper;

/**
 * Created by gracemiller on 1/31/16.
 * create an entryloader which extends asynctaskloader in order to load
 * the exercises from the database
 */
public class RecordingLoader extends AsyncTaskLoader<ArrayList<Recording>>{
    public RecordingLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Recording> loadInBackground() {
        Log.d("loader", "in loader");
            //get a database helper
            RecordingEntryDbHelper helper = new RecordingEntryDbHelper(getContext());
            //fetch all of the exercise entries
            ArrayList<Recording> recordings = helper.fetchRecordings();
        Log.d("loader", "end of loader");

        return recordings;
    }

}
