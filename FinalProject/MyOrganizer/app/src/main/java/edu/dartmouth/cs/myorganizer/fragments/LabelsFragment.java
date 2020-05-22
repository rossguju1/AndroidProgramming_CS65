package edu.dartmouth.cs.myorganizer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import edu.dartmouth.cs.myorganizer.MainActivity;
import edu.dartmouth.cs.myorganizer.R;
import edu.dartmouth.cs.myorganizer.adapters.LabelAdapter;
import edu.dartmouth.cs.myorganizer.adapters.PictureAdapter;

public class LabelsFragment extends Fragment {
    ArrayList<String> mLabels;

    public RecyclerView recyclerView;
    public LabelAdapter mAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_labels, container, false);
        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerLabels);
        ArrayList<String> itemsData= new ArrayList<String>();
        itemsData.add("Math");
        itemsData.add("Phyics");
        itemsData.add("Biology");
        itemsData.add("Chemistry");


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new LabelAdapter(getContext(), itemsData);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        return v;
    }
}
