package edu.dartmouth.cs.gracemiller.jumpstartnav.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Recording;

/**
 * Recording DatabaseHelper to deal with storing all recordings created by the user
 */
public class RecordingEntryDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "recordings.db";
    public static final int VERSION = 1;
    public static final String ENTRIES = "alarms";
    public static final String COL_ID = "_id";
    public static final String COL_FILE = "file_name";
    public static final String COL_TITLE = "recording_title";

    // create table which holds the file string and the title of the recording
    public static final String CREATE_DB = "CREATE TABLE IF NOT EXISTS " + ENTRIES + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_FILE + " TEXT NOT NULL, "
            + COL_TITLE + " TEXT NOT NULL" + ");";

    public Context context;
    public String[] totalColumns = {COL_ID, COL_FILE, COL_TITLE};

    // Constructor for the database
    public RecordingEntryDbHelper(Context context) {
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
    public long insertRecording(Recording entry) {

        long insertNum;

        //get the dbhelper
        RecordingEntryDbHelper DbHelper = this;
        Recording recording = entry;

        //open the database
        SQLiteDatabase database = DbHelper.getWritableDatabase();

        //create a new content value and put all of the information
        //from the exercise into it
        ContentValues cv = new ContentValues();
        //get the time in milliseconds
        cv.put(DbHelper.COL_FILE, recording.getFileName());
        cv.put(DbHelper.COL_TITLE, recording.getAlarmName());

        //insert the cv into the database, and get the number it was
        //inserted at
        insertNum = database.insert(DbHelper.ENTRIES, null, cv);

        Cursor cursor = database.query(DbHelper.ENTRIES,
                DbHelper.totalColumns, DbHelper.COL_ID + " = " + insertNum, null,
                null, null, null);
        cursor.moveToFirst();

        //close the database
        database.close();

        //return the insertNum
        return insertNum;
    }

    // Remove an entry by giving its index
    public void removeRecording(long rowIndex) {
        //open the data as writable
        SQLiteDatabase database = this.getWritableDatabase();

        //delete the given row item from the database
        database.delete(this.ENTRIES, this.COL_ID
                + " = " + rowIndex, null);

        database.close();
    }

    // Query a specific entry by its index.
    public Recording fetchRecordingByIndex(long rowId) {

        //open the database
        SQLiteDatabase database = this.getReadableDatabase();

        //query the database for that given entry
        //get cursor to that query
        Cursor cursor = database.query(this.ENTRIES, this.totalColumns, this.COL_ID +
                " = " + rowId, null, null, null, null);
        //move to the first column in that row
        cursor.moveToFirst();

        //create a temporary exercise from the cursor to return
        Recording tempRecording = getRecordingFromCursor(cursor);
        database.close();
        return tempRecording;
    }

    // Query the entire table, return all rows
    public ArrayList<Recording> fetchRecordings() {
        //get readable database
        SQLiteDatabase database = this.getReadableDatabase();

        ArrayList<Recording> alarms = new ArrayList<Recording>();

        //get a cursor for the table
        Cursor cursor = database.query(this.ENTRIES,
                this.totalColumns, null, null, null, null, null);

        //move the cursor over the items starting at the top
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            //for each item create a new recording
            Recording tempRecording = getRecordingFromCursor(cursor);
            alarms.add(tempRecording);
            cursor.moveToNext();
        }
        // close the cursor and database
        cursor.close();
        database.close();
        return alarms;
    }

    //get a recording from a cursor
    public Recording getRecordingFromCursor(Cursor cursor) {
        //create temporary recording
        Recording tempRecording = new Recording();

        // set all of the data in the exercise
        tempRecording.setId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
        tempRecording.setAlarmName(cursor.getString(cursor.getColumnIndex(COL_TITLE)));
        tempRecording.setFileName(cursor.getString(cursor.getColumnIndex(COL_FILE)));

        return tempRecording;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int pVersion, int nVersion) {
    }
}

//insert your recording into the database
class insertRecording extends AsyncTask<sqlObject, Void, Void> {
    RecordingEntryDbHelper helper;
    private long insertNum;
    private Context context;

    //construct the async task
    public insertRecording(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(sqlObject... params) {
        //get the vairables from the object
        helper = params[0].helper;
        Recording recording = params[0].recording;

        //call insert entry from the helper
        insertNum = helper.insertRecording(recording);

        return null;
    }


    @Override
    protected void onPostExecute(Void unused) {
        //toast out which entry was created
        Toast.makeText(context, "Entry #" + insertNum + "saved.", Toast.LENGTH_SHORT).show();
    }
}


//edu.dartmouth.cs.gracemiller.jumpstartnav.Model.sqlObject which can be passed into the asynctasks
//bundles up the context, exercise, and dbhelper
class sqlObject {
    RecordingEntryDbHelper helper;
    Recording recording;
    Context context;

    sqlObject(RecordingEntryDbHelper helper, Recording recording, Context context) {
        this.helper = helper;
        this.recording = recording;
        this.context = context;
    }
}


