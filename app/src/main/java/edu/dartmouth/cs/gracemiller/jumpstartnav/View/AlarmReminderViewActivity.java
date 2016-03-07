package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Alarm;
import edu.dartmouth.cs.gracemiller.jumpstartnav.MainActivity;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.AlarmEntryDbHelper;
import edu.dartmouth.cs.gracemiller.jumpstartnav.R;

public class AlarmReminderViewActivity extends AppCompatActivity {
    TextView reminderText;
    Button okButton;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_reminder_view);

        mContext = this;
        AlarmEntryDbHelper helper = new AlarmEntryDbHelper(this);

        long alarmId = getIntent().getLongExtra("id", -1);

        Alarm alarm = helper.fetchAlarmByIndex(alarmId);

        String reminder = alarm.getmReminder();
        if (alarm.getmReminder().equals("")) {
            reminder = "No Reminders";
        }

        reminderText = (TextView) findViewById(R.id.textView_remindersDay);
        reminderText.setText(reminder);

        okButton = (Button) findViewById(R.id.button_dayReminder);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
