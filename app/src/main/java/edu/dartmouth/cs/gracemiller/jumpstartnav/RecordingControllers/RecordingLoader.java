package edu.dartmouth.cs.gracemiller.jumpstartnav.RecordingControllers;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Recording;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.RecordingEntryDbHelper;

/**
 * Created by gracemiller on 1/31/16.
 * create an recordingloader which extends asynctaskloader in order to load
 * the recordings from the database
 */
public class RecordingLoader extends AsyncTaskLoader<ArrayList<Recording>> {

    public RecordingLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Recording> loadInBackground() {

        //get a database helper
        RecordingEntryDbHelper helper = new RecordingEntryDbHelper(getContext());

        //fetch all of the recording entries
        ArrayList<Recording> recordings = helper.fetchRecordings();

        helper.close();

        return recordings;
    }
}
