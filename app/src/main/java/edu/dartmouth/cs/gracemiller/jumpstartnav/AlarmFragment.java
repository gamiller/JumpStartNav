package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;


public class AlarmFragment extends Fragment {
    private Context mContext;
    private Boolean mOpened = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificationFragment.
     */
    public static AlarmFragment newInstance() {
        AlarmFragment fragment = new AlarmFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public AlarmFragment() {
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
        return inflater.inflate(R.layout.fragment_alarm, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout cardList = (LinearLayout) view.findViewById(R.id.cardContainer);

        final CardView alarmView = (CardView) view.findViewById(R.id.cardview1);
        final LinearLayout settingsView = (LinearLayout) view.findViewById(R.id.expandedView);
        alarmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                settingsView.setVisibility(View.VISIBLE);
                slide_down(mContext, settingsView);
                mOpened = true;
            }
        });

        Button saveButton = (Button) view.findViewById(R.id.cardSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                slide_up(mContext, settingsView);
                settingsView.setVisibility(View.GONE);
                mOpened = false;
            }
        });
    }

    public static void slide_down(Context context, View view) {
        Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        if(a != null){
            a.reset();
            if(view != null){
                view.clearAnimation();
                view.startAnimation(a);
            }
        }
    }

    public static void slide_up(Context context, View view) {
        Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        if(a != null){
            a.reset();
            if(view != null){
                view.clearAnimation();
                view.startAnimation(a);
            }
        }
    }
}
