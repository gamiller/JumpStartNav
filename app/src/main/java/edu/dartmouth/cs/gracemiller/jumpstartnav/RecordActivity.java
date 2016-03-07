package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import edu.dartmouth.cs.gracemiller.jumpstartnav.Classes.Recording;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.RecordingEntryDbHelper;

public class RecordActivity extends AppCompatActivity {
    private ToggleButton mRecordButton;
    private Button mSaveButton;
    private Button mPlayButton;
    private TextView mFeedbackTextView;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    public static Context mContext;
    private String mFilePath;
    private String mAlarmName;
    private boolean mAudioSet, checked;
    private int mAlarmId;
    private View view;


    /* QUESTIONS
        HOW do I get the filename?? how will I handle that???

        How do I handle overwriting a file????

     */

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

        // get id
//        Intent startIntent = getIntent();
//        mAlarmId = startIntent.getIntExtra("alarm_id",-1);
        Random random = new Random();
        int operation = random.nextInt(10000000 - 1 + 1) + 1;

        mAlarmId = operation; // temporary

        mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/recording" + mAlarmId + ".3gp";

        // Get view objects
        mFeedbackTextView = (TextView) findViewById(R.id.notificationID);
        mRecordButton = (ToggleButton) findViewById(R.id.toggleButton);
        mSaveButton = (Button) findViewById(R.id.saveButton);
        mPlayButton = (Button) findViewById(R.id.playButton);

        mRecordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checked = true;
                    if (mAudioSet) {
                        resetFile();
                    }

                    startRecording();
                } else {
                    stopRecording();
                    checked = false;
                }
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioSet) {
                    playAudio();
                } else {
                    Toast.makeText(mContext, "You need to make an alarm first!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAudioSet) {
                    if (!checked) {
                        DialogFragment fileFragment = StringDialog.newInstance("Alarm Name:");
                        fileFragment.show(getFragmentManager(), "Set Alarm Name");
                    }
                } else {
                    Toast.makeText(mContext,"You need to make an alarm first!",Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    private void resetFile() {
        File file = new File(mFilePath);

        if (file.exists()) {
            file.delete();
        }

    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
            mFeedbackTextView.setText("Recording...");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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


    public void setAlarmName(String alarm) {
        this.mAlarmName = alarm;
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

    private void finishActivity() {
        Recording recording = new Recording();
        recording.setFileName(mFilePath);
        recording.setAlarmName(mAlarmName);

        RecordingEntryDbHelper helper = new RecordingEntryDbHelper(mContext);
        helper.insertRecording(recording);
        helper.close();

        Toast.makeText(mContext,"Saved!",Toast.LENGTH_SHORT)
                .show();


        finish();
    }
}