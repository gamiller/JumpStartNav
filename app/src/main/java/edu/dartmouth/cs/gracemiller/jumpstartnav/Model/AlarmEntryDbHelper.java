package edu.dartmouth.cs.gracemiller.jumpstartnav.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Alarm;

//import edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers.AlarmScheduler;

/**
 * Created by TAlbarran on 3/2/16.
 */
public class AlarmEntryDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "alarms.db";
    public static final int VERSION = 1;
    public static final String ENTRIES = "alarms";
    public static final String COL_ID = "_id";
    public static final String COL_TIME = "date_time";
    public static final String COL_ALARMTYPE = "alarm_type";
    public static final String COL_SOUND = "alarm_sound";
    public static final String COL_ACTIVE = "active";
    public static final String COL_REMINDER = "reminder";
    public static final String COL_DEFINDEX = "default_index";

    // SQL query to create the table for the first time
    // Data types are defined below
    public static final String CREATE_DB = "CREATE TABLE IF NOT EXISTS " + ENTRIES + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_TIME + " DATETIME NOT NULL, "
            + COL_ALARMTYPE + " INTEGER NOT NULL, " + COL_SOUND + " TEXT, "
            + COL_ACTIVE + " INTEGER NOT NULL, " + COL_REMINDER + " TEXT, " + COL_DEFINDEX
            + " INTEGER " + ");";

    public Context context;
    public String[] totalColumns = {COL_ID, COL_TIME, COL_ALARMTYPE, COL_SOUND, COL_ACTIVE,
            COL_REMINDER, COL_DEFINDEX};

    // Constructor
    public AlarmEntryDbHelper(Context context) {
        // DATABASE_NAME is, of course the name of the database, which is defined as a tring constant
        // DATABASE_VERSION is the version of database, which is defined as an integer constant
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    // Create table schema if not exists
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_DB);
    }

    // Insert a item given each column value
    public long insertAlarm(Alarm entry) {

        long insertNum;

        //get the dbhelper
        AlarmEntryDbHelper DbHelper = this;
        Alarm alarm = entry;

        //open the database
        SQLiteDatabase database = DbHelper.getWritableDatabase();

        //create a new content value and put all of the information
        //from the exercise into it
        ContentValues cv = new ContentValues();
        //get the time in milliseconds
        cv.put(DbHelper.COL_TIME, alarm.getmDateTime().getTimeInMillis());
        cv.put(DbHelper.COL_ALARMTYPE, alarm.getmAlarmType());
        cv.put(DbHelper.COL_SOUND, alarm.getmRingToneFile());
        cv.put(DbHelper.COL_ACTIVE, alarm.getmActive());
        cv.put(DbHelper.COL_REMINDER, alarm.getmReminder());
        cv.put(DbHelper.COL_DEFINDEX, alarm.getDefaultIndex());

        //insert the cv into the database, and get the number it was
        //inserted at
        insertNum = database.insert(DbHelper.ENTRIES, null, cv);

        Cursor cursor = database.query(DbHelper.ENTRIES, DbHelper.totalColumns, DbHelper.COL_ID
                + " = " + insertNum, null, null, null, null);
        cursor.moveToFirst();

        //close the database
        database.close();

        //return the insertNum
        return insertNum;
    }

    // Remove an entry by giving its index
    public void removeAlarm(long rowIndex) {
        //open the data as writable
        SQLiteDatabase database = this.getWritableDatabase();

        //delete the given row item from the database
        database.delete(this.ENTRIES, this.COL_ID + " = " + rowIndex, null);

        database.close();
    }

    public void updateAlarm(Alarm alarm) {

        //get the dbhelper
        AlarmEntryDbHelper DbHelper = this;
        //Alarm alarm = entry;
        long alarmId = alarm.getId();

        //open the database
        SQLiteDatabase database = DbHelper.getWritableDatabase();

        //create a new content value and put all of the information
        //from the exercise into it
        ContentValues cv = new ContentValues();
        //get the time in milliseconds
        cv.put(DbHelper.COL_TIME, alarm.getmDateTime().getTimeInMillis());
        cv.put(DbHelper.COL_ALARMTYPE, alarm.getmAlarmType());
        cv.put(DbHelper.COL_SOUND, alarm.getmRingToneFile());
        cv.put(DbHelper.COL_ACTIVE, alarm.getmActive());
        cv.put(DbHelper.COL_REMINDER, alarm.getmReminder());
        cv.put(DbHelper.COL_DEFINDEX, alarm.getDefaultIndex());

        database.update(ENTRIES, cv, "_id=" + alarmId, null);
        database.close();
    }

    // Query a specific entry by its index.
    public Alarm fetchAlarmByIndex(long rowId) {

        //open the database
        SQLiteDatabase database = this.getReadableDatabase();

        //query the database for that given entry
        //get cursor to that query
        Cursor cursor = database.query(this.ENTRIES, this.totalColumns, this.COL_ID +
                " = " + rowId, null, null, null, null);
        //move to the first column in that row
        cursor.moveToFirst();

        //create a temporary exercise from the cursor to return
        Alarm tempAlarm = getAlarmFromCursor(cursor);
        database.close();
        return tempAlarm;
    }

    // Query the entire table, return all rows
    public ArrayList<Alarm> fetchAlarms() {

        //get readable database
        SQLiteDatabase database = this.getReadableDatabase();

        ArrayList<Alarm> alarms = new ArrayList<Alarm>();

        //get a cursor for the table
        Cursor cursor = database.query(this.ENTRIES,
                this.totalColumns, null, null, null, null, null);

        //move the cursor over the items starting at the top
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            //for each item create a new exercise
            Alarm tempAlarm = getAlarmFromCursor(cursor);
            alarms.add(tempAlarm);
            cursor.moveToNext();
        }
        // close the cursor and database
        cursor.close();
        database.close();
        return alarms;
    }

    //get a exercise from a cursor
    public Alarm getAlarmFromCursor(Cursor cursor) {

        //create temporary exercise
        Alarm tempAlarm = new Alarm();

        // set all of the data in the exercise
        tempAlarm.setId(cursor.getLong(cursor.getColumnIndex(COL_ID)));
        tempAlarm.setmDateTime(getDate(cursor.getLong(cursor.getColumnIndex(COL_TIME))));
        tempAlarm.setmAlarmType(cursor.getInt(cursor.getColumnIndex(COL_ALARMTYPE)));
        tempAlarm.setmRingToneFile(cursor.getString(cursor.getColumnIndex(COL_SOUND)));
        tempAlarm.setmActive(cursor.getInt(cursor.getColumnIndex(COL_ACTIVE)));
        tempAlarm.setmReminder(cursor.getString(cursor.getColumnIndex(COL_REMINDER)));
        tempAlarm.setDefaultIndex(cursor.getInt(cursor.getColumnIndex(COL_DEFINDEX)));

        return tempAlarm;
    }

    //get a calender instance from milliseconds
    //used with help from http://stackoverflow.com/questions/18929929/convert-timestamp-into-current-date-in-android
    private Calendar getDate(long time) {
        //get an instance
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //set the calender
        cal.setTimeInMillis(time);
        return cal;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int pVersion, int nVersion) {
    }
}

//insert your task into the database
class insertAlarmTask extends AsyncTask<sqlObjectAlarm, Void, Void> {

    AlarmEntryDbHelper helper;
    private long insertNum;
    private Context context;

    //construct the async task
    public insertAlarmTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(sqlObjectAlarm... params) {
        //get the vairables from the object
        helper = params[0].helper;
        Alarm alarm = params[0].alarm;

        //call insert entry from the helper
        insertNum = helper.insertAlarm(alarm);
        //AlarmScheduler.setAlarm(context,(int)insertNum,alarm.getmDateTime());

        return null;
    }


    @Override
    protected void onPostExecute(Void unused) {
        //toast out which entry was created
        Toast.makeText(context, "Entry #" + insertNum + "saved.", Toast.LENGTH_SHORT).show();
    }
}


//sqlObject which can be passed into the asynctasks
//bundles up the context, exercise, and dbhelper
class sqlObjectAlarm {

    AlarmEntryDbHelper helper;
    Alarm alarm;
    Context context;

    sqlObjectAlarm(AlarmEntryDbHelper helper, Alarm alarm, Context context) {
        this.helper = helper;
        this.alarm = alarm;
        this.context = context;
    }
}