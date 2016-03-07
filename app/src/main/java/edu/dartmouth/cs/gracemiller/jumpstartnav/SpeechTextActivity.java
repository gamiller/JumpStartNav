package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers.AlarmPlayer;

public class SpeechTextActivity extends Activity {

    private int mId;
    private AlarmPlayer player;

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
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_speech_text);

        Intent intent = getIntent();
        mId = intent.getIntExtra("id",0);

        player = new AlarmPlayer(this,mId);
        player.startSound();

        mContext = this;

        speechInput = "";

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mListener = new SpeechListener();
        mRecognizer.setRecognitionListener(mListener);

        recordButton = (ToggleButton) findViewById(R.id.RecordButton);
        redoButton = (Button) findViewById(R.id.redoButton);
        textView = (TextView) findViewById(R.id.speechInput);
        readView = (TextView) findViewById(R.id.readTextView);
        Log.d("onCreate()", "read data is " + readData);
        readView.setText(readData);

        recordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cleared = false;
                    Log.d("Speechin", "Speech input is" + speechInput);
                    textView.setText("");
                    recordAudio();
                } else {
                    recording = false;
//                    stopRecording();
//                    if (!cleared) {
//                        Log.d("comparing","comparing");
//                        compareStrings();
//                    }
                }
            }
        });

        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("redoing","redone");
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
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,this.getPackageName());

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

        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "Listening");
            ready = true;
            errorCount = 0;
        }

        public void onBeginningOfSpeech() {}
        public void onRmsChanged(float rmsdB) {}
        public void onBufferReceived(byte[] buffer) {}

        public void onEndOfSpeech()
        {
            Log.d(TAG, "Done");
            this.done = true;
        }
        public void onError(int error)
        {
            Log.d(TAG, "error " + error);
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
                Toast.makeText(SpeechTextActivity.mContext,"Couldn't understand you"
                        ,Toast.LENGTH_SHORT).show();
                if (recordButton.isChecked()) {
                    recordButton.setChecked(false);
                }
            }
        }
        public void onResults(Bundle results)
        {
            data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            Log.d(TAG, "onResults " + data);


            mInput = (String) data.get(0);

            Log.d("data","this is the data: "+ mInput);
            setInput(mInput);

            dataAvailable = true;

            if (recording) {
                recordAudio();
            } else {
                stopRecording();
                compareStrings();
            }
        }
        public void onPartialResults(Bundle partialResults) {}
        public void onEvent(int eventType, Bundle params) {}

    }

    private void compareStrings() {
        int compareResult;

        compareResult = speechInput.compareTo(readData);
        Log.d("speechINput","speech input is: " + speechInput);
        Log.d("compare result", "The compared result is: " + compareResult);

        if ((compareResult > -5) && (compareResult < 5)) {
            Log.d("TAGG", "matches!!");
            // end notification here
            Toast.makeText(this, "matches!", Toast.LENGTH_SHORT).show();
            player.stopSound();
            finish();
        } else {
            Log.d("TAGG", "doesn't match!! WAKE UP");
            Toast.makeText(this, "Doesn't match.  Try again!", Toast.LENGTH_SHORT).show();
            // resetText
            // ???
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

}