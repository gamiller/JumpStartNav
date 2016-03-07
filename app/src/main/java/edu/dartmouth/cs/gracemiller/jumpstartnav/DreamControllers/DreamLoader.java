package edu.dartmouth.cs.gracemiller.jumpstartnav.DreamControllers;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Dream;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.DreamDbHelper;

/**
 * Created by gracemiller on 1/31/16.
 * create an entryloader which extends asynctaskloader in order to load
 * the exercises from the database
 */
public class DreamLoader extends AsyncTaskLoader<ArrayList<Dream>> {
    Context mContext;

    public DreamLoader(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public ArrayList<Dream> loadInBackground() {
        Log.d("loader", "in loader");
        //get a database helper
        DreamDbHelper helper = new DreamDbHelper(mContext);
        //fetch all of the exercise entries
        Log.d("database for dreams", ""+ helper.getDatabaseName());
        ArrayList<Dream> dreams = helper.fetchDreams();
        Log.d("loader", "end of loader");

        helper.close();
        return dreams;
    }

}