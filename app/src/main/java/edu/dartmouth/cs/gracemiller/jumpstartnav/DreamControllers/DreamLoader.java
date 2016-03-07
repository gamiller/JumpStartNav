package edu.dartmouth.cs.gracemiller.jumpstartnav.DreamControllers;

import android.content.AsyncTaskLoader;
import android.content.Context;

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
        //get a database helper
        DreamDbHelper helper = new DreamDbHelper(mContext);

        //fetch all of the exercise entries
        ArrayList<Dream> dreams = helper.fetchDreams();

        helper.close();

        return dreams;
    }
}