package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers.AlarmPlayer;
import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Alarm;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.AlarmEntryDbHelper;
import edu.dartmouth.cs.gracemiller.jumpstartnav.R;

public class MathAlarmActivity extends AppCompatActivity {

    int mCurrAnswer = 0;
    String NUM_CORR = "numberCorrect";
    String NUM_LEFT = "numberLeft";
    String NUM_WRONG = "numberWrong";
    private int id;
    private AlarmPlayer player;
    private EditText mAnswerText;
    private Button mSubmitButton;
    private TextView mEquationText, mCorrectText, mWrongText, mNumLeftText;
    private int mSolution = 0;
    private int mNumCorrect, mNumWrong, mNumLeft, mNumToSolve;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate()", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_alarm);

        Intent i = getIntent();
        id = i.getIntExtra("id", 0);

        AlarmEntryDbHelper helper = new AlarmEntryDbHelper(this);
        Alarm onAlarm = helper.fetchAlarmByIndex((long) id);
        String dataSource = onAlarm.getmRingToneFile();
        int index = onAlarm.getDefaultIndex();

        //start playing the sound
        player = new AlarmPlayer(this, id);
//        player.startSound(context,dataSource,index);
//        player.startSound();

        mNumCorrect = i.getIntExtra(NUM_CORR, 0);
        mNumWrong = i.getIntExtra(NUM_WRONG, 0);
        mNumLeft = i.getIntExtra(NUM_LEFT, 0);

        mAnswerText = (EditText) findViewById(R.id.answerBox);
        mEquationText = (TextView) findViewById(R.id.mathproblem);
        mCorrectText = (TextView) findViewById(R.id.correctAnswers);
        mWrongText = (TextView) findViewById(R.id.wrongAnswers);
        mNumLeftText = (TextView) findViewById(R.id.remainingProbs);

        mCorrectText.setText("Correct Answers: " + mNumCorrect);
        mWrongText.setText("Wrong Answers: " + mNumWrong);
        mNumLeftText.setText("Problems Remaining: " + mNumLeft);


        char[] operations = {'+', '-', '*'};
        //find random number between 1-3 to decide if add, subtract, mult
        Random random = new Random();
        int operation = random.nextInt(2 - 0 + 1) + 0;
        //if 1 or 2 then chose two numbers between 1-1000
        int num1 = 0;
        int num2 = 0;
        if (operation != 2) {
            Random numRand = new Random();
            num1 = numRand.nextInt(1000 - 1 + 1) + 1;
            num2 = numRand.nextInt(1000 - 1 + 1) + 1;
            if (num2 > num1) {
                int temp = num1;
                num1 = num2;
                num2 = temp;
            }

        } else {
            //else chose two numbers between 1 and 20 for multiplication
            Random numRand = new Random();
            num1 = numRand.nextInt(20 - 1 + 1) + 1;
            num2 = numRand.nextInt(20 - 1 + 1) + 1;
        }

        //chose operation depending on the random
        // 0 is addition, 1 is subtraction, 2 is multiplication
        if (operation == 0) {
            mSolution = num1 + num2;
        } else if (operation == 1) {
            mSolution = num1 - num2;
        } else {
            mSolution = num1 * num2;
        }

        String equation = (Integer.toString(num1) + operations[operation] + Integer.toString(num2));
        mEquationText.setText(equation);

        //listener for submit button
        mSubmitButton = (Button) findViewById(R.id.submit_math_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {

            //when it is clicked, exit the app
            @Override
            public void onClick(View v) {
                mCurrAnswer = Integer.valueOf(mAnswerText.getText().toString());

                boolean check = false;

                if (!mAnswerText.getText().toString().equals("")) {
                    mCurrAnswer = Integer.valueOf(mAnswerText.getText().toString());
                    //check if the answer is correct
                    check = checkAnswer(mCurrAnswer);
                } else {
                    //if string empty, automatically false
                    check = false;
                }
                //if correct answer
                if (check) {
                    Toast.makeText(mContext, "CORRECT", Toast.LENGTH_SHORT).show();
                    mNumLeft--;
                    mNumCorrect++;

                    //if the user answered all three questions
                    if (mNumLeft <= 0) {
                        //stop sound, open up the reminder display for the alarm
                        player.stopSound();
                        Intent i = new Intent(mContext, AlarmReminderViewActivity.class);
                        i.putExtra("id", (long) id);
                        startActivity(i);

                    } else {
                        //call the activity again, restart the alarm sound
                        player.stopSound();
                        Intent intent = new Intent(mContext, MathAlarmActivity.class);
                        intent.putExtra(NUM_CORR, mNumCorrect);
                        intent.putExtra(NUM_WRONG, mNumWrong);
                        intent.putExtra(NUM_LEFT, mNumLeft);
                        intent.putExtra("id", id);

                        AlarmEntryDbHelper helper = new AlarmEntryDbHelper(getApplicationContext());
                        Alarm alarm = helper.fetchAlarmByIndex((long) id);
                        alarm.setmActive(0);
                        helper.updateAlarm(alarm);
                        helper.close();

                        intent.putExtra("id", id);
                        finish();

                        startActivity(intent);
                    }

                } else {
                    //wrong answer, call the activity again
                    //update the textview of correct, incorrect
                    Toast.makeText(mContext, "WRONG", Toast.LENGTH_SHORT).show();

                    mNumWrong++;
                    mWrongText.setText("Wrong Answers: " + mNumWrong);

                    player.stopSound();
                    Intent intent = new Intent(mContext, MathAlarmActivity.class);
                    intent.putExtra(NUM_CORR, mNumCorrect);
                    intent.putExtra(NUM_WRONG, mNumWrong);
                    intent.putExtra(NUM_LEFT, mNumLeft);
                    intent.putExtra("id", id);
                    finish();

                    startActivity(intent);
                }
            }
        });

    }

    //check that the given answer is the same as the calculated answer
    private boolean checkAnswer(int answer) {
        if (answer == mSolution) {
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt("NUM_LEFT", mNumLeft);
        savedInstanceState.putInt("NUM_WRONG", mNumWrong);
        savedInstanceState.putInt("NUM_CORR", mNumCorrect);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
