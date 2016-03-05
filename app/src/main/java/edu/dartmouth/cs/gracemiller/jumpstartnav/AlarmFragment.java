package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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


public class AlarmFragment extends Fragment  {
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

    public  AlarmAdapter mAlarmAdapter;
    public  RecyclerView mRecyclerView;
    public  RecyclerView.LayoutManager mLayoutManager;
    public ArrayList<Alarm> mDataset;
    public AlarmEntryDbHelper mAlarmDbHelper;

    private static final int ALARM_LOADER_ID = 1;
    private static final int RECORDING_LOADER_ID = 2;

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

        Alarm myAlarm = new Alarm();
        myAlarm.setmAlarmType(0);
        Calendar cal = Calendar.getInstance();
        myAlarm.setmDateTime(cal);
        myAlarm.setmActive(0);

        AlarmEntryDbHelper helper = new AlarmEntryDbHelper(mContext);
        helper.insertAlarm(myAlarm);




        // Inflate the layout for this fragment
        mInflatedView = inflater.inflate(R.layout.fragment_alarm, container, false);


        loaderManager = getActivity().getLoaderManager();
        loaderManager.initLoader(1, null, alarmLoaderListener).forceLoad();

        mRecyclerView = (RecyclerView) mInflatedView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);


        return mInflatedView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("getting above fab", "above fab");
        FloatingActionButton fab = (FloatingActionButton) mInflatedView.findViewById(R.id.add_alarm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAGG", "FAB click");
                addNewAlarm();
            }
        });

//        LinearLayout cardList = (LinearLayout) view.findViewById(R.id.cardContainer);
//
//        final CardView alarmView = (CardView) view.findViewById(R.id.cardview);
//        final LinearLayout settingsView = (LinearLayout) view.findViewById(R.id.expandedView);
//        alarmView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!mOpened) {
//                    alarmView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                    settingsView.setVisibility(View.VISIBLE);
//                    slide_down(mContext, settingsView);
//                    mOpened = true;
//                } else if (mOpened) {
//                    alarmView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//                    slide_up(mContext, settingsView);
//                    settingsView.setVisibility(View.GONE);
//                    mOpened = false;
//                    Toast.makeText(mContext, "Changes Not Saved", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//        final Switch alarmSwitch = (Switch) view.findViewById(R.id.alarm_on_switch);
//        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                isActivated = isChecked;
//            }
//        });
//
//        final ListView settingsList = (ListView) view.findViewById(R.id.alarmOptions);
//        settingsList.setAdapter(new ArrayAdapter<String>(mContext, R.layout.alarm_settings_list_text,
//                R.id.settings_list_white_text, alarmSettingsArray));
//        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 0:
//                        onDateClicked();
//                        View dateView = settingsList.getChildAt(position);
//                        TextView dateTextView = (TextView) dateView.findViewById(R.id.settings_list_white_text);
//                        String date = android.text.format.DateFormat.format("MMM dd yyyy", mDateAndTime).toString();
//                        dateTextView.setText("Date: " + date);
//                        break;
//                    case 1:
//                        onActivityClicked();
//                        View activityView = settingsList.getChildAt(position);
//                        TextView activityTextView = (TextView) activityView.findViewById(R.id.settings_list_white_text);
//                        activityTextView.setText("Wakeup Activity: " + mActivityType);
//                        break;
//                    case 2:
//                        onRingtoneClicked();
//                        View ringtoneView = settingsList.getChildAt(position);
//                        TextView ringtoneTestView = (TextView) ringtoneView.findViewById(R.id.settings_list_white_text);
//                        ringtoneTestView.setText("Ringtone: " + mRingtone);
//                        break;
//                    case 3:
//                        onReminderClicked();
//                        break;
//                }
//
//            }
//        });
//
    }

    private void addNewAlarm() {
        Log.d("addNewAlarm()", "add");
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

    private LoaderManager.LoaderCallbacks<ArrayList<Recording>> recordingLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<Recording>>() {
        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            Log.d("onCreateRecLoader()", "onCreateRecordingLoader()");

            // returns an entry loader using context
            return new RecordingLoader(mContext);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Recording>> loader, ArrayList<Recording> data) {
            Log.d("onLoadFinished()", "onLoadFinished()");


            //sets global variable
            myRecordings = data;

            if (!data.isEmpty()) {
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
                                RecordingEntryDbHelper helper = new RecordingEntryDbHelper(mContext);

                                mRingtone = myRecordings.get(which).getAlarmName();
                                Log.d("ringtone is", "ringstone is : " + mRingtone);


                            }

                        });
                mRingtoneDialog.setPositiveButton("save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


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


    };

    private LoaderManager.LoaderCallbacks<ArrayList<Alarm>> alarmLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<Alarm>>() {
        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            Log.d("onALARMLOADERCREATE()", "onALARM LOADER CREATE()");

            // returns an entry loader using context
            return new AlarmLoader(mContext);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Alarm>> loader, ArrayList<Alarm> data) {
            Log.d("onLoaderFinished()", "onLoaderFinishALARM()");

            //sets global variable
            mDataset = data;

//            if(!data.isEmpty()) {
//                Log.d("onLoadFinished()", "not empty");
//
//                //String[] recordingNames = new String[40];
//                ArrayList<String> recordingNames = new ArrayList<String>();
//                int i = 0;
//                for (Recording recording : data) {
//                    Log.d("in recordings", "recording: " + recording.getAlarmName());
//                    //recordingNames.add(recording.getAlarmName());
//                    //recordingNames[i] = recording.getAlarmName();
//                    //i++;
//                    recordingNames.add(recording.getAlarmName());
//                    //Log.d("in recordings", "recording: " + recordingNames[i]);
//                    Log.d("in recordings", "recording: " + recordingNames.toArray());
//
//
//                }

            //sets adapter to array list of exercises

            // Define a new adapter
//                myAdapter = new ArrayAdapter<String>(mContext,
//                        R.layout.listview_layout, recordingNames);

            mAlarmAdapter = new AlarmAdapter(mDataset);
            Log.d("onalarmLoadFinished()", "got adapter");


            // Assign the adapter to ListView
            //setListAdapter(mAdapter);
            //myAdapter = new ExerciseLineArrayAdapter(mContext, data);
            //mListView.setListAdapter(myAdapter);
            mRecyclerView.setAdapter(mAlarmAdapter);
            Log.d("onalarmLoadFinished()", "set adapter");
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Alarm>> loader) {
            Log.d("onalarmLoaderReset()", "onLoaderReset()");

            //reloads exercises into adapter
           // mAlarmAdapter.clear();
            mAlarmAdapter.notifyDataSetChanged();


        }


    };

    // Do we need an async loader here? - we may need to add in default ringtones to database
    private void onRingtoneClicked() {
        loaderManager = getActivity().getLoaderManager();
        //loaderManager.initLoader(1, null, this).forceLoad();
        loaderManager.initLoader(4, null, recordingLoaderListener).forceLoad();



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


    @Override
    public void onResume() {
        Log.d("onResume()", "onResume()");

        super.onResume();

        //reloads the list when onResume is called
        //loaderManager.initLoader(1, null, this).forceLoad();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d("adding alarm", "add");
            addNewAlarm();
        }

        return super.onOptionsItemSelected(item);
    }
}


