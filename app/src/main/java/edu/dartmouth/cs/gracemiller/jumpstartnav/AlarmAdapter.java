package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

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
        // set the view's size, margins, paddings and layout parameters

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
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}