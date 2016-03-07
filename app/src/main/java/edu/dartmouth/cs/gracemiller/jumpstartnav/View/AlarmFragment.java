package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

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
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmControllers.AlarmListAdapter;
import edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmControllers.AlarmLoader;
import edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers.AlarmScheduler;
import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Alarm;
import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Recording;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.AlarmEntryDbHelper;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.RecordingEntryDbHelper;
import edu.dartmouth.cs.gracemiller.jumpstartnav.R;
import edu.dartmouth.cs.gracemiller.jumpstartnav.RecordingControllers.RecordingLoader;

/*
Alarm fragment holds all of the alarms in a listview and allows the user to add
an alarm, edit, delete, save alarms
 */

public class AlarmFragment extends Fragment {
    public static ArrayAdapter<String> myAdapter;
    public static ListView mListView;
    public static android.app.LoaderManager loaderManager;

    public AlarmEntryDbHelper mAlarmDbHelper;
    public ListView mAlarmListView;
    public AlarmListAdapter aListAdapter;
    public ArrayList<Alarm> mDataset;
    HashMap<Long, Boolean> mOpenMap = new HashMap<Long, Boolean>();

    RecordingEntryDbHelper helper;
    Recording mRecording;
    ArrayList<Recording> myRecordings;

    private View mInflatedView;
    private Context mContext;

    private ArrayList<String> alarmSettingsArray = new ArrayList<>();

    private Boolean isActivated = false;
    private Calendar mDateAndTime = Calendar.getInstance();
    private String mActivityType = "";
    private String mRingtone = "";
    private String mRingtoneName = "Default";
    private String mReminder = "";
    private int mDefindex = RingtoneManager.TYPE_ALARM;

    private TextView currentRingtoneTV;
    private int mSoundSelected = 0;


    //recording loader for the recordings when the ringtone selected is chosen
    private LoaderManager.LoaderCallbacks<ArrayList<Recording>> recordingLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<Recording>>() {

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            // returns an entry loader using context
            return new RecordingLoader(mContext);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Recording>> loader, ArrayList<Recording> data) {
            //sets global variable
            myRecordings = data;

            //load all recordings in the database
            if (!data.isEmpty()) {
                final ArrayList<String> recordingNames = new ArrayList<String>();

                for (Recording recording : data) {
                    recordingNames.add(recording.getAlarmName());
                }

                // Define a new adapter for the recordings
                myAdapter = new ArrayAdapter<String>(mContext,
                        R.layout.check_listview_layout, recordingNames);

                //create a dialogue for these recordings
                AlertDialog.Builder mRingtoneDialog = new AlertDialog.Builder(mContext);
                mRingtoneDialog.setTitle(R.string.ringtone_dialog_title);

                //if dont chose a ringtone dismiss
                mRingtoneDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                //can chose one custom ringtone
                mRingtoneDialog.setSingleChoiceItems(myAdapter, mSoundSelected,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSoundSelected = which;
                            }

                        });

                //save custom ringtone as new ringtone
                mRingtoneDialog.setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RecordingEntryDbHelper helper = new RecordingEntryDbHelper(mContext);

                                mRingtone = myRecordings.get(mSoundSelected).getFileName();

                                helper.close();

