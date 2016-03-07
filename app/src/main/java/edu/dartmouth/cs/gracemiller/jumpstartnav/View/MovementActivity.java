package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers.AlarmPlayer;
import edu.dartmouth.cs.gracemiller.jumpstartnav.R;

public class MovementActivity extends AppCompatActivity {
    private Context mContext;

    private int mId;
    private AlarmPlayer player;

    private SensorsService mSensorService;
    private SensorsService.SensorBinder mBinder;
    private Boolean mBound;

    // connection between service and activity
    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (SensorsService.SensorBinder) service;
            mSensorService = mBinder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mSensorService = null;
        }
    };

    private SensorsServiceReceiver broadcastRx;
    private TextView mMotivateText;
    private TextView mTimerText;

    @Override
    public void onBackPressed() {
        if (mBound) {
            // release resources
            unregisterReceiver(broadcastRx);
            unbindService(mConnection);
            mBound = false;
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement);

        mContext = this;

        Intent recvIntent = getIntent();
        mId = recvIntent.getIntExtra("id", 0);

        player = new AlarmPlayer(this, mId);
//        player.startSound();

        mMotivateText = (TextView) findViewById(R.id.movementText);
        mTimerText = (TextView) findViewById(R.id.timerText);

        //start the service
        Intent i = new Intent(this, SensorsService.class);
        startService(i);

        // bind to service
        Intent intent = new Intent(this, SensorsService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;

        // set up receiver
        IntentFilter iF = new IntentFilter();
        broadcastRx = new SensorsServiceReceiver();
        iF.addAction("edu.dartmouth.cs.gracemiller.jumpstart.MOVEMENT_CHANGE");
        registerReceiver(broadcastRx, iF);
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // unreg receiver and unbind service to free up resources
            unregisterReceiver(broadcastRx);
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            Intent i = new Intent(this, SensorsService.class);
            stopService(i);
        }
        super.onDestroy();
    }

    // broadcast receiver to update when new location info comes in
    public class SensorsServiceReceiver extends BroadcastReceiver {
        boolean isRunning = false;

        CountDownTimer movementTimer = new CountDownTimer(10000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                mTimerText.setText(String.valueOf((millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(),
                        "Done!", Toast.LENGTH_SHORT).show();
                player.stopSound();
                Intent i = new Intent(mContext, AlarmReminderViewActivity.class);
                i.putExtra("id", (long) mId);
                startActivity(i);
            }
        };

        @Override
        public void onReceive(Context context, Intent intent) {
            int movementType = mBinder.getMovement();

            if (movementType == 0) { // sleeping
                movementTimer.cancel();

                if (isRunning) {
                    Toast.makeText(getApplicationContext(),
                            "Don't give up!", Toast.LENGTH_SHORT).show();
                }

                isRunning = false;
                mMotivateText.setText(R.string.start_moving_text);
                mTimerText.setText(R.string.get_jumpstart_day_text);

            } else if (movementType == 1) { // moving
                mMotivateText.setText(R.string.keep_moving_text);
                movementTimer.cancel();

                if (!isRunning) {
                    movementTimer.start();
                    isRunning = true;
                }

            } else {
                mMotivateText.setText("Error");
            }
        }
    }
}
