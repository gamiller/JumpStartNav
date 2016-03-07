package edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmControllers;

import android.content.Context;
import android.support.v7.widget.CardView;
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

/*
Adapter to place our cardViews into a listView so the user can interact with them
 */
public class AlarmListAdapter extends ArrayAdapter<CardView> {
    private static final String TAG = "CardArrayAdapter";
    private List<CardView> cardList = new ArrayList<CardView>();
    private ArrayList<Alarm> mDataset;

    public AlarmListAdapter(Context context, int resource, ArrayList<Alarm> myDataset) {
        super(context, resource);
        mDataset = myDataset;
    }

    //allows user to touch the list
    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    //add a new cardView
    @Override
    public void add(CardView object) {
        cardList.add(object);
        super.add(object);
    }

    //number of cards in the view
    @Override
    public int getCount() {
        return this.cardList.size();
    }

    //get the item
    @Override
    public CardView getItem(int index) {
        return this.cardList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        CardViewHolder viewHolder;

        //if no view already
        if (v == null) {
            //inflate the cardview
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.cardview_layout, parent, false);

            //get all components of the cardview to set
            viewHolder = new CardViewHolder();
            viewHolder.mCardView = (CardView) v.findViewById(R.id.cardView);
            viewHolder.mAlarmTime = (TextView) v.findViewById(R.id.alarm_time);
            viewHolder.mActiveSwitch = (Switch) v.findViewById(R.id.alarm_on_switch);
            viewHolder.mAlarmDate = (TextView) v.findViewById(R.id.alarm_date_textview);
            viewHolder.mWakeupActivity = (TextView) v.findViewById(R.id.alarm_wakeup_activity_textview);
            viewHolder.mRingtone = (TextView) v.findViewById(R.id.alarm_ringtone_textview);
            viewHolder.mReminder = (TextView) v.findViewById(R.id.alarm_reminder_textview);

            v.setTag(viewHolder);
        } else {
            viewHolder = (CardViewHolder) v.getTag();
        }

        //set background color of the card
        viewHolder.mCardView.setBackgroundColor(v.getResources().getColor(R.color.colorPrimaryDark));

        Boolean active = true;
        if (mDataset.get(position).getmActive() == 0) {
            active = false;
        }

        //set the default strings for the cards
        //date
        String time = android.text.format.DateFormat.format("hh:mm a",
                mDataset.get(position).getmDateTime()).toString();
        String date = android.text.format.DateFormat.format("MMM dd yyyy",
                mDataset.get(position).getmDateTime()).toString();

        //activity type
        String wakeupActivity = "Jumping Jacks";
        if (mDataset.get(position).getmAlarmType() == 0) {
            wakeupActivity = "Math Problem";
        } else if (mDataset.get(position).getmAlarmType() == 2) {
            wakeupActivity = "Record Yourself";
        }

        //set the switch on or off, and the textviews as above
        viewHolder.mAlarmTime.setText(time);
        viewHolder.mActiveSwitch.setChecked(active);
        viewHolder.mAlarmDate.setText("Date: " + date);
        viewHolder.mWakeupActivity.setText("Wakeup Activity: " + wakeupActivity);
        viewHolder.mRingtone.setText("Ringtone: " + mDataset.get(position).getmRingToneFile());
        viewHolder.mReminder.setText("Reminder");

        return v;
    }

    //structure of the cardviewholder
    static class CardViewHolder {
        public CardView mCardView;
        public TextView mAlarmTime;
        public Switch mActiveSwitch;
        public TextView mAlarmDate;
        public TextView mWakeupActivity;
        public TextView mRingtone;
        public TextView mReminder;
    }
}