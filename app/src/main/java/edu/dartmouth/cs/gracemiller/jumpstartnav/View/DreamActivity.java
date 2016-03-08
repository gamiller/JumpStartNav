package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Dream;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.DreamDbHelper;
import edu.dartmouth.cs.gracemiller.jumpstartnav.R;

public class DreamActivity extends AppCompatActivity {
    public static Context mContext;
    SpeechRecognizer mRecognizer;
    ToggleButton recordButton;
    TextView dreamView;
    SpeechListener mListener;
    boolean recording = false;
    boolean cleared = false;
    boolean checked = false;
    Button redoButton;
    String speechInput;
    Button saveButton;
    String mDreamName;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (recording) {
            stopRecording();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream);

        // instantiate variables
        mContext = this;
        speechInput = "";

        // get recognizer
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mListener = new SpeechListener();
        mRecognizer.setRecognitionListener(mListener);

        // get view objects
        recordButton = (ToggleButton) findViewById(R.id.DreamRecord);
        redoButton = (Button) findViewById(R.id.dreamRedo);
        dreamView = (TextView) findViewById(R.id.dreamInput);
        saveButton = (Button) findViewById(R.id.saveButtonDream);


        // handle record button
        recordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // not empty and note that it is checked
                    cleared = false;
                    recordAudio();
                    checked = true;
                } else {
                    // note not checked and not recording
                    recording = false;
                    checked = false;
                }
            }
        });

        // handle redo button
        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set cleared
                cleared = true;
                speechInput = "";
                dreamView.setText(speechInput);

                // undo check
                if (recordButton.isChecked()) {
                    recordButton.setChecked(false);
                }
            }
        });


        // handle save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if speech is not null
                if (!speechInput.equals("")) {
                    // and if not checked
                    if (!checked) {
                        // set dream name
                        DialogFragment fileFragment = StringDialog.newInstance("Dream Name:");
                        fileFragment.show(getFragmentManager(), "Set Dream Name");
                    }
                } else {
                    // tell user to set dream
                    Toast.makeText(mContext, "You need to tell me about your dream!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    // intent to record audio
    private void recordAudio() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 500);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        // start listening
        mRecognizer.startListening(intent);
        recording = true;
    }

    // stop listening
    private void stopRecording() {
        mRecognizer.stopListening();
        recording = false;
    }

    // sets the dream input
    private void setInput(String input) {
        if (speechInput.equals("")) {
            speechInput = input;
        } else {
            speechInput += (" " + input);
        }
        dreamView.setText(speechInput);
    }

    // set the dream name
    public void setDreamName(String dream) {
        this.mDreamName = dream;
    }

    // finishes the dream activity
    private void finishActivity() {
        // new dream variable
        Dream dream = new Dream();
        dream.setDreamName(mDreamName);
        dream.setDream(speechInput);

        // save new dream
        DreamDbHelper helper = new DreamDbHelper(getApplicationContext());
        int id = (int) helper.insertDream(dream);
        helper.close();

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
            comment_text.setHint("What do you want to call your dream?");

            // set title and view
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setView(comment_text);

            // if yes
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int whichButton) {
                    // calls set dream name and finish
                    String dreamName = comment_text.getText().toString();
                    ((DreamActivity) getActivity()).setDreamName(dreamName);
                    ((DreamActivity) getActivity()).finishActivity();
                }
            });

            // cancel dialog
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int whichButton) {
                    dialog.cancel();
                }
            });

            return builder.create();
        }
    }

    // handle speech input
    public class SpeechListener implements RecognitionListener {

        String TAG = "TAGG";
        String textInput;
        boolean ready = false;
        boolean dataAvailable = false;
        boolean done = false;
        String mInput = "";
        ArrayList data;
        int errorCount;

        public SpeechListener() {
            this.textInput = "";
            errorCount = 0;
        }

        public void onReadyForSpeech(Bundle params) {
            ready = true;
            errorCount = 0;
        }

        public void onBeginningOfSpeech() {}

        public void onRmsChanged(float rmsdB) {}

        public void onBufferReceived(byte[] buffer) {}

        public void onEndOfSpeech() {
            Log.d(TAG, "Done");
            this.done = true;
        }

        public void onError(int error) {
            Log.d(TAG, "error " + error);
            textInput = "error";

            // if not understood
            if (error == 7) {
                errorCount++;
                // if not heard
            } else if (error == 6) {
                Toast.makeText(DreamActivity.mContext, "Speak up!"
                        , Toast.LENGTH_SHORT).show();
                // set unchecked
                if (recordButton.isChecked()) {
                    recordButton.setChecked(false);
                }
            }

            // if too many errors
            if (errorCount > 1 && error != 6) {
                Toast.makeText(DreamActivity.mContext, "What?"
                        , Toast.LENGTH_SHORT).show();

                // set unchecked
                if (recordButton.isChecked()) {
                    recordButton.setChecked(false);
                }
            }
        }

        // on get result
        public void onResults(Bundle results) {

            // grab data
            data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            mInput = (String) data.get(0);

            // set dream input
            setInput(mInput);

            dataAvailable = true;

            // if recording keep recording else stop
            if (recording) {
                recordAudio();
            } else {
                stopRecording();
            }
        }

        public void onPartialResults(Bundle partialResults) {}

        public void onEvent(int eventType, Bundle params) {}
    }
}