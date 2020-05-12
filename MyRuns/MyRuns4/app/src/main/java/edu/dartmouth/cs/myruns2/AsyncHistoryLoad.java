package edu.dartmouth.cs.myruns2;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;


import edu.dartmouth.cs.myruns2.models.Exercise;

class AsyncHistoryLoad extends AsyncTaskLoader<ArrayList<Exercise>> {

    private static final String DEBUG_TAG = "HistoryAsyncLoad";
    private final ExerciseEntry dataSource;

    public AsyncHistoryLoad(@NonNull Context context) {
        super(context);

        Log.d(DEBUG_TAG, "AsyncHistoryLoad: Thread ID: " + Thread.currentThread().getId());
        dataSource = new ExerciseEntry(context);
        dataSource.open();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.d(DEBUG_TAG, "onStartLoading: Thread ID: " + Thread.currentThread().getId());
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<Exercise> loadInBackground() {
        Log.d(DEBUG_TAG, "loadInBackground: Thread ID: " + Thread.currentThread().getId());
        return dataSource.getAllExercises();
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        Log.d(DEBUG_TAG, "onForceLoad: Thread ID: " + Thread.currentThread().getId());
    }
}
