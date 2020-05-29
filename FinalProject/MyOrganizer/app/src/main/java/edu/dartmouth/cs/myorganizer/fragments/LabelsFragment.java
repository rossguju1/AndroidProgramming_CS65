package edu.dartmouth.cs.myorganizer.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
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

    private static final String DEBUG = "LabelsFragment";
    ArrayList<String> mLabels;
    public SharedPreferences sharedPreferences;
    public RecyclerView recyclerView;
    public LabelAdapter mAdapter;
    private ArrayList<String> itemsData;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_labels, container, false);
        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerLabels);
        itemsData= new ArrayList<String>();
        itemsData.add("Math");
        itemsData.add("Phyics");
        itemsData.add("Biology");
        itemsData.add("Chemistry");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (LoadState() ==1){

            mAdapter = new LabelAdapter(getContext(), itemsData);
            recyclerView.setAdapter(mAdapter);
        }


        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_labels, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if (id == R.id.action_syncro) {

         //  recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
           mAdapter = new LabelAdapter(getContext(), itemsData);
           //recyclerView.setItemAnimator(new DefaultItemAnimator());

           recyclerView.setAdapter(mAdapter);


          SaveState(1);


           return true;
       }


        return super.onOptionsItemSelected(item);
    }

    public void SaveState(int value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("organized", value);
        editor.commit();
    }
    public int LoadState(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int organized = sharedPreferences.getInt("organized", -1);
        return organized;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(DEBUG, "onResume");

    }

}
