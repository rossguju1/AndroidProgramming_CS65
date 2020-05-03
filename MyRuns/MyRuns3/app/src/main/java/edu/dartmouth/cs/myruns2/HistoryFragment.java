package edu.dartmouth.cs.myruns2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.dartmouth.cs.myruns2.models.Exercise;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private static final String FROM_HISTORY_TAB = "history_tab";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.history_fragment, container, false);
        //setContentView(R.layout.history_fragment);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ExerciseEntry ex = new ExerciseEntry(getContext());
        ex.open();
        ArrayList<Exercise> itemsData = ex.getAllExercises();

        for (int position = 0; position < itemsData.size(); position++){
            Exercise e = itemsData.get(position);
            Log.d("h-Frag: ID:  ", "ID:  " + e.getId());
            Log.d("h-Frag: INPUT:  ", "INPUT :  " + e.getmInputType());
            Log.d("h-Frag: ACTIVITY:  ", "ACT :  " + e.getmActivityType());

        }
        ex.close();

        HistoryAdapterRecycler mAdapter = new HistoryAdapterRecycler(getContext(), itemsData);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return v;
    }


}
