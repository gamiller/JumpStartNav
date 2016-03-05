package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers.AlarmScheduler;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Classes.Alarm;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Classes.Recording;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.AlarmEntryDbHelper;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.RecordingEntryDbHelper;


public class AlarmFragment extends Fragment implements android.app.LoaderManager.LoaderCallbacks<ArrayList<Recording>> {
    private View mInflatedView;
    private Context mContext;
    private Boolean mOpened = false;
    private ArrayList<String> alarmSettingsArray = new ArrayList<>();

    private Boolean isActivated;
    private Calendar mDateAndTime = Calendar.getInstance();
    private String mActivityType;
    private String mRingtone;
    private String mReminder;

    private int mSoundSelected =0;

    public static ArrayAdapter<String> myAdapter;
    public static ListView mListView;
    RecordingEntryDbHelper helper;
    Recording mRecording;
    ArrayList<Recording> myRecordings;
    public static android.app.LoaderManager loaderManager;

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
        mInflatedView = inflater.inflate(R.layout.fragment_alarm, container, false);

        FloatingActionButton fab = (FloatingActionButton) mInflatedView.findViewById(R.id.add_alarm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewAlarm();
            }
        });

        return mInflatedView;
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
                    Toast.makeText(mContext, "Changes Not Saved", Toast.LENGTH_LONG).show();
                }
            }
        });

        final Switch alarmSwitch = (Switch) view.findViewById(R.id.alarm_on_switch);
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isActivated = isChecked;
            }
        });

        final ListView settingsList = (ListView) view.findViewById(R.id.alarmOptions);
        settingsList.setAdapter(new ArrayAdapter<String>(mContext, R.layout.alarm_settings_list_text,
                R.id.settings_list_white_text, alarmSettingsArray));
        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        onDateClicked();
                        View dateView = settingsList.getChildAt(position);
                        TextView dateTextView = (TextView) dateView.findViewById(R.id.settings_list_white_text);
                        String date = android.text.format.DateFormat.format("MMM dd yyyy", mDateAndTime).toString();
                        dateTextView.setText("Date: " + date);
                        break;
                    case 1:
                        onActivityClicked();
                        View activityView = settingsList.getChildAt(position);
                        TextView activityTextView = (TextView) activityView.findViewById(R.id.settings_list_white_text);
                        activityTextView.setText("Wakeup Activity: " + mActivityType);
                        break;
                    case 2:
                        onRingtoneClicked();
                        break;
                    case 3:
                        onReminderClicked();
                        break;
                }
            }
        });

        Button saveButton = (Button) view.findViewById(R.id.cardSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                if (mOpened) {
                    slide_up(mContext, settingsView);

                    //ADD ALARM HERE
                    //TODO: add alarm here

                }
                mOpened = false;
                if (!mOpened) {
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

    private void addNewAlarm() {
        // Create dialog to show current time
        TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mDateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mDateAndTime.set(Calendar.MINUTE, minute);

                /*

                Added by Tyler - wasn't able to test it

                 */

                // grab data and create alarm object
                Alarm addAlarm = new Alarm();
                Calendar setTime = Calendar.getInstance();
                setTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                setTime.set(Calendar.MINUTE, minute);
                addAlarm.setmDateTime(setTime);

                // add alarm to database
                AlarmEntryDbHelper helper = new AlarmEntryDbHelper(mContext);
                int id = (int) helper.insertAlarm(addAlarm);

                //add alarm to scheduler
                AlarmScheduler.setAlarm(mContext,id,setTime);

                isActivated = true;
            }
        };

        new TimePickerDialog(mContext, mTimeListener,
                mDateAndTime.get(Calendar.HOUR_OF_DAY),
                mDateAndTime.get(Calendar.MINUTE), true).show();
    }

    private void onActivityClicked() {
        // Create dialog with radio buttons to select wakeup activity
        final AlertDialog.Builder mActivitySelector = new AlertDialog.Builder(mContext);
        mActivitySelector.setTitle("Wakeup Activity:");
        final RadioGroup radioGroup = new RadioGroup(mContext);
        final RadioButton mathButton = new RadioButton(mContext);
        mathButton.setText("Math Problem");
        final RadioButton movementButton = new RadioButton(mContext);
        movementButton.setText("Jumping Jacks");
        final RadioButton speechButton = new RadioButton(mContext);
        speechButton.setText("Record Yourself");
        radioGroup.addView(mathButton);
        radioGroup.addView(movementButton);
        radioGroup.addView(speechButton);
        mActivitySelector.setView(radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case 0:
                        mActivityType = "Math Problem";
                        break;
                    case 1:
                        mActivityType = "Jumping Jacks";
                        break;
                    case 2:
                        mActivityType = "Record Yourself";
                        break;
                }
            }
        });
        mActivitySelector.show();
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

    // Do we need an async loader here? - we may need to add in default ringtones to database
    private void onRingtoneClicked() {
        loaderManager = getActivity().getLoaderManager();
        loaderManager.initLoader(1, null, this).forceLoad();
       // View mInflateView = inflater.inflate(R.layout.fragment_soundlist, container, false);
        //mListView = (ListView) mInflateView.findViewById(R.id.recordingEntries);



//        //TYLER'S CODE
//        AlertDialog.Builder mRingtoneDialog = new AlertDialog.Builder(mContext);
//        mRingtoneDialog.setTitle(R.string.ringtone_dialog_title);
//
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                mContext, R.layout.ringtone_listview);
//
//        // get alarms from db
//        RecordingEntryDbHelper helper = new RecordingEntryDbHelper(mContext);
//        final ArrayList<Recording> recordings = helper.fetchRecordings();
//
//        for (Recording recording:recordings) {
//            adapter.add(recording.getAlarmName());
//        }
//
//        mRingtoneDialog.setNegativeButton("cancel",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//        mRingtoneDialog.setAdapter(adapter,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String alarmName = adapter.getItem(which);
//
//                        Recording recording = recordings.get(which);
//
//                        // need to save this to the temporary object
//                        recording.getFileName();
//
////                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
////                        builder.setMessage(strName);
////                        builder.setTitle("Your Selected Item is");
////                        builder.setPositiveButton(
////                                "Ok",
////                                new DialogInterface.OnClickListener() {
////                                    @Override
////                                    public void onClick(DialogInterface dialog, int which) {
////                                        dialog.dismiss();
////                                    }
////                                });
////                        builder.show();
//                    }
//                });
//
//        mRingtoneDialog.show();


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
                mReminder = inputText.getText().toString();
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

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d("onCreateLoader()", "onCreateLoader()");

        // returns an entry loader using context
        return new RecordingLoader(mContext);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recording>> loader, ArrayList<Recording> data) {
        Log.d("onLoadFinished()", "onLoadFinished()");



        //sets global variable
        myRecordings = data;

        if(!data.isEmpty()) {
            Log.d("onLoadFinished()", "not empty");

            //String[] recordingNames = new String[40];
            final ArrayList<String> recordingNames = new ArrayList<String>();
            int i = 0;
            for (Recording recording : data) {
                Log.d("in recordings", "recording: " + recording.getAlarmName());
                //recordingNames.add(recording.getAlarmName());
                //recordingNames[i] = recording.getAlarmName();
                //i++;
                recordingNames.add(recording.getAlarmName());
                //Log.d("in recordings", "recording: " + recordingNames[i]);
                Log.d("in recordings", "recording: " + recordingNames.toArray());


            }

            //sets adapter to array list of exercises

            // Define a new adapter
            myAdapter = new ArrayAdapter<String>(mContext,
                    R.layout.check_listview_layout, recordingNames);
            Log.d("onLoadFinished()", "got adapter");

            AlertDialog.Builder mRingtoneDialog = new AlertDialog.Builder(mContext);
            mRingtoneDialog.setTitle(R.string.ringtone_dialog_title);
            ArrayList<Integer> mSelectedItems = new ArrayList();  // Where we track the selected items


            mRingtoneDialog.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            mRingtoneDialog.setSingleChoiceItems(myAdapter, mSoundSelected,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("checked the item", "checked: " + which);

                            mSoundSelected = which;
                        }

                    });
            mRingtoneDialog.setPositiveButton("save",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                        //ARRAY UNCOMMENT
//            mRingtoneDialog.setAdapter(myAdapter,
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            String alarmName = myAdapter.getItem(which);
//                            Log.d("alarm name onclick", "alarm name is " + alarmName);
//                            Recording recording = myRecordings.get(which);
//
//                            // need to save this to the temporary object
//                            recording.getFileName();
//
////                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
////                        builder.setMessage(strName);
////                        builder.setTitle("Your Selected Item is");
////                        builder.setPositiveButton(
////                                "Ok",
////                                new DialogInterface.OnClickListener() {
////                                    @Override
////                                    public void onClick(DialogInterface dialog, int which) {
////                                        dialog.dismiss();
////                                    }
////                                });
////                        builder.show();
//                        }
//                    });

            mRingtoneDialog.show();







            // Assign the adapter to ListView
            //setListAdapter(mAdapter);
            //myAdapter = new ExerciseLineArrayAdapter(mContext, data);
            //mListView.setListAdapter(myAdapter);
            //mListView.setAdapter(myAdapter);
            Log.d("onLoadFinished()", "set adapter");

        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recording>> loader) {
        Log.d("onLoaderReset()", "onLoaderReset()");

        //reloads exercises into adapter
        myAdapter.clear();
        myAdapter.notifyDataSetChanged();


    }
    @Override
    public void onResume() {
        Log.d("onResume()", "onResume()");

        super.onResume();

        //reloads the list when onResume is called
        //loaderManager.initLoader(1, null, this).forceLoad();
    }
}
