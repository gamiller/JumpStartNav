package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

import android.app.Fragment;
import android.app.FragmentManager;
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
import edu.dartmouth.cs.gracemiller.jumpstartnav.MainActivity;
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

        // grab text views
        dreamContent = (TextView) findViewById(R.id.DreamContent);
        dreamTitle = (TextView) findViewById(R.id.dreamTitleDisplay);

        // grab id
        Intent intent = getIntent();
        mId = intent.getIntExtra("id",0);

        // get dream
        DreamDbHelper helper = new DreamDbHelper(getApplicationContext());
        Dream dream = helper.fetchDreamByIndex((long) mId);
        helper.close();

        // set dream title and dream text
        String finalString = dream.getDreamName() + ":";
        dreamTitle.setText(finalString);
        dreamContent.setText(dream.getDream());



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate delete button
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dream_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // delete dream
            case R.id.delete:
                deleteDream();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // delete dream from db
    private void deleteDream() {

        DreamDbHelper helper = new DreamDbHelper(getApplicationContext());
        helper.removeEntry((long) mId);
        helper.close();

        // ends and returns to main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }


}
