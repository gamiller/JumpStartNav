package edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmControllers;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Alarm;
import edu.dartmouth.cs.gracemiller.jumpstartnav.R;

public class AlarmListAdapter  extends ArrayAdapter<CardView> {
    private static final String TAG = "CardArrayAdapter";
    private List<CardView> cardList = new ArrayList<CardView>();
    private ArrayList<Alarm> mDataset;

    public AlarmListAdapter(Context context, int resource,  ArrayList<Alarm> myDataset) {
        super(context, resource);
        mDataset = myDataset;

    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    static class CardViewHolder {
        public CardView mCardView;
        public TextView mAlarmTime;
        public Switch mActiveSwitch;
        public TextView mAlarmDate;
        public TextView mWakeupActivity;
        public TextView mRingtone;
        public TextView mReminder;
    }

    @Override
    public void add(CardView object)
    {
        cardList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.cardList.size();
    }

    @Override
    public CardView getItem(int index) {
        return this.cardList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("AlarmListAdapter", "getView()");
        View v = convertView;
        CardViewHolder viewHolder;
        if (v == null) {
            Log.d("getView", "v is null");

            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.cardview_layout, parent, false);
            viewHolder = new CardViewHolder();

//            viewHolder.line1 = (TextView) row.findViewById(R.id.line1);
//            viewHolder.line2 = (TextView) row.findViewById(R.id.line2);
            viewHolder.mCardView = (CardView) v.findViewById(R.id.cardView);
            viewHolder.mAlarmTime = (TextView) v.findViewById(R.id.alarm_time);
            viewHolder.mActiveSwitch = (Switch) v.findViewById(R.id.alarm_on_switch);
            viewHolder.mAlarmDate = (TextView) v.findViewById(R.id.alarm_date_textview);
            viewHolder.mWakeupActivity = (TextView) v.findViewById(R.id.alarm_wakeup_activity_textview);
            viewHolder.mRingtone = (TextView) v.findViewById(R.id.alarm_ringtone_textview);
            viewHolder.mReminder = (TextView) v.findViewById(R.id.alarm_reminder_textview);
            v.setTag(viewHolder);
        } else {
            viewHolder = (CardViewHolder)v.getTag();
        }

        viewHolder.mCardView.setBackgroundColor(v.getResources().getColor(R.color.colorPrimaryDark));

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

        viewHolder.mAlarmTime.setText(time);
        viewHolder.mActiveSwitch.setChecked(active);
        viewHolder.mAlarmDate.setText("Date: " + date);
        viewHolder.mWakeupActivity.setText("Wakeup Activity: " + wakeupActivity);
        viewHolder.mRingtone.setText("Ringtone: " + mDataset.get(position).getmRingToneFile());
        viewHolder.mReminder.setText("Reminder");
       // card.setBackgroundColor(v.getResources().getColor(R.color.colorPrimaryDark));
//        viewHolder.mActiveSwitch.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(parent.getContext(), "yuh that worked", Toast.LENGTH_SHORT).show();
//            }
//        });

            return v;
    }


}