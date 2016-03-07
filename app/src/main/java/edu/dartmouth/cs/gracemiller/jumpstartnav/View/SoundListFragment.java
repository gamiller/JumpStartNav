package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Recording;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.RecordingEntryDbHelper;
import edu.dartmouth.cs.gracemiller.jumpstartnav.R;
import edu.dartmouth.cs.gracemiller.jumpstartnav.RecordingControllers.RecordingLoader;

/*
fragment to show the recordings that have been created, also allows the user to open dialogue to
play recordings or delete recordings
 */
public class SoundListFragment extends Fragment
        implements android.app.LoaderManager.LoaderCallbacks<ArrayList<Recording>> {

    // have static variables for maintaining context when switching
    // tabs and orientation
    public static ArrayAdapter<String> myAdapter;
    public static ListView mListView;
    public static android.app.LoaderManager loaderManager;
    public static Context mContext;
    RecordingEntryDbHelper helper;
    Recording mRecording;
    ArrayList<Recording> myRecordings;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the static variables when created
        mContext = getActivity();
        loaderManager = getActivity().getLoaderManager();
        loaderManager.initLoader(2, null, this).forceLoad();

        //inflate the list of recordings
        View mInflateView = inflater.inflate(R.layout.fragment_soundlist, container, false);
        mListView = (ListView) mInflateView.findViewById(R.id.recordingEntries);

        //floating action button to add a new recording, opens the recording activity
        FloatingActionButton fab = (FloatingActionButton) mInflateView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RecordActivity.class);
                startActivity(intent);
            }
        });

        //if user clicks a recording open dialogue with play and delete buttons
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                Recording recording = myRecordings.get(position);
                final long recordingId = recording.getId();
                helper = new RecordingEntryDbHelper(mContext);

                //dialogue with play and delete buttons
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

                Button playButton = (Button) dialogView.findViewById(R.id.button_play_dialog);
                Button deleteButton = (Button) dialogView.findViewById(R.id.button_delete_dialog);

                //play the given recording
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playRecoding(position);

                    }
                });

                final AlertDialog alert = builder.create();

                //delete the given recording from the database
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        long removeID = recordingId;

                        helper.removeRecording(removeID);

                        //update the list of recordings
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
        super.onResume();

        //reloads the list when onResume is called
        loaderManager.initLoader(2, null, this).forceLoad();
    }


    //loader for the recordings from the recording database
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // returns an entry loader using context
        return new RecordingLoader(mContext);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recording>> loader, ArrayList<Recording> data) {
        //sets global variable
        myRecordings = data;

        if (!data.isEmpty()) {
            ArrayList<String> recordingNames = new ArrayList<String>();

            for (Recording recording : data) {
                recordingNames.add(recording.getAlarmName());
            }

            //sets adapter to array list of sounds
            myAdapter = new ArrayAdapter<String>(mContext,
                    R.layout.listview_layout, recordingNames);

            mListView.setAdapter(myAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recording>> loader) {
        //reloads exercises into adapter
        myAdapter.clear();
        myAdapter.notifyDataSetChanged();
    }

    private void playRecoding(int position) {
        //creates db helper
        helper = new RecordingEntryDbHelper(mContext);

        // gets rowId for recording
        Recording myRecording = myRecordings.get(position);
        long exerciseId = myRecording.getId();

        //gets recording entry from the rowID
        mRecording = helper.fetchRecordingByIndex(exerciseId);

        //play the sound associated with recording entry
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