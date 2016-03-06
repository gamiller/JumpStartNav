package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.AlarmManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Calendar;

import edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers.AlarmPlayer;
import edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers.AlarmScheduler;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Classes.Alarm;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.AlarmEntryDbHelper;


public class AlarmTestFragment extends Fragment {


    public AlarmTestFragment() {
        // Required empty public constructor
    }

    AlarmPlayer player;

    Button addbutton;
    Button deleteButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_alarm_test, container, false);
//        player = new AlarmPlayer(getContext(),);

        addbutton = (Button) v.findViewById(R.id.addbutton);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlarm2Schedule();
            }
        });

        deleteButton = (Button) v.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlarm();
            }
        });

        // Inflate the layout for this fragment
        return v;

    }

    private void deleteAlarm() {
        AlarmScheduler.deleteAlarm(getActivity().getApplicationContext(), 15);
//        player.stopSound();
    }

    private void addAlarm2Schedule() {
        Alarm alarm = new Alarm();
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis());
//        time.set(Calendar.YEAR,2016);
//        time.set(Calendar.MONTH,Calendar.MARCH);
//        time.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
//        time.set(Calendar.HOUR, 21);
        time.set(Calendar.MINUTE, 8);
        time.set(Calendar.SECOND, 00);
//        time.set(Calendar.AM_PM, 1);
        Log.d("date", "date is " + time.toString());
        alarm.setmDateTime(time);

        AlarmEntryDbHelper helper = new AlarmEntryDbHelper(getActivity().getApplicationContext());
        int id = (int) helper.insertAlarm(alarm);

        AlarmScheduler.setAlarm(getActivity().getApplicationContext(), id, time);

//        player.startSound(getActivity().getApplicationContext(),"default", RingtoneManager.TYPE_ALARM);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
