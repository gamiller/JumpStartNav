package edu.dartmouth.cs.gracemiller.jumpstartnav;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //AlarmFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmFragment extends Fragment {


//    private static final String TAG = AlarmFragment.class.getSimpleName();
//
//    /**
//     * The CardView widget.
//     */
//    //@VisibleForTesting
//    CardView mCardView;
//
//    /**
//     * SeekBar that changes the cornerRadius attribute for the {@link #mCardView} widget.
//     */
//    //@VisibleForTesting
//    SeekBar mRadiusSeekBar;
//
//    /**
//     * SeekBar that changes the Elevation attribute for the {@link #mCardView} widget.
//     */
//    //@VisibleForTesting
//    SeekBar mElevationSeekBar;

    private Context mContext;
    private Boolean mOpened = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificationFragment.
     */
    public static AlarmFragment newInstance() {
        AlarmFragment fragment = new AlarmFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout cardList = (LinearLayout) view.findViewById(R.id.cardContainer);

        final CardView alarmView = (CardView) view.findViewById(R.id.cardview1);
        final LinearLayout settingsView = (LinearLayout) view.findViewById(R.id.expandedView);
        alarmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                settingsView.setVisibility(View.VISIBLE);
            }
        });

        Button saveButton = (Button) view.findViewById(R.id.cardSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                settingsView.setVisibility(View.GONE);
            }
        });

//        CardView primary = (CardView) view.findViewById(R.id.cardview1);
//        final CardView dropDown = (CardView) view.findViewById(R.id.cardview2);
//        dropDown.setVisibility(View.GONE);
//
//        primary.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dropDown.setVisibility(View.VISIBLE);
//                slide_down(mContext, v);
//            }
//        });
//
//        dropDown.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                slide_up(mContext, v);
//                dropDown.setVisibility(View.GONE);
//            }
//        });
    }

    public static void slide_down(Context context, View view) {
        Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        if(a != null){
            a.reset();
            if(view != null){
                view.clearAnimation();
                view.startAnimation(a);
            }
        }
    }

    public static void slide_up(Context context, View view) {
        Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        if(a != null){
            a.reset();
            if(view != null){
                view.clearAnimation();
                view.startAnimation(a);
            }
        }
    }


















//    private final String[] ALARM_SAMPLE_GREEN = {"7:00  Monday", "9:30  Tuesday"};
//    private final String[] ALARM_SAMPLE_RED = {"7:00  Wednesday", "8:00  Thursday", "6:30  Friday", "11:00  Saturday"};
//    private final String[] ALARM_SAMPLE = {"7:00  Monday", "9:30  Tuesday", "7:00  Wednesday", "8:00  Thursday", "6:30  Friday", "11:00  Saturday"};
//
//    private ArrayList<String> ALARM_SAMPLE_ARRAY = new ArrayList<>(7);
//
//    private View mInflatedView;
//    private Context mContext;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View mInflatedView = inflater.inflate(R.layout.fragment_alarm, container, false);
//
//        mContext = getActivity();
//
//        ALARM_SAMPLE_ARRAY.add("7:00  Monday");
//        ALARM_SAMPLE_ARRAY.add("9:30  Tuesday");
//        ALARM_SAMPLE_ARRAY.add("7:00  Wednesday");
//        ALARM_SAMPLE_ARRAY.add("8:00  Thursday");
//        ALARM_SAMPLE_ARRAY.add("6:30  Friday");
//        ALARM_SAMPLE_ARRAY.add("13:00  Saturday");
//
//        ListView listView = (ListView) mInflatedView.findViewById(R.id.alarm_listview);
//        listView.setAdapter(new Adapter1(getActivity(), R.layout.custom_green_textview, ALARM_SAMPLE_ARRAY));
//
//        return mInflatedView;
//    }
//
//    public class Adapter1 extends ArrayAdapter<String> {
//
//        public Adapter1(Context context, int resID, ArrayList<String> items) {
//            super(context, resID, items);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View v = super.getView(position, convertView, parent);
//            if (position == 0 || position == 1) {
//                ((TextView) v).setTextColor(Color.GREEN);
//            } else {
//                ((TextView) v).setTextColor(Color.RED);
//            }
//            return v;
//        }
//
//    }
//
////    public class CustomAlarmAdapter extends BaseAdapter {
////
////        @Override
////        public int getCount() {
////            return 0;
////        }
////
////        @Override
////        public Object getItem(int position) {
////            return null;
////        }
////
////        @Override
////        public long getItemId(int position) {
////            return 0;
////        }
////
////        @Override
////        public View getView(int position, View convertView, ViewGroup parent) {
////            View superView = super.getView(position, convertView, parent);
////        }
////    }
}
