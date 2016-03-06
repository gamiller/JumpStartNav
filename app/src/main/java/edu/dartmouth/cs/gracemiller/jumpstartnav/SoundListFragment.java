package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import edu.dartmouth.cs.gracemiller.jumpstartnav.Classes.Recording;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.RecordingEntryDbHelper;


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
        Log.d("onCreateView()", "onCreateView()");

        //create new view
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//Make sure you have this line of code.






        // set the static variables when created
        mContext = getActivity();
        loaderManager = getActivity().getLoaderManager();
        loaderManager.initLoader(2, null, this).forceLoad();
        View mInflateView = inflater.inflate(R.layout.fragment_soundlist, container, false);
        mListView = (ListView) mInflateView.findViewById(R.id.recordingEntries);

        FloatingActionButton fab = (FloatingActionButton) mInflateView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RecordActivity.class);
                startActivity(intent);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                Recording recording = myRecordings.get(position);
                final long recordingId = recording.getId();
                helper = new RecordingEntryDbHelper(mContext);



                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_twobutton_recording, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);


                builder.setCancelable(true)
                        .setView(dialogView);
                builder.setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.cancel();
                            }
                        });

                Button playButton =  (Button) dialogView.findViewById(R.id.button_play_dialog);
                Button deleteButton =  (Button) dialogView.findViewById(R.id.button_delete_dialog);

                playButton.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           playRecoding(position);

                       }
                   });


                final AlertDialog alert = builder.create();

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("remove id is","remove id is" + recordingId );
                        Log.d("remove pos is","remove pos is" + position );
                        long removeID = recordingId;



                        helper.removeRecording(removeID);

                        FragmentTransaction tr = getFragmentManager().beginTransaction();
                        Fragment mAddSoundFrag = new SoundListFragment();
                        tr.replace(R.id.fragment_holder, mAddSoundFrag).commit();

                        alert.dismiss();
                    }
                });
                alert.show();




            }
        });

        return mInflateView;
    }

    @Override
    public void onResume() {
        Log.d("onResume()", "onResume()");

        super.onResume();

        //reloads the list when onResume is called
        loaderManager.initLoader(2, null, this).forceLoad();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d("onCreateLoader()", "onCreateLoader()");

        // returns an entry loader using context
        return new RecordingLoader(mContext);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recording>> loader, ArrayList<Recording> data) {
        Log.d("onLoadFinished()", "onLoadFinished()");


        //sets global variable
        myRecordings = data;

        if(!data.isEmpty()) {
            Log.d("onLoadFinished()", "not empty");

            //String[] recordingNames = new String[40];
            ArrayList<String> recordingNames = new ArrayList<String>();
            int i = 0;
            for (Recording recording : data) {
                Log.d("in recordings", "recording: " + recording.getAlarmName());
                //recordingNames.add(recording.getAlarmName());
                //recordingNames[i] = recording.getAlarmName();
                //i++;
                recordingNames.add(recording.getAlarmName());
                //Log.d("in recordings", "recording: " + recordingNames[i]);
                Log.d("in recordings", "recording: " + recordingNames.toArray());


            }

            //sets adapter to array list of exercises

            // Define a new adapter
            myAdapter = new ArrayAdapter<String>(mContext,
                    R.layout.listview_layout, recordingNames);
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
    public void onLoaderReset(Loader<ArrayList<Recording>> loader) {
        Log.d("onLoaderReset()", "onLoaderReset()");

        //reloads exercises into adapter
        myAdapter.clear();
        myAdapter.notifyDataSetChanged();


    }




    private void playRecoding(int position){
        //creates db helper
        helper = new RecordingEntryDbHelper(mContext);

        // gets rowId for exercise
        Recording myRecording = myRecordings.get(position);
        long exerciseId = myRecording.getId();

        //gets exercise entry from the rowID
        mRecording = helper.fetchRecordingByIndex(exerciseId);

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(mRecording.getFileName());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}