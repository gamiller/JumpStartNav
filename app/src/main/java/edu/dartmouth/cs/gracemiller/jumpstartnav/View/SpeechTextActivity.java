package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Random;

import edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers.AlarmPlayer;
import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Alarm;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.AlarmEntryDbHelper;
import edu.dartmouth.cs.gracemiller.jumpstartnav.R;

public class SpeechTextActivity extends Activity {

    public static Context mContext;
    SpeechRecognizer mRecognizer;
    ToggleButton recordButton;
    TextView textView;
    TextView readView;
    SpeechListener mListener;
    String readData = "read this please so we can compare it";
    boolean recording = false;
    boolean cleared = false;
    Button redoButton;
    String speechInput;
    private int mId;
    private AlarmPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_text);

        // pick random read text
        Random numRand = new Random();
        int num1 = numRand.nextInt(4 - 1 + 0) + 0;
        String[] readText = getResources().getStringArray(R.array.speech_text_array);
        readData = readText[num1];

        // get context and id
        mContext = this;
        Intent intent = getIntent();
        mId = intent.getIntExtra("id", 0);

        // start sound
        player = new AlarmPlayer(mContext, mId);
        player.startSound();

        // set speech input null
        speechInput = "";

        // get recognizer
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mListener = new SpeechListener();
        mRecognizer.setRecognitionListener(mListener);

        // get view objects
        recordButton = (ToggleButton) findViewById(R.id.RecordButton);
        redoButton = (Button) findViewById(R.id.redoButton);
        textView = (TextView) findViewById(R.id.speechInput);
        readView = (TextView) findViewById(R.id.readTextView);
        readView.setText(readData);

        // handle record button
        recordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // if recording
                if (isChecked) {
                    // start recording
                    cleared = false;
                    textView.setText("");
                    recordAudio();

                } else {
                    // set stop flag
                    recording = false;
                }
            }
        });

        // handle redo button
        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleared = true;
                speechInput = "";
                textView.setText(speechInput);

                if (recordButton.isChecked()) {
                    recordButton.setChecked(false);
                }
            }
        });
    }

    // start recognizer
    private void recordAudio() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        mRecognizer.startListening(intent);
        recording = true;
    }

    // stop recognizer
    private void stopRecording() {
        mRecognizer.stopListening();
        recording = false;
    }

    // compare the strings
    private void compareStrings() {

        // get value of compare
        int compareResult = speechInput.compareTo(readData);

        // if matches within a certain tolerance
        if ((compareResult > -5) && (compareResult < 5)) {

            // notify user and stop sound
            Toast.makeText(this, "matches!", Toast.LENGTH_SHORT).show();
            player.stopSound();

            // set alarm not active
            AlarmEntryDbHelper helper = new AlarmEntryDbHelper(getApplicationContext());
            Alarm alarm = helper.fetchAlarmByIndex((long) mId);
            alarm.setmActive(0);
            helper.updateAlarm(alarm);
            helper.close();

            // start reminder view
            Intent i = new Intent(mContext, AlarmReminderViewActivity.class);
            i.putExtra("id", (long) mId);
            startActivity(i);

        } else {
            Toast.makeText(this, "Doesn't match.  Try again!", Toast.LENGTH_SHORT).show();
        }

        speechInput = "";
    }

    // add to speech input
    private void setInput(String input) {
        if (speechInput.equals("")) {
            speechInput = input;
        } else {
            speechInput += (" " + input);
        }

        textView.setText(speechInput);
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

        // reset errors
        public void onReadyForSpeech(Bundle params) {
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
            this.done = true;
        }

        public void onError(int error) {
            textInput = "error";

            // if can't understand
            if (error == 7) {
                errorCount++;

                // if taking too long
            } else if (error == 6) {
                Toast.makeText(SpeechTextActivity.mContext, "Couldn't hear you"
                        , Toast.LENGTH_SHORT).show();
                if (recordButton.isChecked()) {
                    recordButton.setChecked(false);
                }
            }

            // if too many errors
            if (errorCount > 1) {
                Toast.makeText(SpeechTextActivity.mContext, "Couldn't understand you"
                        , Toast.LENGTH_SHORT).show();
                if (recordButton.isChecked()) {
                    recordButton.setChecked(false);
                }
            }
        }

        public void onResults(Bundle results) {
            // grab data
            data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            mInput = (String) data.get(0);
            setInput(mInput);

            dataAvailable = true;

            // if recording keep recording
            if (recording) {
                recordAudio();

                // else stop and compare
            } else {
                stopRecording();
                compareStrings();
            }
        }

        public void onPartialResults(Bundle partialResults) {
        }

        public void onEvent(int eventType, Bundle params) {
        }
    }
}