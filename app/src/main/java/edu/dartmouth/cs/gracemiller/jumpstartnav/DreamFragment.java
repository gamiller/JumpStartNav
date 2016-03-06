package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import edu.dartmouth.cs.gracemiller.jumpstartnav.Classes.Dream;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.DreamDbHelper;


public class DreamFragment extends android.app.Fragment {
    // have static variables for maintaining context when switching
    // tabs and orientation
    public static ArrayAdapter<String> myAdapter;
    public static ListView mListView;
    DreamDbHelper helper;
    Dream mDream;
    ArrayList<Dream> myDreams;
    public static android.app.LoaderManager loaderManager;
    public static Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //create new view
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//Make sure you have this line of code.

        // set the static variables when created
        mContext = getActivity();
        loaderManager = getActivity().getLoaderManager();
        loaderManager.initLoader(3, null, dreamLoaderListener).forceLoad();
        View mInflateView = inflater.inflate(R.layout.fragment_dream, container, false);
        mListView = (ListView) mInflateView.findViewById(R.id.dreamEntries);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                Dream dream = myDreams.get(position);
                final long dreamID = dream.getId();

//                Intent intent = new Intent(mContext,DisplayDreamActivity);
//                intent.putExtra("id",dreamID);
//                startActivity(intent);
            }
        });


        // Inflate the layout for this fragment
        return mInflateView;
    }

    @Override
    public void onResume() {
        Log.d("onResume()", "onResume()");

        super.onResume();

        //reloads the list when onResume is called
        loaderManager.initLoader(3, null, dreamLoaderListener).forceLoad();
    }

    private LoaderManager.LoaderCallbacks<ArrayList<Dream>> dreamLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<Dream>>() {
        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            Log.d("onCreateLoader()", "onCreateLoader()");

            // returns an entry loader using context
            return new DreamLoader(mContext);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Dream>> loader, ArrayList<Dream> data) {
            Log.d("onLoadFinished()", "onLoadFinished()");


            //sets global variable
            myDreams = data;

            if (data.size() != 0) {
                Log.d("onLoadFinished()", "not empty");
                Log.d("onLoadFinished()", "data size = " + data.size());


                //String[] recordingNames = new String[40];
                ArrayList<String> dreamNames = new ArrayList<String>();
                for (Dream dream : myDreams) {

                    Calendar cal = dream.getDate();
                    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
                    String finalString = dream.getDreamName() + " " + formatDate.format(cal.getTime());

                    dreamNames.add(finalString);

                }

                //sets adapter to array list of exercises

                // Define a new adapter

                // check this
                myAdapter = new ArrayAdapter<String>(mContext,
                        R.layout.listview_layout, dreamNames);
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
        public void onLoaderReset(Loader<ArrayList<Dream>> loader) {
            Log.d("onLoaderReset()", "onLoaderReset()");

            //reloads exercises into adapter
            myAdapter.clear();
            myAdapter.notifyDataSetChanged();
        }
    };
}
