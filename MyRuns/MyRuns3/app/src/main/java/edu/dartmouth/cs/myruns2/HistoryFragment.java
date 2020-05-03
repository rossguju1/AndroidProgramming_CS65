package edu.dartmouth.cs.myruns2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class HistoryFragment extends Fragment {

    private static final String FROM_HISTORY_TAB = "history_tab";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.history_fragment, container, false);
        return v;
    }


}

