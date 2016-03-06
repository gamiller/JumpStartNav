package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers.AlarmPlayer;
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

    private Boolean isActivated = false;
    private Calendar mDateAndTime = Calendar.getInstance();
    private String mActivityType = "";
    private String mRingtone = "";
    private String mRingtoneName = "Default";
    private String mReminder="";
    private int mDefindex = RingtoneManager.TYPE_ALARM;

    private TextView currentRingtoneTV;

    private int mSoundSelected = 0;

    public static ArrayAdapter<String> myAdapter;
    public static ListView mListView;
    RecordingEntryDbHelper helper;
    Recording mRecording;
    ArrayList<Recording> myRecordings;
    public static android.app.LoaderManager loaderManager;

    public AlarmEntryDbHelper mAlarmDbHelper;

    //TODO: ALARM LIST IMPLEMENTING
    public  ListView mAlarmListView;
    public AlarmListAdapter aListAdapter;
    //ArrayList<Alarm> myAlarms;
    public ArrayList<Alarm> mDataset;
    AlarmEntryDbHelper alarmHelper;

    HashMap<Long, Boolean> mOpenMap = new HashMap<Long, Boolean>();

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {

            Uri ringtoneURI = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            mDefindex = 3;

            mRingtone = ringtoneURI.toString();
            mRingtoneName = "Default";
            updateTextView(mRingtoneName);

        }
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
        mInflatedView = inflater.inflate(R.layout.fragment_alarmlist, container, false);
        mAlarmListView = (ListView) mInflatedView.findViewById(R.id.alarmEntriesList);

        loaderManager = getActivity().getLoaderManager();
        loaderManager.initLoader(1, null, alarmLoaderListener).forceLoad();

        mAlarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                Log.d("onalarmclick", "in onalarmclick()");

                final Alarm alarmPre = mDataset.get(position);
                final long alarmId = alarmPre.getId();
                alarmHelper = new AlarmEntryDbHelper(mContext);
                final Alarm alarm = alarmHelper.fetchAlarmByIndex(alarmId);

                if(alarm.getmActive() == 0) {
                    isActivated = false;
                } else {
                    isActivated = true;
                }

                mDateAndTime = alarm.getmDateTime();

                String oldAlarmType = "";
                switch (alarm.getmAlarmType()) {
                    case 0:
                        mActivityType = "Math Problem";
                        oldAlarmType = "Math Problem";
                        break;
                    case 1:
                        mActivityType = "Jumping Jacks";
                        oldAlarmType = "Jumping Jacks";
                        break;
                    case 2:
                        mActivityType = "Record Yourself";
                        oldAlarmType = "Record Yourself";
                        break;
                }

                mReminder = alarm.getmReminder();


                String oldAlarmRingtone = alarm.getmRingToneFile();

                mRingtone = alarm.getmRingToneFile();


                Log.d("hit alarm", "alarm hit is position " + position + " and id " + alarmId);
                boolean mOpen = false;
                if (mOpenMap.containsKey(alarmId)) {
                    mOpen = mOpenMap.get(alarmId);
                }


                final CardView cardView = (CardView) view.findViewById(R.id.cardView);
                final LinearLayout expandedView = (LinearLayout) view.findViewById(R.id.expandedView);

                final Switch activeSwitch = (Switch) view.findViewById(R.id.alarm_on_switch);
                activeSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (activeSwitch.isChecked()) {
                            isActivated = true;
                        } else {
                            isActivated = false;
                        }
                        updateAlarm(alarmId);
                    }
                });

                final Button saveButton = (Button) view.findViewById(R.id.cardSaveButton);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateAlarm(alarmId);
                        cardView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        slide_up(mContext, expandedView);
                        expandedView.setVisibility(View.GONE);
                        //mOpened = false;
                        mOpenMap.put(alarmId, false);
                        Toast.makeText(mContext, "Saved!", Toast.LENGTH_SHORT).show();


                    }
                });

                final Button deleteButton = (Button) view.findViewById(R.id.cardDeleteButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmHelper.removeAlarm(alarmId);
                        cardView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        slide_up(mContext, expandedView);
                        expandedView.setVisibility(View.GONE);
                        //mOpened = false;
                        mOpenMap.put(alarmId, false);
                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                        FragmentTransaction tr = getFragmentManager().beginTransaction();
                        Fragment mAlarmFrag = new AlarmFragment();
                        tr.replace(R.id.fragment_holder, mAlarmFrag).commit();

                    }
                });

                if (!mOpen) {
                    boolean otherAlarmOpen = false;
                    //for (long i = 0; i < mOpenMap.size(); i++) {
                    for (long i : mOpenMap.keySet()) {
                        if (mOpenMap.get(i)) {
                            otherAlarmOpen = mOpenMap.get(i);
                            Toast.makeText(mContext, "Cannot edit two alarms at once\nClose open alarm",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    //}

                    if (!otherAlarmOpen) {
                        TextView dateText = (TextView) cardView.findViewById(R.id.alarm_date_textview);
                        dateText.setText("Date: " + android.text.format.DateFormat.format("MMM dd yyyy", mDateAndTime).toString());

                        TextView wakeupActivityText = (TextView) cardView.findViewById(R.id.alarm_wakeup_activity_textview);
                        wakeupActivityText.setText("Wakeup Activity: " + oldAlarmType);

                        TextView ringtoneText = (TextView) cardView.findViewById(R.id.alarm_ringtone_textview);
                        ringtoneText.setText("Ringtone: " + oldAlarmRingtone);

                        expandedView.setVisibility(View.VISIBLE);
                        cardView.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
                        slide_down(mContext, expandedView);
                        mOpenMap.put(alarmId, true);
                    }
                } else if (mOpen) {
                    cardView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    slide_up(mContext, expandedView);
                    expandedView.setVisibility(View.GONE);

                    TextView dateText = (TextView) cardView.findViewById(R.id.alarm_date_textview);
                    dateText.setText("Date: " + android.text.format.DateFormat.format("MMM dd yyyy", mDateAndTime).toString());

                    TextView wakeupActivityText = (TextView) cardView.findViewById(R.id.alarm_wakeup_activity_textview);
                    wakeupActivityText.setText("Wakeup Activity: " + oldAlarmType);

                    TextView ringtoneText = (TextView) cardView.findViewById(R.id.alarm_ringtone_textview);
                    ringtoneText.setText("Ringtone: " + oldAlarmRingtone);

                    mOpenMap.put(alarmId, false);
                    Toast.makeText(mContext, "Changes Not Saved", Toast.LENGTH_SHORT).show();
                }

                final TextView dateTextView = (TextView) cardView.findViewById(R.id.alarm_date_textview);
                dateTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar time = alarm.getmDateTime();
                        onDateClicked(time, dateTextView);
                        String date = android.text.format.DateFormat.format("MMM dd yyyy", mDateAndTime).toString();
                        dateTextView.setText("Date: " + date);
                    }
                });

                final TextView wakeupTextView = (TextView) cardView.findViewById(R.id.alarm_wakeup_activity_textview);
                wakeupTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int alarmType = alarm.getmAlarmType();
                        onActivityClicked(wakeupTextView, alarmType);
                        wakeupTextView.setText("Wakeup Activity: " + mActivityType);
                    }
                });

                final TextView ringtoneTextView = (TextView) cardView.findViewById(R.id.alarm_ringtone_textview);
                ringtoneTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String oldRingtone = alarm.getmRingToneFile();
                        mRingtone = oldRingtone;
                        onRingtoneClicked(ringtoneTextView);
                        ringtoneTextView.setText("Ringtone: " + mRingtoneName);
                    }
                });

                final TextView reminderTextView = (TextView) cardView.findViewById(R.id.alarm_reminder_textview);
                reminderTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReminderClicked(mReminder);
                        reminderTextView.setText("Reminder");
                    }
                });
            }
        });

        Log.d("getting above fab", "above fab");
        FloatingActionButton fab = (FloatingActionButton) mInflatedView.findViewById(R.id.add_alarm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAGG", "FAB click");
                addNewAlarm();
            }
        });
        return mInflatedView;
    }

    private void updateAlarm(Long position){
        AlarmEntryDbHelper helper = new AlarmEntryDbHelper(mContext);
        Alarm oldAlarm = helper.fetchAlarmByIndex(position);

        Alarm alarm = new Alarm();
        alarm.setId(oldAlarm.getId());
        alarm.setmDateTime(oldAlarm.getmDateTime());
        alarm.setmActive(oldAlarm.getmActive());
        alarm.setmReminder(oldAlarm.getmReminder());
        alarm.setmRingToneFile(oldAlarm.getmRingToneFile());
        alarm.setmAlarmType(oldAlarm.getmAlarmType());

        alarm.setmDateTime(mDateAndTime);

        int active;
        if(isActivated && alarm.getmActive() == 0){
            active = 1;
            AlarmScheduler.deleteAlarm(mContext,(int) alarm.getId());
            AlarmScheduler.setAlarm(mContext, (int) alarm.getId(), alarm.getmDateTime());
            alarm.setmActive(active);
        } else if (!isActivated && alarm.getmActive() == 1){
            AlarmScheduler.deleteAlarm(mContext,(int) alarm.getId());
            active = 0;
            alarm.setmActive(active);
        } else {
            AlarmScheduler.deleteAlarm(mContext,(int) alarm.getId());
            AlarmScheduler.setAlarm(mContext,(int)alarm.getId(),alarm.getmDateTime());
            alarm.setmActive(oldAlarm.getmActive());
        }

        int alarmType = 0;
        switch(mActivityType) {
            case "Jumping Jacks":
                alarmType = 1;
                break;
            case "Record Yourself":
                alarmType = 2;
                break;
        }
        alarm.setmAlarmType(alarmType);
        alarm.setmRingToneFile(mRingtone);
        alarm.setmReminder(mReminder);
        alarm.setDefaultIndex(mDefindex);
        //helper.removeAlarm(position);
        helper.updateAlarm(alarm);
    }

    private void addNewAlarm() {

        // Create dialog to show current time
        TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mDateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mDateAndTime.set(Calendar.MINUTE, minute);

                // grab data and create alarm object
                Alarm addAlarm = new Alarm();
                Calendar setTime = Calendar.getInstance();
                setTime.setTimeInMillis(System.currentTimeMillis());
                setTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                setTime.set(Calendar.MINUTE, minute);
                addAlarm.setmDateTime(setTime);
                addAlarm.setmReminder("");
                addAlarm.setmActive(1);

                // add alarm to database
                AlarmEntryDbHelper helper = new AlarmEntryDbHelper(mContext);
                int id = (int) helper.insertAlarm(addAlarm);
                helper.close();

                //add alarm to scheduler
                AlarmScheduler.setAlarm(mContext,id,setTime);

                isActivated = true;

                FragmentTransaction tr = getFragmentManager().beginTransaction();
                Fragment mAlarmFrag = new AlarmFragment();
                tr.replace(R.id.fragment_holder, mAlarmFrag).commit();
            }
        };

        new TimePickerDialog(mContext, mTimeListener,
                mDateAndTime.get(Calendar.HOUR_OF_DAY),
                mDateAndTime.get(Calendar.MINUTE), true).show();
    }

    private void onActivityClicked(final TextView view, final int oldAlarmType) {

        String[] activityTypes = {"Math Problem", "Jumping Jacks", "Record Yourself"};
        // Define a new adapter
        myAdapter = new ArrayAdapter<String>(mContext,
                R.layout.check_listview_layout, activityTypes);
        Log.d("onLoadFinished()", "got adapter");

        AlertDialog.Builder mActivityDialog = new AlertDialog.Builder(mContext);
        mActivityDialog.setTitle("WakeUp Activity Type");
        ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();  // Where we track the selected items
        mActivityDialog.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (oldAlarmType) {
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
                        dialog.dismiss();
                    }
                });
        mActivityDialog.setSingleChoiceItems(myAdapter, oldAlarmType,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("checked the item", "checked: " + which);

                        switch (which) {
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
        mActivityDialog.setPositiveButton("save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.setText("Wakeup Activity: " + mActivityType);
                        dialog.dismiss();
                    }
                });


        mActivityDialog.show();
    }

    private void onDateClicked(Calendar time, final TextView tView) {

        final Calendar oldTime = time;
        // Create dialog to show current date
        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                mDateAndTime.set(Calendar.YEAR, year);
                mDateAndTime.set(Calendar.MONTH, monthOfYear);
                mDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDateAndTime.set(Calendar.HOUR, oldTime.get(Calendar.HOUR));
                mDateAndTime.set(Calendar.MINUTE, oldTime.get(Calendar.MINUTE));
                mDateAndTime.set(Calendar.AM_PM, oldTime.get(Calendar.AM_PM));

                String date = android.text.format.DateFormat.format("MMM dd yyyy", mDateAndTime).toString();
                tView.setText("Date: " + date);
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

                // Define a new adapter
                myAdapter = new ArrayAdapter<String>(mContext,
                        R.layout.check_listview_layout, recordingNames);
                Log.d("onLoadFinished()", "got adapter");

                AlertDialog.Builder mRingtoneDialog = new AlertDialog.Builder(mContext);
                mRingtoneDialog.setTitle(R.string.ringtone_dialog_title);
                final ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();  // Where we track the selected items

                mRingtoneDialog.setNegativeButton("Cancel",
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
                mRingtoneDialog.setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                RecordingEntryDbHelper helper = new RecordingEntryDbHelper(mContext);

                                mRingtone = myRecordings.get(mSoundSelected).getFileName();
                                Log.d("ringtone is", "ringtone is : " + mRingtone);

                                helper.close();

                                mRingtoneName = myRecordings.get(mSoundSelected).getAlarmName();
                                updateTextView(mRingtoneName);
                                dialog.dismiss();
                            }
                        });
                mRingtoneDialog.show();

                Log.d("onLoadFinished()", "set adapter");
            } else {
                Toast.makeText(mContext, "No custom ringtones found", Toast.LENGTH_SHORT).show();
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

//    private LoaderManager.LoaderCallbacks<ArrayList<Alarm>> alarmLoaderListener
//            = new LoaderManager.LoaderCallbacks<ArrayList<Alarm>>() {
//        @Override
//        public Loader onCreateLoader(int id, Bundle args) {
//            Log.d("onALARMLOADERCREATE()", "onALARM LOADER CREATE()");
//
//            // returns an entry loader using context
//            return new AlarmLoader(mContext);
//        }
//
//        @Override
//        public void onLoadFinished(Loader<ArrayList<Alarm>> loader, ArrayList<Alarm> data) {
//            Log.d("onLoaderFinished()", "onLoaderFinishALARM()");
//
//            //sets global variable
//            mDataset = data;
//
////            if(!data.isEmpty()) {
////                Log.d("onLoadFinished()", "not empty");
////
////                //String[] recordingNames = new String[40];
////                ArrayList<String> recordingNames = new ArrayList<String>();
////                int i = 0;
////                for (Recording recording : data) {
////                    Log.d("in recordings", "recording: " + recording.getAlarmName());
////                    //recordingNames.add(recording.getAlarmName());
////                    //recordingNames[i] = recording.getAlarmName();
////                    //i++;
////                    recordingNames.add(recording.getAlarmName());
////                    //Log.d("in recordings", "recording: " + recordingNames[i]);
////                    Log.d("in recordings", "recording: " + recordingNames.toArray());
////
////
////                }
//
//            //sets adapter to array list of exercises
//
//            // Define a new adapter
////                myAdapter = new ArrayAdapter<String>(mContext,
////                        R.layout.listview_layout, recordingNames);
//
//            mAlarmAdapter = new AlarmAdapter(mDataset);
//            Log.d("onalarmLoadFinished()", "got adapter");
//
//
//            // Assign the adapter to ListView
//            //setListAdapter(mAdapter);
//            //myAdapter = new ExerciseLineArrayAdapter(mContext, data);
//            //mListView.setListAdapter(myAdapter);
//            mRecyclerView.setAdapter(mAlarmAdapter);
//            Log.d("onalarmLoadFinished()", "set adapter");
//        }
//
//        @Override
//        public void onLoaderReset(Loader<ArrayList<Alarm>> loader) {
//            Log.d("onalarmLoaderReset()", "onLoaderReset()");
//
//            //reloads exercises into adapter
//           // mAlarmAdapter.clear();
//            mAlarmAdapter.notifyDataSetChanged();
//
//
//        }
//
//
//    };

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

        aListAdapter = new AlarmListAdapter(mContext, R.layout.cardview_layout, mDataset);
        //View view = getView().findViewById(R.layout.cardview_layout);
        Log.d("onalarmLoadFinished()", "got adapter");

        for (Alarm alarm : mDataset) {
            CardView mCardView = new CardView(mContext);
            mCardView = (CardView) mInflatedView.findViewById(R.id.cardView);

            if(mCardView != null) {
                mCardView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            aListAdapter.add(mCardView);
        }

        mAlarmListView.setAdapter(aListAdapter);
        Log.d("onalarmLoadFinished()", "set adapter");
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Alarm>> loader) {
        Log.d("onalarmLoaderReset()", "onLoaderReset()");

        //reloads exercises into adapter
        // mAlarmAdapter.clear();
        aListAdapter.notifyDataSetChanged();
    }


};

    // Do we need an async loader here? - we may need to add in default ringtones to database
    private void onRingtoneClicked(final TextView view) {

        currentRingtoneTV = view;

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_twobutton_recording, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setCancelable(true)
                .setView(dialogView);
        builder.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        dialog.cancel();
                    }
                });

        Button DefaultButton =  (Button) dialogView.findViewById(R.id.button_play_dialog);
        Button CustomButton =  (Button) dialogView.findViewById(R.id.button_delete_dialog);
        DefaultButton.setText("Default");
        CustomButton.setText("Custom");


        final AlertDialog alert = builder.create();

        DefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load default ringtone list
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Ringtone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                startActivityForResult(intent, 2);
                alert.dismiss();
            }
        });

        CustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loaderManager = getActivity().getLoaderManager();
                //loaderManager.initLoader(1, null, this).forceLoad();
                loaderManager.initLoader(4, null, recordingLoaderListener).forceLoad();

                mRingtoneName = "Custom";
                updateTextView(mRingtoneName);
                alert.dismiss();
            }
        });
        alert.show();


        view.setText("Ringtone: " + mRingtoneName);
    }

    private void updateTextView (String name) {
        currentRingtoneTV.setText("Ringtone: " + name);
    }

    private void onReminderClicked(String oldReminder) {
        // Create dialog with EditText widget to input reminder
        AlertDialog.Builder mReminderDialog = new AlertDialog.Builder(mContext);
        mReminderDialog.setTitle(R.string.reminder_dialog_title);
        final EditText inputText = new EditText(mContext);
        if(!mReminder.equals(String.valueOf(""))){
            inputText.setText(oldReminder);
        }
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
                Log.d("mReminder text is now", "" + mReminder);
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


