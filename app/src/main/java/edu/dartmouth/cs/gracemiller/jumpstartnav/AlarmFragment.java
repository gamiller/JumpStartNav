package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;


public class AlarmFragment extends Fragment {
    private Context mContext;
    private Boolean mOpened = false;
    private ArrayList<String> alarmSettingsArray = new ArrayList<>();

    private Calendar mDateAndTime = Calendar.getInstance();
    private long mDateAndTimeMillis;

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
        alarmSettingsArray.add("Date");
        alarmSettingsArray.add("Wakeup Activity");
        alarmSettingsArray.add("Ringtone");
        alarmSettingsArray.add("Reminder");
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

        final CardView alarmView = (CardView) view.findViewById(R.id.cardview);
        final LinearLayout settingsView = (LinearLayout) view.findViewById(R.id.expandedView);
        alarmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mOpened) {
                    alarmView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    settingsView.setVisibility(View.VISIBLE);
                    slide_down(mContext, settingsView);
                    mOpened = true;
                } else if (mOpened) {
                    alarmView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    slide_up(mContext, settingsView);
                    settingsView.setVisibility(View.GONE);
                    mOpened = false;
                    Toast.makeText(mContext, "Changes Not Saved!", Toast.LENGTH_LONG).show();
                }
            }
        });

        final ListView settingsList = (ListView) view.findViewById(R.id.alarmOptions);
        settingsList.setAdapter(new ArrayAdapter<String>(mContext, R.layout.alarm_settings_list_text,
                R.id.settings_list_white_text, alarmSettingsArray));
        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        onDateClicked();
                        View v = settingsList.getChildAt(position);
                        TextView textView = (TextView) v.findViewById(R.id.settings_list_white_text);
                        String date = android.text.format.DateFormat.format("MMM dd yyyy", mDateAndTime).toString();
                        textView.setText(date);
                        break;
                    case 1:
                        //wakeup
                    case 2:
                        //ringtone
                    case 3:
                        onReminderClicked();
                }
            }
        });

        Button saveButton = (Button) view.findViewById(R.id.cardSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                if(mOpened) {
                    slide_up(mContext, settingsView);
                }
                mOpened = false;
                if(!mOpened) {
                    settingsView.setVisibility(View.GONE);
                    Toast.makeText(mContext, "Saved!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button deleteButton = (Button) view.findViewById(R.id.cardDeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void onActivityClicked() {
        // Create dialog with radio buttons to select wakeup activity
        AlertDialog.Builder mDurationDialog = new AlertDialog.Builder(mContext);
        mDurationDialog.setTitle(R.string.duration_dialog_title);
        final EditText inputText = new EditText(this);
        inputText.setRawInputType(Configuration.KEYBOARD_12KEY);    // Input in numerical keyboard
        mDurationDialog.setView(inputText);
        mDurationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        mDurationDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                duration = Double.parseDouble(inputText.getText().toString());
                duration *= 60.0;
                dialog.dismiss();
            }
        });
        mDurationDialog.show();
    }

    private void onDateClicked() {
        // Create dialog to show current date
        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mDateAndTime.set(Calendar.YEAR, year);
                mDateAndTime.set(Calendar.MONTH, monthOfYear);
                mDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        };

        new DatePickerDialog(mContext, mDateListener,
                mDateAndTime.get(Calendar.YEAR),
                mDateAndTime.get(Calendar.MONTH),
                mDateAndTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void onReminderClicked() {
        // Create dialog with EditText widget to input reminder
        AlertDialog.Builder mReminderDialog = new AlertDialog.Builder(mContext);
        mReminderDialog.setTitle(R.string.reminder_dialog_title);
        final EditText inputText = new EditText(mContext);
        inputText.setHint(R.string.reminder_dialog_hint);
        mReminderDialog.setView(inputText);
        mReminderDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        mReminderDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                //comment = inputText.getText().toString();
                dialog.dismiss();
            }
        });
        mReminderDialog.show();
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
