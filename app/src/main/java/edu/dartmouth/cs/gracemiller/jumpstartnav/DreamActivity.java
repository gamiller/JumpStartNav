package edu.dartmouth.cs.gracemiller.jumpstartnav;

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

import edu.dartmouth.cs.gracemiller.jumpstartnav.Classes.Dream;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Classes.Recording;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.DreamDbHelper;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.RecordingEntryDbHelper;

public class DreamActivity extends AppCompatActivity {
    SpeechRecognizer mRecognizer;
    ToggleButton recordButton;
    TextView dreamView;
    SpeechListener mListener;
    boolean recording = false;
    boolean cleared = false;
    boolean checked = false;
    Button redoButton;
    String speechInput;
    public static Context mContext;
    Button saveButton;
    String mDreamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream);

        mContext = this;
        speechInput = "";

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mListener = new SpeechListener();
        mRecognizer.setRecognitionListener(mListener);

        recordButton = (ToggleButton) findViewById(R.id.DreamRecord);
        redoButton = (Button) findViewById(R.id.dreamRedo);
        dreamView = (TextView) findViewById(R.id.dreamInput);
        saveButton = (Button) findViewById(R.id.saveButtonDream);

        recordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cleared = false;
                    dreamView.setText("");
                    recordAudio();
                    checked = true;
                } else {
                    recording = false;
                    checked = false;
                }
            }
        });

        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("redoing", "redone");
                cleared = true;
                speechInput = "";
                dreamView.setText(speechInput);
                if (recordButton.isChecked()) {
                    recordButton.setChecked(false);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speechInput.equals("")) {
                    if (!checked) {
                        DialogFragment fileFragment = StringDialog.newInstance("Dream Name:");
                        fileFragment.show(getFragmentManager(), "Set Dream Name");
                    }
                } else {
                    Toast.makeText(mContext,"You need to tell me about your dream!",Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });


    }

    private void recordAudio() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,500);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        mRecognizer.startListening(intent);

        recording = true;
    }

    private void stopRecording() {
        mRecognizer.stopListening();
        recording = false;
    }


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
            Log.d(TAG, "Listening");
            ready = true;
            errorCount = 0;
        }

        public void onBeginningOfSpeech() {
        }

        public void onRmsChanged(float rmsdB) {
        }

        public void onBufferReceived(byte[] buffer) {
        }

        public void onEndOfSpeech() {
            Log.d(TAG, "Done");
            this.done = true;
        }

        public void onError(int error) {
            Log.d(TAG, "error " + error);
            textInput = "error";
            if (error == 7) {
                errorCount++;
            } else if (error == 6) {
                Toast.makeText(DreamActivity.mContext, "Speak up!"
                        , Toast.LENGTH_SHORT).show();
                if (recordButton.isChecked()) {
                    recordButton.setChecked(false);
                }
//                recordAudio();
            }

            if (errorCount > 1 && error != 6) {
                Toast.makeText(DreamActivity.mContext, "What?"
                        , Toast.LENGTH_SHORT).show();
                if (recordButton.isChecked()) {
                    recordButton.setChecked(false);
                }
//                recordAudio();
            }
//            // MAY MAKE IT CRASH???
//            if (error == 6) {
//                recordAudio();
//            }

            // recordAudio();
        }

        public void onResults(Bundle results) {
            data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            Log.d(TAG, "onResults " + data);


            mInput = (String) data.get(0);

            Log.d("data", "this is the data: " + mInput);
            setInput(mInput);

            dataAvailable = true;

            if (recording) {
                recordAudio();
            } else {
                stopRecording();
//                compareStrings();
            }
        }

        public void onPartialResults(Bundle partialResults) {
        }

        public void onEvent(int eventType, Bundle params) {
        }

    }

    private void setInput(String input) {

        if (speechInput.equals("")) {
            speechInput = input;
        } else {
            speechInput += (" " + input);
        }
        dreamView.setText(speechInput);
    }

    public void setDreamName(String dream) {
        this.mDreamName = dream;
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

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setView(comment_text);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int whichButton) {

                    // calls set comment
                    String dreamName = comment_text.getText().toString();
                    ((DreamActivity) getActivity()).setDreamName(dreamName);
                    ((DreamActivity) getActivity()).finishActivity();
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
        Dream dream = new Dream();
        dream.setDreamName(mDreamName);
        dream.setDream(speechInput);

        DreamDbHelper helper = new DreamDbHelper(mContext);
        int id = (int) helper.insertDream(dream);
        helper.close();

        Toast.makeText(mContext,"Saved!",Toast.LENGTH_SHORT)
                .show();


        finish();
    }


}