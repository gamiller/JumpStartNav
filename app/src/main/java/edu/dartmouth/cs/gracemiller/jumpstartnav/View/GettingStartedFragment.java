package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.dartmouth.cs.gracemiller.jumpstartnav.R;

/*
fragment loads the gettingstarted tab which is instructions on how to use the app
 */
public class GettingStartedFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_getting_started, container, false);
    }
}
