package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Recording;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.RecordingEntryDbHelper;
import edu.dartmouth.cs.gracemiller.jumpstartnav.R;

/*
Activity to record an entry for the sound database
 */
public class RecordActivity extends AppCompatActivity {
    public static Context mContext;
    private ToggleButton mRecordButton;
    private Button mSaveButton;
    private Button mPlayButton;
    private TextView mFeedbackTextView;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String mFilePath;
    private String mAlarmName;
    private boolean mAudioSet, checked;
    private int mAlarmId;
    private View view;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // initialize variables
        mContext = this;
        mAudioSet = false;
        checked = false;
        mRecorder = null;
        mPlayer = null;

        // get id for the recording to be saved under
        Random random = new Random();
        int operation = random.nextInt(10000000 - 1 + 1) + 1;

        mAlarmId = operation; // temporary

        // get file path
        mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/recording" + mAlarmId + ".3gp";

        // Get view objects
        mFeedbackTextView = (TextView) findViewById(R.id.notificationID);
        mRecordButton = (ToggleButton) findViewById(R.id.toggleButton);
        mSaveButton = (Button) findViewById(R.id.saveButton);
        mPlayButton = (Button) findViewById(R.id.playButton);

        // handle record button
        mRecordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checked = true;

                    // if audio was set reset file path
                    if (mAudioSet) {
                        resetFile();
                    }

                    // start recording
                    startRecording();

                } else {

                    // stop recording
                    stopRecording();
                    checked = false;
                }
            }
        });


        // handle play button
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if audio exists play
                if (mAudioSet) {
                    playAudio();
                } else {
                    // else notify user
                    Toast.makeText(mContext, "You need to make an alarm first!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // handle save button
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if audio not null
                if (mAudioSet) {

                    // if not recording
                    if (!checked) {
                        // set recording name
                        DialogFragment fileFragment = StringDialog.newInstance("Alarm Name:");
                        fileFragment.show(getFragmentManager(), "Set Alarm Name");
                    }
                } else {
                    // else notify user
                    Toast.makeText(mContext, "You need to make an alarm first!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    // delete file
    private void resetFile() {
        File file = new File(mFilePath);

        if (file.exists()) {
            file.delete();
        }
    }

    // record audio
    private void startRecording() {

        // set up recorder
        mRecorder = new MediaRecorder();
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        // atempt start
        try {
            mRecorder.prepare();
            mRecorder.start();
            mFeedbackTextView.setText("Recording...");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // stop recording
    private void stopRecording() {
        try {
            mRecorder.stop();
            mRecorder.release();
            mAudioSet = true;
            mRecorder = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        mFeedbackTextView.setText("Recording done. Hit record to redo recording.");
    }

    // play back the media recording
    private void playAudio() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFilePath);
            mPlayer.prepare();
            mPlayer.start();
            mFeedbackTextView.setText("Playing...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // set name of alarm
    public void setAlarmName(String alarm) {
        this.mAlarmName = alarm;
    }

    // save and finish
    private void finishActivity() {
        // create new recording
        Recording recording = new Recording();
        recording.setFileName(mFilePath);
        recording.setAlarmName(mAlarmName);

        // save ew recording
        RecordingEntryDbHelper helper = new RecordingEntryDbHelper(mContext);
        helper.insertRecording(recording);
        helper.close();

        // finish
        Toast.makeText(mContext, "Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    // retreives the users comment input from a text alert dialog box
    public static class StringDialog extends DialogFragment {

        public static StringDialog newInstance(String title) {
            StringDialog newFragment = new StringDialog();
            Bundle args = new Bundle();
            args.putString("title", title);
            newFragment.setArguments(args);
            return newFragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String title = getArguments().getString("title");

            //Sets up the edit text
            final EditText comment_text = new EditText(mContext);
            comment_text.setInputType(InputType.TYPE_CLASS_TEXT);
            comment_text.setHint("What do you want to call your alarm?");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setView(comment_text);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int whichButton) {
                    // calls set comment
                    String alarmName = comment_text.getText().toString();

                    // sets alarm name and finishes
                    ((RecordActivity) getActivity()).setAlarmName(alarmName);
                    ((RecordActivity) getActivity()).finishActivity();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int whichButton) {
                    dialog.cancel();
                }
            });

            return builder.create();
        }
    }
}