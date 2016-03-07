package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Dream;
import edu.dartmouth.cs.gracemiller.jumpstartnav.DreamControllers.DreamLoader;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.DreamDbHelper;
import edu.dartmouth.cs.gracemiller.jumpstartnav.R;


public class DreamFragment extends android.app.Fragment {
    // have static variables for maintaining context when switching
    // tabs and orientation
    public static ArrayAdapter<String> myAdapter;
    public static ListView mListView;
    public static android.app.LoaderManager loaderManager;
    public static Context mContext;
    DreamDbHelper helper;
    Dream mDream;
    ArrayList<Dream> myDreams;

    private LoaderManager.LoaderCallbacks<ArrayList<Dream>> dreamLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<Dream>>() {
        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            // returns an entry loader using context
            return new DreamLoader(mContext);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Dream>> loader, ArrayList<Dream> data) {
            //sets global variable
            myDreams = data;

            if (data.size() != 0) {
                //String[] recordingNames = new String[40];
                ArrayList<String> dreamNames = new ArrayList<String>();
                for (Dream dream : data) {
                    Calendar cal = dream.getDate();
                    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
                    String finalString = formatDate.format(cal.getTime()) + " | " + dream.getDreamName();


                    Log.d("adding a dream","adding a dream");
                    dreamNames.add(finalString);
                }

                //sets adapter to array list of dreams
                myAdapter = new ArrayAdapter<String>(mContext,
                        R.layout.listview_layout, dreamNames);


                // Assign the adapter to ListView
                mListView.setAdapter(myAdapter);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Dream>> loader) {
            //reloads exercises into adapter

            if (myAdapter != null) {
                myAdapter.clear();
                myAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //create new view
        super.onCreate(savedInstanceState);

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

                Intent intent = new Intent(mContext,DisplayDreamActivity.class);
                Log.d("the id is", " the dream id is " + dreamID);
                intent.putExtra("id", (int) dreamID);
                startActivityForResult(intent,1);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) mInflateView.findViewById(R.id.fabDream);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DreamActivity.class);
                startActivity(intent);
            }
        });


        // Inflate the layout for this fragment
        return mInflateView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //reloads the list when onResume is called
        loaderManager.initLoader(3, null, dreamLoaderListener).forceLoad();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            onResume();
        }
    }


}