                                //get ringtone file and put in db
                                mRingtoneName = myRecordings.get(mSoundSelected).getAlarmName();
                                updateTextView(mRingtoneName);
                                dialog.dismiss();
                            }
                        });
                mRingtoneDialog.show();

            } else {
                Toast.makeText(mContext, "No custom ringtones found", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Recording>> loader) {
            //reloads exercises into adapter
            myAdapter.clear();
            myAdapter.notifyDataSetChanged();
        }
    };

    //loads all of the alarms for the view of the fragment
    private LoaderManager.LoaderCallbacks<ArrayList<Alarm>> alarmLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<Alarm>>() {

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            // returns an entry loader using context
            return new AlarmLoader(mContext);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Alarm>> loader, ArrayList<Alarm> data) {
            //sets global variable
            mDataset = data;

            //create custom list adapter for the cards and the alarms
            aListAdapter = new AlarmListAdapter(mContext, R.layout.cardview_layout, mDataset);

            for (Alarm alarm : mDataset) {
                //place alarm in custom cardView
                CardView mCardView = new CardView(mContext);
                mCardView = (CardView) mInflatedView.findViewById(R.id.cardView);

                if (mCardView != null) {
                    mCardView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                aListAdapter.add(mCardView);
            }

            mAlarmListView.setAdapter(aListAdapter);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Alarm>> loader) {
            aListAdapter.notifyDataSetChanged();
        }
    };

    public AlarmFragment() {
        // Required empty public constructor
    }

    public static AlarmFragment newInstance() {
        AlarmFragment fragment = new AlarmFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    //slide down animation for each indidividual option to chow editing options
    //for the alarm
    public static void slide_down(Context context, View view) {
        //load the slide down aniamtion and present to the viewer
        Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_down);

        if (a != null) {
            a.reset();
            if (view != null) {
                view.clearAnimation();
                view.startAnimation(a);
            }
        }
    }

    //slide up animation for each indidividual option to close editing options
    //for the alarm
    public static void slide_up(Context context, View view) {
        //load the slide up aniamtion and present to the viewer
        Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_up);

        if (a != null) {
            a.reset();
            if (view != null) {
                view.clearAnimation();
                view.startAnimation(a);
            }
        }
    }

    //chose the ringtone for the alarm to use
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            //get URI from the ringtone manager
            Uri ringtoneURI = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtoneURI == null) {
                //if null use default tone
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                mRingtone = notification.toString();
            } else {
                mRingtone = ringtoneURI.toString();
            }

            mDefindex = 3;
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
        mInflatedView = inflater.inflate(R.layout.fragment_alarm, container, false);
        mAlarmListView = (ListView) mInflatedView.findViewById(R.id.alarmEntriesList);

        //load all of the alarms into the custom listview
        loaderManager = getActivity().getLoaderManager();
        loaderManager.initLoader(1, null, alarmLoaderListener).forceLoad();

        //if the alarm is selected
        mAlarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                //get the alarm information for that position
                final Alarm alarmPre = mDataset.get(position);
                final long alarmId = alarmPre.getId();
                mAlarmDbHelper = new AlarmEntryDbHelper(mContext);
                final Alarm alarm = mAlarmDbHelper.fetchAlarmByIndex(alarmId);

                //set whether active for the switch
                if (alarm.getmActive() == 0) {
                    isActivated = false;
                } else {
                    isActivated = true;
                }

                //get initial date and time to display
                mDateAndTime = alarm.getmDateTime();
                //get initial alarmtype to display
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

                String oldAlarmRingtone = alarm.getmRingToneFile();

                //get initial type of ringtone for the textviews
                int defIndex = alarm.getDefaultIndex();

                if (defIndex == 3) {
                    oldAlarmRingtone = "Default";
                    mRingtone = "Default";
                } else {
                    oldAlarmRingtone = "Custom";
                    mRingtone = "Custom";

                }

                //get initial reminder
                mReminder = alarm.getmReminder();

                //check if the alarm is open or not to decide on the animation
                boolean mOpen = false;
                if (mOpenMap.containsKey(alarmId)) {
                    mOpen = mOpenMap.get(alarmId);
                }

                final CardView cardView = (CardView) view.findViewById(R.id.cardView);
                final LinearLayout expandedView = (LinearLayout) view.findViewById(R.id.expandedView);

                //if the click is on the switch either switch to on or off
                final Switch activeSwitch = (Switch) view.findViewById(R.id.alarm_on_switch);
                activeSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //change the activity
                        if (activeSwitch.isChecked()) {
                            isActivated = true;
                        } else {
                            isActivated = false;
                        }
                        //set alarm as active or not
                        updateAlarm(alarmId);
                    }
                });

                //save the alarm with the given edits
                final Button saveButton = (Button) view.findViewById(R.id.cardSaveButton);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateAlarm(alarmId);

                        //change background and slide up
                        cardView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        slide_up(mContext, expandedView);
                        expandedView.setVisibility(View.GONE);

                        //make sure that only one alarm opened at a time, set as closed
                        mOpenMap.put(alarmId, false);
                        Toast.makeText(mContext, "Saved!", Toast.LENGTH_SHORT).show();
                    }
                });

                //delete the alarm
                final Button deleteButton = (Button) view.findViewById(R.id.cardDeleteButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlarmDbHelper.removeAlarm(alarmId);

                        cardView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        slide_up(mContext, expandedView);
                        expandedView.setVisibility(View.GONE);

                        //make sure that only one alarm opened at a time, set as closed
                        mOpenMap.put(alarmId, false);
                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();

                        //reload the fragment with the updated list
                        FragmentTransaction tr = getFragmentManager().beginTransaction();
                        Fragment mAlarmFrag = new AlarmFragment();
                        tr.replace(R.id.fragment_holder, mAlarmFrag).commit();
                    }
                });

                //if the alarm is not opened, open it
                if (!mOpen) {
                    boolean otherAlarmOpen = false;
                    for (long i : mOpenMap.keySet()) {
                        //if any other alarm is opened
                        if (mOpenMap.get(i)) {
                            //dont let the user open another alarm while other alarm open for saving purposes
                            otherAlarmOpen = mOpenMap.get(i);
                            Toast.makeText(mContext, "Cannot edit two alarms at once\nClose open alarm",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }

                    //if no other alarm open, open this alarm
                    if (!otherAlarmOpen) {
                        //set the textviews about the alarm to the information stored in the alarm database
                        TextView dateText = (TextView) cardView.findViewById(R.id.alarm_date_textview);
                        dateText.setText("Date: " + android.text.format.DateFormat.format("MMM dd yyyy", mDateAndTime).toString());

                        TextView wakeupActivityText = (TextView) cardView.findViewById(R.id.alarm_wakeup_activity_textview);
                        wakeupActivityText.setText("Wakeup Activity: " + oldAlarmType);

                        TextView ringtoneText = (TextView) cardView.findViewById(R.id.alarm_ringtone_textview);
                        ringtoneText.setText("Ringtone: " + oldAlarmRingtone);

                        //show the expanded alarm view with animation and changed color
                        expandedView.setVisibility(View.VISIBLE);
                        cardView.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
                        slide_down(mContext, expandedView);
                        mOpenMap.put(alarmId, true);
                    }
                } else if (mOpen) {
                    //if alarm is opened then take away the expanded alarm view
                    cardView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    slide_up(mContext, expandedView);
                    expandedView.setVisibility(View.GONE);

                    TextView dateText = (TextView) cardView.findViewById(R.id.alarm_date_textview);
                    dateText.setText("Date: " + android.text.format.DateFormat.format("MMM dd yyyy", mDateAndTime).toString());

                    TextView wakeupActivityText = (TextView) cardView.findViewById(R.id.alarm_wakeup_activity_textview);
                    wakeupActivityText.setText("Wakeup Activity: " + oldAlarmType);

                    TextView ringtoneText = (TextView) cardView.findViewById(R.id.alarm_ringtone_textview);
                    ringtoneText.setText("Ringtone: " + oldAlarmRingtone);

                    //set as not opened, mark that it had NOT been saved
                    mOpenMap.put(alarmId, false);
                    Toast.makeText(mContext, "Changes Not Saved", Toast.LENGTH_SHORT).show();
                }

                //if the user touches the date
                final TextView dateTextView = (TextView) cardView.findViewById(R.id.alarm_date_textview);
                dateTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //open the dialogue to set a new date
                        Calendar time = alarm.getmDateTime();
                        onDateClicked(time, dateTextView);

                        //set the date text to be the new set date
                        String date = android.text.format.DateFormat.format("MMM dd yyyy", mDateAndTime).toString();
                        dateTextView.setText("Date: " + date);
                    }
                });

                //if the user touches the wakeup activity
                final TextView wakeupTextView = (TextView) cardView.findViewById(R.id.alarm_wakeup_activity_textview);
                wakeupTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //open the dialogue to set a new acitivity type
                        int alarmType = alarm.getmAlarmType();
                        onActivityClicked(wakeupTextView, alarmType);

                        //set the wakeup text view to be the new activity text
                        wakeupTextView.setText("Wakeup Activity: " + mActivityType);
                    }
                });

                //if the user touches the ringtone activity
                final TextView ringtoneTextView = (TextView) cardView.findViewById(R.id.alarm_ringtone_textview);
                ringtoneTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //open the dialogue to set a new ringtone type
                        mRingtone = alarm.getmRingToneFile();
                        onRingtoneClicked(ringtoneTextView);

                        //set the wakeup text view to be the new ringtone text
                        ringtoneTextView.setText("Ringtone: " + mRingtoneName);
                    }
                });

                //if the user touches the reminder text
                final TextView reminderTextView = (TextView) cardView.findViewById(R.id.alarm_reminder_textview);
                reminderTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //open the dialogue to set the new reminder
                        onReminderClicked(mReminder);
                        reminderTextView.setText("Reminder");
                    }
                });
            }
        });

        //if user hits the floating aciton button, add a new alarm
        FloatingActionButton fab = (FloatingActionButton) mInflatedView.findViewById(R.id.add_alarm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewAlarm();
            }
        });
        return mInflatedView;
    }

    //update the alarm that has been edited
    private void updateAlarm(Long position) {
        //get the current alarm
        AlarmEntryDbHelper helper = new AlarmEntryDbHelper(mContext);
        Alarm oldAlarm = helper.fetchAlarmByIndex(position);

        //set new alarm to old alarm defaults
        Alarm alarm = new Alarm();
        alarm.setId(oldAlarm.getId());
        alarm.setmDateTime(oldAlarm.getmDateTime());
        alarm.setmActive(oldAlarm.getmActive());
        alarm.setmReminder(oldAlarm.getmReminder());
        alarm.setmRingToneFile(oldAlarm.getmRingToneFile());
        alarm.setmAlarmType(oldAlarm.getmAlarmType());

        //update new alarm with changed variables
        alarm.setmDateTime(mDateAndTime);

        int active;

        //update the data in the alarm scheudler
        //if the alarm is now active and was not previously
        if (isActivated && alarm.getmActive() == 0) {
            //add the new alarm and its data
            AlarmScheduler.deleteAlarm(mContext, (int) alarm.getId());
            AlarmScheduler.setAlarm(mContext, (int) alarm.getId(), alarm.getmDateTime());

            active = 1;
            alarm.setmActive(active);

        } else if (!isActivated && alarm.getmActive() == 1) {
            //if alarm is not active and was previously then delete alarm
            AlarmScheduler.deleteAlarm(mContext, (int) alarm.getId());

            active = 0;
            alarm.setmActive(active);

        } else {
            //otherwise delete and reset alarm to refresh all data of ringtone and activity
            AlarmScheduler.deleteAlarm(mContext, (int) alarm.getId());
            AlarmScheduler.setAlarm(mContext, (int) alarm.getId(), alarm.getmDateTime());

            alarm.setmActive(oldAlarm.getmActive());
        }

        //get the alarm type from the atring
        int alarmType = 0;
        switch (mActivityType) {
            case "Jumping Jacks":
                alarmType = 1;
                break;
            case "Record Yourself":
                alarmType = 2;
                break;
        }

        //set all new data to alarm, update alarm
        alarm.setmAlarmType(alarmType);
        alarm.setmRingToneFile(mRingtone);
        alarm.setmReminder(mReminder);
        alarm.setDefaultIndex(mDefindex);
        helper.updateAlarm(alarm);
    }

    //add a new alarm to the database of alarms
    private void addNewAlarm() {

        // Create dialog to show current time
        TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mDateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mDateAndTime.set(Calendar.MINUTE, minute);

                // grab data and create alarm object to defaults
                Alarm addAlarm = new Alarm();
                Calendar setTime = Calendar.getInstance();
                setTime.setTimeInMillis(System.currentTimeMillis());
                setTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                setTime.set(Calendar.MINUTE, minute);
                addAlarm.setmDateTime(setTime);
                addAlarm.setmReminder("");
                addAlarm.setmActive(1);
                isActivated = true;

                // add alarm to database
                AlarmEntryDbHelper helper = new AlarmEntryDbHelper(mContext);
                int id = (int) helper.insertAlarm(addAlarm);
                helper.close();

                //add alarm to scheduler
                AlarmScheduler.setAlarm(mContext, id, setTime);

                //call fragment to update with new alarms
                FragmentTransaction tr = getFragmentManager().beginTransaction();
                Fragment mAlarmFrag = new AlarmFragment();
                tr.replace(R.id.fragment_holder, mAlarmFrag).commit();
            }
        };

        //open timepicker dialogue for user to chose time
        new TimePickerDialog(mContext, mTimeListener,
                mDateAndTime.get(Calendar.HOUR_OF_DAY),
                mDateAndTime.get(Calendar.MINUTE), true).show();
    }

    //if the user changes the activity type
    private void onActivityClicked(final TextView view, final int oldAlarmType) {

        String[] activityTypes = {"Math Problem", "Jumping Jacks", "Record Yourself"};

        // Define a new adapter
        myAdapter = new ArrayAdapter<String>(mContext,
                R.layout.check_listview_layout, activityTypes);

        //create dialogue with lists of activities
        AlertDialog.Builder mActivityDialog = new AlertDialog.Builder(mContext);
        mActivityDialog.setTitle("WakeUp Activity Type");

        // if cancel set type to old type
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
        //users can only select on of the items
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

        //if save set type to new selected type
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

    //allow users to change the date set
    private void onDateClicked(Calendar time, final TextView tView) {

        final Calendar oldTime = time;

        // Create dialog to show current date
        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                //set the date and time to new date selected
                mDateAndTime.set(Calendar.YEAR, year);
                mDateAndTime.set(Calendar.MONTH, monthOfYear);
                mDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDateAndTime.set(Calendar.HOUR, oldTime.get(Calendar.HOUR));
                mDateAndTime.set(Calendar.MINUTE, oldTime.get(Calendar.MINUTE));
                mDateAndTime.set(Calendar.AM_PM, oldTime.get(Calendar.AM_PM));

                //update the textview and the temp alarm
                String date = android.text.format.DateFormat.format("MMM dd yyyy", mDateAndTime).toString();
                tView.setText("Date: " + date);
            }
        };

        //open datepicker dialogue for the user
        new DatePickerDialog(mContext, mDateListener,
                mDateAndTime.get(Calendar.YEAR),
                mDateAndTime.get(Calendar.MONTH),
                mDateAndTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    //if user changes the ringtone
    private void onRingtoneClicked(final TextView view) {

        currentRingtoneTV = view;

        //inflate dialogue with button for default and custom ringtones
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_twobutton_recording, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(true).setView(dialogView);

        builder.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        dialog.cancel();
                    }
                });

        Button DefaultButton = (Button) dialogView.findViewById(R.id.button_play_dialog);
        Button CustomButton = (Button) dialogView.findViewById(R.id.button_delete_dialog);
        DefaultButton.setText("Default");
        CustomButton.setText("Custom");

        final AlertDialog alert = builder.create();

        //if user choses default button load the default ringtones
        DefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load default ringtone list using the ringtone manager
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Ringtone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                startActivityForResult(intent, 2);
                alert.dismiss();
            }
        });

        //if the user choses custom ringtones load the custom ringtones from the db
        CustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the asyncloader for the ringtone
                loaderManager = getActivity().getLoaderManager();
                loaderManager.initLoader(4, null, recordingLoaderListener).forceLoad();

                mRingtoneName = "Custom";
                updateTextView(mRingtoneName);
                alert.dismiss();
            }
        });

        alert.show();

        view.setText("Ringtone: " + mRingtoneName);
    }

    private void updateTextView(String name) {
        currentRingtoneTV.setText("Ringtone: " + name);
    }


    //if the user updates the reminders
    private void onReminderClicked(String oldReminder) {

        // Create dialog with EditText widget to input reminder
        AlertDialog.Builder mReminderDialog = new AlertDialog.Builder(mContext);
        mReminderDialog.setTitle(R.string.reminder_dialog_title);

        //if the reminder currently is not empty, load the old reminder into the editext
        final EditText inputText = new EditText(mContext);
        if (!mReminder.equals(String.valueOf(""))) {
            inputText.setText(oldReminder);
        }

        inputText.setHint(R.string.reminder_dialog_hint);
        mReminderDialog.setView(inputText);

        mReminderDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });

        //if the user wishes to save edits then save their text to the alarm
        mReminderDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                mReminder = inputText.getText().toString();
                dialog.cancel();
            }
        });

        mReminderDialog.show();
    }
}


