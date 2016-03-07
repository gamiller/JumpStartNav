package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Dream;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.DreamDbHelper;
import edu.dartmouth.cs.gracemiller.jumpstartnav.R;

public class DisplayDreamActivity extends AppCompatActivity {

    private int mId;
    TextView dreamContent;
    TextView dreamTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_dream);

        dreamContent = (TextView) findViewById(R.id.DreamContent);
        dreamTitle = (TextView) findViewById(R.id.dreamTitleDisplay);

        Intent intent = getIntent();
        mId = intent.getIntExtra("id",0);

        Log.d("receive", "received id is " + mId);

        DreamDbHelper helper = new DreamDbHelper(getApplicationContext());
        Dream dream = helper.fetchDreamByIndex((long) mId);
        helper.close();

//        Calendar cal = dream.getDate();
//        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
//        String finalString = formatDate.format(cal.getTime()) + " " + dream.getDreamName();
        String finalString = dream.getDreamName() + ":";


//        String titleString = "" + dream.getDreamName()+ ": "+ dream.getDate();

        dreamTitle.setText(finalString);
        dreamContent.setText(dream.getDream());



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dream_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete:
                deleteDream();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteDream() {

        DreamDbHelper helper = new DreamDbHelper(getApplicationContext());
        helper.removeEntry((long) mId);
        helper.close();

        finish();

    }


}
