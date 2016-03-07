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

        Random numRand = new Random();
        int num1 = numRand.nextInt(4 - 1 + 0) + 0;

        String[] readText = getResources().getStringArray(R.array.speech_text_array);

        readData = readText[num1];


        mContext = this;

        Intent intent = getIntent();
        mId = intent.getIntExtra("id", 0);

        player = new AlarmPlayer(this, mId);
        player.startSound();

        speechInput = "";

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mListener = new SpeechListener();
        mRecognizer.setRecognitionListener(mListener);

        recordButton = (ToggleButton) findViewById(R.id.RecordButton);
        redoButton = (Button) findViewById(R.id.redoButton);
        textView = (TextView) findViewById(R.id.speechInput);
        readView = (TextView) findViewById(R.id.readTextView);
        readView.setText(readData);

        recordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cleared = false;
                    textView.setText("");
                    recordAudio();

                } else {
                    recording = false;
                }
            }
        });

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

    private void recordAudio() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        mRecognizer.startListening(intent);
        recording = true;
    }

    private void stopRecording() {
        mRecognizer.stopListening();
        recording = false;
    }

    private void compareStrings() {
        int compareResult = speechInput.compareTo(readData);

        if ((compareResult > -5) && (compareResult < 5)) {
            Toast.makeText(this, "matches!", Toast.LENGTH_SHORT).show();

            player.stopSound();

            AlarmEntryDbHelper helper = new AlarmEntryDbHelper(getApplicationContext());
            Alarm alarm = helper.fetchAlarmByIndex((long) mId);
            alarm.setmActive(0);
            helper.updateAlarm(alarm);
            helper.close();

            Intent i = new Intent(mContext, AlarmReminderViewActivity.class);
            i.putExtra("id", (long) mId);
            startActivity(i);

        } else {
            Toast.makeText(this, "Doesn't match.  Try again!", Toast.LENGTH_SHORT).show();
        }

        speechInput = "";
    }

    private void setInput(String input) {

        if (speechInput.equals("")) {
            speechInput = input;
        } else {
            speechInput += (" " + input);
        }

        textView.setText(speechInput);
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

            if (error == 7) {
                errorCount++;

            } else if (error == 6) {
                Toast.makeText(SpeechTextActivity.mContext, "Couldn't hear you"
                        , Toast.LENGTH_SHORT).show();
                if (recordButton.isChecked()) {
                    recordButton.setChecked(false);
                }
            }

            if (errorCount > 1) {
                Toast.makeText(SpeechTextActivity.mContext, "Couldn't understand you"
                        , Toast.LENGTH_SHORT).show();
                if (recordButton.isChecked()) {
                    recordButton.setChecked(false);
                }
            }
        }

        public void onResults(Bundle results) {
            data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            mInput = (String) data.get(0);
            setInput(mInput);

            dataAvailable = true;

            if (recording) {
                recordAudio();
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