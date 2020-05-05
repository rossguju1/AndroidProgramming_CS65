package edu.dartmouth.cs.myruns2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.dartmouth.cs.myruns2.models.Exercise;

public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Exercise>> {
    private static final String DEBUG_TAG = "HistoryFragment";
    private static final int ALL_COMMENTS_LOADER_ID = 1;
    public static ArrayList<Exercise> itemsData = new  ArrayList<Exercise>();
    //public static AlertDialog.Builder recyclerView;

    public static RecyclerView recyclerView;
    //private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    //ArrayList<Exercise> itemsData;
    public static HistoryAdapterRecycler mAdapter;
    public ExerciseEntry ex;
    private static final String FROM_HISTORY_TAB = "history_tab";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "onCreateView()");

        View v = inflater.inflate(R.layout.history_fragment, container, false);
        //setContentView(R.layout.history_fragment);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new HistoryAdapterRecycler(getContext(), itemsData);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

       // mAdapter = new HistoryAdapterRecycler(getContext(), itemsData);


       // ExerciseEntry ex = new ExerciseEntry(getContext());
//        ex.open();
//        //itemsData = ex.getAllExercises();
//
//        for (int position = 0; position < itemsData.size(); position++) {
//            Exercise e = itemsData.get(position);
//            Log.d("h-Frag: ID:  ", "ID:  " + e.getId());
//            Log.d("h-Frag: INPUT:  ", "INPUT :  " + e.getmInputType());
//            Log.d("h-Frag: ACTIVITY:  ", "ACT :  " + e.getmActivityType());
//
//        }
//        ex.close();

//       //
//        try{
//        if (mAdapter.getItemCount() == 0){
//            Log.d(DEBUG_TAG, "HERE !" );
//
//            mAdapter = new HistoryAdapterRecycler(getContext(), new ArrayList<Exercise>());
//
//        }
//
//
//
//
//        } catch (Exception exc){
//            Log.d(DEBUG_TAG, "HERE 2" );
//
//            mAdapter = new HistoryAdapterRecycler(getContext(), new ArrayList<Exercise>());
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
//            recyclerView.setAdapter(mAdapter);
//
//
//        }
//
//        Log.d(DEBUG_TAG, "HERE 3" );


        // maybe call this onActivityResult to see if something user deleted an exercise

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(DEBUG_TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        ex = new ExerciseEntry(getContext());
        ex.open();
        LoaderManager.getInstance(this).initLoader(ALL_COMMENTS_LOADER_ID, null, this);

        Log.d(DEBUG_TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(DEBUG_TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "onDestroy");
    }


    @NonNull
    @Override
    public Loader<ArrayList<Exercise>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(DEBUG_TAG, "onCreateLoader: Thread ID: " + Thread.currentThread().getId());
        if (id == ALL_COMMENTS_LOADER_ID){
            return new AsyncHistoryLoad(getContext());
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Exercise>> loader, ArrayList<Exercise> data) {
        Log.d(DEBUG_TAG, "onLoadFinished: Thread ID: " + Thread.currentThread().getId());
        if (loader.getId() == ALL_COMMENTS_LOADER_ID) {

            Log.d(DEBUG_TAG, "onLoadFinished: dataSize: " + data.size());
                Log.d(DEBUG_TAG, "onLoadFinished: data size = 0");
                mAdapter = new HistoryAdapterRecycler(getContext(), data);
                ExerciseEntry exerc = new ExerciseEntry(getActivity());
                exerc.close();
                recyclerView.setAdapter(mAdapter);

        }
    }
    public static void onActivityResult(){

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Exercise>> loader) {
        Log.d(DEBUG_TAG, "onLoaderReset: Thread ID: " + Thread.currentThread().getId());

    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(DEBUG_TAG, "onACTIVITY RESUKT IN FRAG");
//
//        if(requestCode == 1)
//            mAdapter.onActivityResult(requestCode,1, data,);
//
//    }

}
