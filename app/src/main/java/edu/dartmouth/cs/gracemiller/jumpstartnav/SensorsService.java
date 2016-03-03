package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.meapsoft.FFT;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by gbzales on 3/2/16.
 */
public class SensorsService extends Service implements SensorEventListener {
    public static final int ACCELEROMETER_BUFFER_CAPACITY = 2048;
    public static final int ACCELEROMETER_BLOCK_CAPACITY = 64;
    private static final int mFeatLen = ACCELEROMETER_BLOCK_CAPACITY + 2;

    private SensorManager mSensorMan;
    private ReadFromQueueTask mReadFromQueueTask;
    private IBinder mBinder = new SensorBinder();

    private static ArrayBlockingQueue<Double> mAccBuffer;

    private int mMovementType;
    private int mLastMovement = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        mAccBuffer = new ArrayBlockingQueue<Double>(ACCELEROMETER_BUFFER_CAPACITY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String otherService = Context.SENSOR_SERVICE;
        mSensorMan = (SensorManager)getSystemService(otherService);

        // get device sensor
        mSensorMan.registerListener(this, mSensorMan.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_GAME);

        // async task to pull from buffered list of readings
        // following code modified from stackoverflow - honeycomb update issue
        mReadFromQueueTask = new ReadFromQueueTask();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            mReadFromQueueTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        }
        else{
            mReadFromQueueTask.execute((Void[])null);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mSensorMan.unregisterListener(this);
        mReadFromQueueTask.cancel(true);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // make buffer of sensor data and add new data
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            double m = Math.sqrt(event.values[0] * event.values[0]
                    + event.values[1] * event.values[1] + event.values[2]
                    * event.values[2]);

            // Inserts the specified element into this queue if it is possible
            // to do so immediately without violating capacity restrictions,
            // returning true upon success and throwing an IllegalStateException
            // if no space is currently available. When using a
            // capacity-restricted queue, it is generally preferable to use
            // offer.

            try {
                mAccBuffer.add(Double.valueOf(m));
            } catch (IllegalStateException e) {

                // Exception happens when reach the capacity.
                // Doubling the buffer. ListBlockingQueue has no such issue,
                // But generally has worse performance
                ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<Double>(
                        mAccBuffer.size() * 2);

                mAccBuffer.drainTo(newBuf);
                mAccBuffer = newBuf;
                mAccBuffer.add(Double.valueOf(m));

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // AsyncTask to read values from queue
    // modified from MyRunsDataCollector
    private class ReadFromQueueTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // make feature vector of features
            ArrayList<Double> featureVector = new ArrayList<>(ACCELEROMETER_BLOCK_CAPACITY + 1);
            int blockSize = 0;
            FFT fft = new FFT(ACCELEROMETER_BLOCK_CAPACITY);
            double[] accBlock = new double[ACCELEROMETER_BLOCK_CAPACITY];
            double[] re = accBlock;
            double[] im = new double[ACCELEROMETER_BLOCK_CAPACITY];

            double max = Double.MIN_VALUE;

            while (!isCancelled()) {
                try {
                    // need to check if the AsyncTask is cancelled or not in the while loop

                    // Dumping buffer
                    accBlock[blockSize++] = mAccBuffer.take().doubleValue();

                    if (blockSize == ACCELEROMETER_BLOCK_CAPACITY) {
                        blockSize = 0;

                        // time = System.currentTimeMillis();
                        max = .0;
                        for (double val : accBlock) {
                            if (max < val) {
                                max = val;
                            }
                        }

                        fft.fft(re, im);

                        for (int i = 0; i < re.length; i++) {
                            double mag = Math.sqrt(re[i] * re[i] + im[i]
                                    * im[i]);
                            featureVector.add(Double.valueOf(mag));
                            im[i] = .0; // Clear the field
                        }

                        // Append max after frequency component
                        int value = (int) WekaClassifier.classify(featureVector
                                .toArray());
                        featureVector.clear();

                        if(mLastMovement == -1) {
                            mLastMovement = value;
                            Intent i = new Intent();
                            i.setAction("edu.dartmouth.cs.gracemiller.jumpstart.MOVEMENT_CHANGE");
                            sendBroadcast(i);       // inform MovementActivity of updated movement type
                        }

                        mMovementType = value;

                        // send broadcast if movement type changes
                        if (mLastMovement != mMovementType) {
                            Intent i = new Intent();
                            i.setAction("edu.dartmouth.cs.gracemiller.jumpstart.MOVEMENT_CHANGE");
                            sendBroadcast(i);       // inform MovementActivity of updated movement type
                            mLastMovement = mMovementType;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public class SensorBinder extends Binder {

        SensorsService getService() {
            return SensorsService.this;
        }

        int getMovement() {
            return mMovementType;
        }
    }
}
