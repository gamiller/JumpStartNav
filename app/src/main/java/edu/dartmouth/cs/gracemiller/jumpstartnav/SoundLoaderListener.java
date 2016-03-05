//package edu.dartmouth.cs.gracemiller.jumpstartnav;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Loader;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//
//import java.util.ArrayList;
//
//import edu.dartmouth.cs.gracemiller.jumpstartnav.Classes.Recording;
//
///**
// * Created by gracemiller on 3/4/16.
// */
//public class SoundLoaderListener implements android.app.LoaderManager.LoaderCallbacks<ArrayList<Recording>> {
//    Context mContext;
//
//    public SoundLoaderListener(Context context){
//        mContext = context;
//    }
//
//
//    @Override
//    public Loader onCreateLoader(int id, Bundle args) {
//        Log.d("onCreateLoader()", "onCreateLoader()");
//
//        // returns an entry loader using context
//        return new RecordingLoader(mContext);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<ArrayList<Recording>> loader, ArrayList<Recording> data) {
//        Log.d("onLoadFinished()", "onLoadFinished()");
//
//
//
//        //sets global variable
//        myRecordings = data;
//
//        if(!data.isEmpty()) {
//            Log.d("onLoadFinished()", "not empty");
//
//            //String[] recordingNames = new String[40];
//            final ArrayList<String> recordingNames = new ArrayList<String>();
//            int i = 0;
//            for (Recording recording : data) {
//                Log.d("in recordings", "recording: " + recording.getAlarmName());
//                //recordingNames.add(recording.getAlarmName());
//                //recordingNames[i] = recording.getAlarmName();
//                //i++;
//                recordingNames.add(recording.getAlarmName());
//                //Log.d("in recordings", "recording: " + recordingNames[i]);
//                Log.d("in recordings", "recording: " + recordingNames.toArray());
//
//
//            }
//
//            //sets adapter to array list of exercises
//
//            // Define a new adapter
//            myAdapter = new ArrayAdapter<String>(mContext,
//                    R.layout.check_listview_layout, recordingNames);
//            Log.d("onLoadFinished()", "got adapter");
//
//            AlertDialog.Builder mRingtoneDialog = new AlertDialog.Builder(mContext);
//            mRingtoneDialog.setTitle(R.string.ringtone_dialog_title);
//            ArrayList<Integer> mSelectedItems = new ArrayList();  // Where we track the selected items
//
//
//            mRingtoneDialog.setNegativeButton("cancel",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            mRingtoneDialog.setSingleChoiceItems(myAdapter, mSoundSelected,
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Log.d("checked the item", "checked: " + which);
//
//                            mSoundSelected = which;
//                        }
//
//                    });
//            mRingtoneDialog.setPositiveButton("save",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            //ARRAY UNCOMMENT
////            mRingtoneDialog.setAdapter(myAdapter,
////                    new DialogInterface.OnClickListener() {
////                        @Override
////                        public void onClick(DialogInterface dialog, int which) {
////                            String alarmName = myAdapter.getItem(which);
////                            Log.d("alarm name onclick", "alarm name is " + alarmName);
////                            Recording recording = myRecordings.get(which);
////
////                            // need to save this to the temporary object
////                            recording.getFileName();
////
//////                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//////                        builder.setMessage(strName);
//////                        builder.setTitle("Your Selected Item is");
//////                        builder.setPositiveButton(
//////                                "Ok",
//////                                new DialogInterface.OnClickListener() {
//////                                    @Override
//////                                    public void onClick(DialogInterface dialog, int which) {
//////                                        dialog.dismiss();
//////                                    }
//////                                });
//////                        builder.show();
////                        }
////                    });
//
//            mRingtoneDialog.show();
//
//
//
//
//
//
//
//            // Assign the adapter to ListView
//            //setListAdapter(mAdapter);
//            //myAdapter = new ExerciseLineArrayAdapter(mContext, data);
//            //mListView.setListAdapter(myAdapter);
//            //mListView.setAdapter(myAdapter);
//            Log.d("onLoadFinished()", "set adapter");
//
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<ArrayList<Recording>> loader) {
//        Log.d("onLoaderReset()", "onLoaderReset()");
//
//        //reloads exercises into adapter
//        myAdapter.clear();
//        myAdapter.notifyDataSetChanged();
//
//
//    }
//    @Override
//    public void onResume() {
//        Log.d("onResume()", "onResume()");
//
//        super.onResume();
//
//        //reloads the list when onResume is called
//        //loaderManager.initLoader(1, null, this).forceLoad();
//    }
//}
