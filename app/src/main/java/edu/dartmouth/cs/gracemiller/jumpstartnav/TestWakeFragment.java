package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;


public class TestWakeFragment extends Fragment {
    String NUM_CORR = "numberCorrect";
    String NUM_LEFT = "numberLeft";
    String NUM_WRONG = "numberWrong";

    Button mMathButton;
    Button mMoveButton;
    Button mSpeechButton;
    Context mContext;
    public TestWakeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_test_wake, container, false);

        mMathButton = (Button) v.findViewById(R.id.button_wakeMath);
        mMoveButton = (Button) v.findViewById(R.id.button_wakeMove);
        mSpeechButton = (Button) v.findViewById(R.id.button_wakeSpeech);

        mMathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(mContext, MathAlarmActivity.class);
            intent.putExtra(NUM_CORR, 0);
            intent.putExtra(NUM_WRONG, 0);
            intent.putExtra(NUM_LEFT, 3);
            startActivity(intent);
            }
        });

        mMoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MovementActivity.class);
                startActivity(intent);
            }
        });

        mSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SpeechTextActivity.class);
                startActivity(intent);
            }
        });



        return v;
    }

}
