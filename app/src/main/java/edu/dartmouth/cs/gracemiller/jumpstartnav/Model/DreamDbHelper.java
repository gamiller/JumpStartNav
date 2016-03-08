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

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Dream;
import edu.dartmouth.cs.gracemiller.jumpstartnav.View.DreamActivity;

/**
 * Created by TAlbarran on 1/30/16.
 */
public class DreamDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "dreams.db";
    public static final int VERSION = 1;
    public static final String ENTRIES = "dreams";
    public static final String COL_ID = "_id";
    public static final String COL_DREAM = "dream";
    public static final String COL_DATE = "date";
    public static final String COL_NAME = "name";
    // SQL query to create the table for the first time
    // Data types are defined below
    public static final String CREATE_DB = "CREATE TABLE IF NOT EXISTS " + ENTRIES + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_DATE + " DATETIME NOT NULL, "
            + COL_NAME + " TEXT, " + COL_DREAM + " TEXT " + ");";
    public Context context;
    public String[] totalColumns = {COL_ID, COL_DATE, COL_NAME, COL_DREAM};

    // Constructor
    public DreamDbHelper(Context context) {
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
    public long insertDream(Dream entry) {

        long insertNum;

        //get the dbhelper
        DreamDbHelper DbHelper = this;
        Dream dream = entry;

        //open the database
        SQLiteDatabase database = DbHelper.getWritableDatabase();

        //create a new content value and put all of the information
        //from the dream into it
        ContentValues cv = new ContentValues();
        //get the time in milliseconds
        cv.put(DbHelper.COL_DREAM, dream.getDream());
        cv.put(DbHelper.COL_DATE, dream.getDate().getTimeInMillis());
        cv.put(DbHelper.COL_NAME, dream.getDreamName());

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
    public void removeEntry(long rowIndex) {
        //open the data as writable
        SQLiteDatabase database = this.getWritableDatabase();

        //delete the given row item from the database
        database.delete(this.ENTRIES, this.COL_ID
                + " = " + rowIndex, null);

        database.close();

    }

    // Query a specific entry by its index.
    public Dream fetchDreamByIndex(long rowId) {

        //open the database
        SQLiteDatabase database = this.getReadableDatabase();

        //query the database for that given entry
        //get cursor to that query
        Cursor cursor = database.query(this.ENTRIES, this.totalColumns, this.COL_ID +
                " = " + rowId, null, null, null, null);
        //move to the first column in that row
        cursor.moveToFirst();

        //create a temporary dream from the cursor to return
        Dream tempDream = getDreamFromCursor(cursor);
        database.close();
        return tempDream;

    }

    // Query the entire table, return all rows
    public ArrayList<Dream> fetchDreams() {
        //get readable database
        SQLiteDatabase database = this.getReadableDatabase();

        ArrayList<Dream> dreams = new ArrayList<Dream>();

        //get a cursor for the table
        Cursor cursor = database.query(this.ENTRIES,
                this.totalColumns, null, null, null, null, null);

        //move the cursor over the items starting at the top
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            //for each item create a new dream
            Dream tempDream = getDreamFromCursor(cursor);
            dreams.add(tempDream);
            cursor.moveToNext();
        }
        // close the cursor and database
        cursor.close();
        database.close();
        return dreams;

    }

    //get a dream from a cursor
    public Dream getDreamFromCursor(Cursor cursor) {
        //create temporary dream
        Dream tempDream = new Dream();

        // set all of the data in the dream
        tempDream.setId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
        tempDream.setDream(cursor.getString(cursor.getColumnIndex(COL_DREAM)));
        tempDream.setDate(getDate(cursor.getLong(cursor.getColumnIndex(COL_DATE))));
        tempDream.setDreamName(cursor.getString(cursor.getColumnIndex(COL_NAME)));

        return tempDream;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int pVersion, int nVersion) {
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
}

//insert your task into the database
class insertDream extends AsyncTask<sqlObjectDream, Void, Void> {
    DreamDbHelper helper;
    private long insertNum;
    private Context context;

    //construct the async task
    public insertDream(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(sqlObjectDream... params) {
        //get the vairables from the object
        helper = params[0].helper;
        Dream dream = params[0].dream;

        //call insert entry from the helper
        insertNum = helper.insertDream(dream);

        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        //toast out which entry was created
        Toast.makeText(context, "Entry #" + insertNum + "saved.", Toast.LENGTH_SHORT).show();

        //close the manual entry activity
        if (context.equals(DreamActivity.mContext)) {
            ((DreamActivity) context).finish();
        } else {
            ((DreamActivity) context).finish();
        }
    }
}

//edu.dartmouth.cs.gracemiller.jumpstartnav.Model.sqlObject which can be passed into the asynctasks
//bundles up the context, dream, and dbhelper
class sqlObjectDream {
    DreamDbHelper helper;
    Dream dream;
    Context context;

    sqlObjectDream(DreamDbHelper helper, Dream dream, Context context) {
        this.helper = helper;
        this.dream = dream;
        this.context = context;
    }
}


