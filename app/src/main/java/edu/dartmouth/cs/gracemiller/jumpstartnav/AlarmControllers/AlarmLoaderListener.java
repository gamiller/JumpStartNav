package edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmControllers;

import android.content.Loader;
import android.os.Bundle;

import java.util.ArrayList;

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Recording;

/**
 * Created by gracemiller on 3/4/16.
 */
public class AlarmLoaderListener implements android.app.LoaderManager.LoaderCallbacks<ArrayList<Recording>> {

    @Override
    public Loader<ArrayList<Recording>> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recording>> loader, ArrayList<Recording> data) {

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recording>> loader) {

    }
}
