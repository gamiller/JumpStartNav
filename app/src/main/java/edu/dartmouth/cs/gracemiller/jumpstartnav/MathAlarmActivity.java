package edu.dartmouth.cs.gracemiller.jumpstartnav;

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

public class MathAlarmActivity extends AppCompatActivity {

    private EditText mAnswerText;
    private Button mSubmitButton;
    private TextView mEquationText, mCorrectText, mWrongText, mNumLeftText;
    private int mSolution = 0;
    private int mNumCorrect, mNumWrong, mNumLeft, mNumToSolve;
    private Context mContext = this;
    int mCurrAnswer = 0;
    String NUM_CORR = "numberCorrect";
    String NUM_LEFT = "numberLeft";
    String NUM_WRONG = "numberWrong";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate()", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_alarm);

//        // Check whether we're recreating a previously destroyed instance
//        if (savedInstanceState != null) {
//            // Restore value of members from saved state
//            mNumCorrect = savedInstanceState.getInt("NUM_CORR");
//            mNumLeft = savedInstanceState.getInt("NUM_LEFT");
//            mNumWrong = savedInstanceState.getInt("NUM_WRONG");
//        } else {
//            // Probably initialize members with default values for a new instance
//
//            mNumCorrect = 0;
//            mNumLeft = 5;
//            mNumWrong = 0;
//        }
        Intent i = getIntent();
//        if(i != null) {
            mNumCorrect = i.getIntExtra(NUM_CORR, 0);
            mNumWrong = i.getIntExtra(NUM_WRONG, 0);
            mNumLeft = i.getIntExtra(NUM_LEFT, 0);
        Log.d("onCreate()", "NUM_CORR" + mNumCorrect);
        Log.d("onCreate()", "NUM_WRONG" + mNumWrong);
        Log.d("onCreate()", "NUM_Left" + mNumLeft);


//        }else{
//            mNumCorrect = 0;
//            mNumLeft = 5;
//            mNumWrong = 0;
//        }

        mAnswerText = (EditText) findViewById(R.id.answerBox);
        mEquationText = (TextView) findViewById(R.id.mathproblem);
        mCorrectText = (TextView) findViewById(R.id.correctAnswers);
        mWrongText = (TextView) findViewById(R.id.wrongAnswers);
        mNumLeftText = (TextView) findViewById(R.id.remainingProbs);

        mCorrectText.setText("Correct Answers: " + mNumCorrect);
        mWrongText.setText("Wrong Answers: " + mNumWrong);
        mNumLeftText.setText("Problems Remaining: " + mNumLeft);



        char[] operations = {'+', '-' , '*'};
        //find random number between 1-3 to decide if add, subtract, mult
        Random random = new Random();
        int operation = random.nextInt(2 - 0 + 1) + 0;
        //if 1 or 2 then chose two numbers between 1-1000
        int num1 = 0;
        int num2 = 0;
        if(operation != 2) {
            Random numRand = new Random();
            num1 = numRand.nextInt(1000 - 1 + 1) + 1;
            num2 = numRand.nextInt(1000 - 1 + 1) + 1;
            if(num2 > num1){
                int temp = num1;
                num1 = num2;
                num2 = temp;
            }

        }else{
            //else chose two numbers between 1 and 20
            Random numRand = new Random();
            num1 = numRand.nextInt(20 - 1 + 1) + 1;
            num2 = numRand.nextInt(20 - 1 + 1) + 1;
        }



        //int mSolution = 0;
        if(operation == 0){
            mSolution = num1 + num2;
        }else if(operation ==1){
            mSolution = num1 - num2;
        }else{
            mSolution = num1 * num2;
        }

        String equation = (Integer.toString(num1) + operations[operation] + Integer.toString(num2));
        mEquationText.setText(equation);

        //listener for cancel button
        mSubmitButton = (Button) findViewById(R.id.submit_math_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {

            //when it is clicked, exit the app
            @Override
            public void onClick(View v) {
                mCurrAnswer = Integer.valueOf(mAnswerText.getText().toString());
                Log.d("current answer", "current answer is: " + mCurrAnswer);
                Log.d("correct answer is", "correct answer is" + mSolution);
                if (checkAnswer(mCurrAnswer)) {
                    Toast.makeText(mContext, "CORRECT", Toast.LENGTH_SHORT).show();
                    Log.d("checkAnswer", "answer is correct");
                    mNumLeft--;
                    mNumCorrect++;
                    Log.d("num corr and to solve", "num correct " + mNumCorrect + "num to solve " + mNumToSolve);
                    if (mNumLeft <= 0) {
                        Log.d("numtosolve0", "last q");
                        Intent intent = new Intent(mContext, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(mContext, MathAlarmActivity.class);
                        intent.putExtra(NUM_CORR, mNumCorrect);
                        intent.putExtra(NUM_WRONG, mNumWrong);
                        intent.putExtra(NUM_LEFT, mNumLeft);
                        finish();

                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(mContext, "WRONG", Toast.LENGTH_SHORT).show();
                    Log.d("checkAnswer", "wrong answer");
                    mNumWrong++;
                    Log.d("num corr and to solve", "num correct " + mNumCorrect + "num to solve " + mNumToSolve);
                    //recreate();
                    mWrongText.setText("Wrong Answers: " + mNumWrong);
                    Intent intent = new Intent(mContext, MathAlarmActivity.class);
                    intent.putExtra(NUM_CORR, mNumCorrect);
                    intent.putExtra(NUM_WRONG, mNumWrong);
                    intent.putExtra(NUM_LEFT, mNumLeft);
                    finish();

                    startActivity(intent);

                }
                //finish();

            }
        });




    }


    private boolean checkAnswer(int answer){
        if(answer == mSolution){
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d("onSaveInstanceState()", "onSaveInstanceState()");
        // Save the user's current game state
        savedInstanceState.putInt("NUM_LEFT", mNumLeft);
        savedInstanceState.putInt("NUM_WRONG", mNumWrong);
        savedInstanceState.putInt("NUM_CORR", mNumCorrect);


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}