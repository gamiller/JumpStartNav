package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import edu.dartmouth.cs.gracemiller.jumpstartnav.Classes.Alarm;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {
    private ArrayList<Alarm> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardView;
        public TextView mAlarmTime;
        public Switch mActiveSwitch;
        public TextView mAlarmDate;
        public TextView mWakeupActivity;
        public TextView mRingtone;
        public TextView mReminder;

        public ViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.cardView);
            mAlarmTime = (TextView) v.findViewById(R.id.alarm_time);
            mActiveSwitch = (Switch) v.findViewById(R.id.alarm_on_switch);
            mAlarmDate = (TextView) v.findViewById(R.id.alarm_date_textview);
            mWakeupActivity = (TextView) v.findViewById(R.id.alarm_wakeup_activity_textview);
            mRingtone = (TextView) v.findViewById(R.id.alarm_ringtone_textview);
            mReminder = (TextView) v.findViewById(R.id.alarm_reminder_textview);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AlarmAdapter(ArrayList<Alarm> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AlarmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters;
        v.findViewById(R.id.cardView).setBackgroundColor(v.getResources().getColor(R.color.colorPrimaryDark));

        Button saveButton = (Button) v.findViewById(R.id.cardSaveButton);
        final CardView cardView = (CardView) v.findViewById(R.id.cardView);
        final LinearLayout expandedView = (LinearLayout) v.findViewById(R.id.expandedView);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setBackgroundColor(v.getResources().getColor(R.color.colorPrimaryDark));
                expandedView.setVisibility(View.GONE);
            }
        });

        Button deleteButton = (Button) v.findViewById(R.id.cardDeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String time = android.text.format.DateFormat.format("hh:mm a",
                mDataset.get(position).getmDateTime()).toString();
        Boolean active = true;
        if (mDataset.get(position).getmActive() == 0) {
            active = false;
        }
        String date = android.text.format.DateFormat.format("MMM dd yyyy",
                mDataset.get(position).getmDateTime()).toString();
        String wakeupActivity = "Jumping Jacks";
        if (mDataset.get(position).getmAlarmType() == 0) {
            wakeupActivity = "Math Problem";
        } else if (mDataset.get(position).getmAlarmType() == 2) {
            wakeupActivity = "Record Yourself";
        }

        holder.mAlarmTime.setText(time);
        holder.mActiveSwitch.setChecked(active);
        holder.mAlarmDate.setText("Date: " + date);
        holder.mWakeupActivity.setText("Wakeup Activity: " + wakeupActivity);
        holder.mRingtone.setText("Ringtone: " + mDataset.get(position).getmRingToneFile());
        holder.mReminder.setText("Reminder");

        holder.mCardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //if (!mOpened) {
                v.setBackgroundColor(v.getResources().getColor(R.color.colorAccent));
                v.findViewById(R.id.expandedView).setVisibility(View.VISIBLE);
                //slide_down(mContext, settingsView);
                //mOpened = true;
                //} else if (mOpened) {
                //v.setBackgroundColor(R.color.colorPrimaryDark);
                //slide_up(mContext, settingsView);
                //v.findViewById(R.id.expandedView).setVisibility(View.GONE);
                //mOpened = false;
                //Toast.makeText(mContext, "Changes Not Saved", Toast.LENGTH_LONG).show();
                //}
            }
        });

        final Switch activeSwitch = holder.mActiveSwitch;
        holder.mActiveSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activeSwitch.setChecked(isChecked);
            }
        });

        final TextView dateText = holder.mAlarmDate;
        holder.mAlarmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // Create dialog to show current date
//                DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
//                    public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                          int dayOfMonth) {
//
//                        mDateAndTime.set(Calendar.YEAR, year);
//                        mDateAndTime.set(Calendar.MONTH, monthOfYear);
//                        mDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                    }
//                };
//
//                new DatePickerDialog(mContext, mDateListener,
//                        mDateAndTime.get(Calendar.YEAR),
//                        mDateAndTime.get(Calendar.MONTH),
//                        mDateAndTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TextView wakeupText = holder.mWakeupActivity;
        holder.mWakeupActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // Create dialog with radio buttons to select wakeup activity
//                final AlertDialog.Builder mActivitySelector = new AlertDialog.Builder(mContext);
//                mActivitySelector.setTitle("Wakeup Activity:");
//                final RadioGroup radioGroup = new RadioGroup(mContext);
//                final RadioButton mathButton = new RadioButton(mContext);
//                mathButton.setText("Math Problem");
//                final RadioButton movementButton = new RadioButton(mContext);
//                movementButton.setText("Jumping Jacks");
//                final RadioButton speechButton = new RadioButton(mContext);
//                speechButton.setText("Record Yourself");
//                radioGroup.addView(mathButton);
//                radioGroup.addView(movementButton);
//                radioGroup.addView(speechButton);
//                mActivitySelector.setView(radioGroup);
//                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(RadioGroup group, int checkedId) {
//                        switch (checkedId){
//                            case 0:
//                                mActivityType = "Math Problem";
//                                break;
//                            case 1:
//                                mActivityType = "Jumping Jacks";
//                                break;
//                            case 2:
//                                mActivityType = "Record Yourself";
//                                break;
//                        }
//                    }
//                });
//                mActivitySelector.show();
            }
        });

        final TextView ringtoneText = holder.mRingtone;
        holder.mRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}