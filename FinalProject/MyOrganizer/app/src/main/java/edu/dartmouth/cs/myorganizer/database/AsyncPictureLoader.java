package edu.dartmouth.cs.myorganizer.database;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;


import edu.dartmouth.cs.myorganizer.database.PictureEntry;

public class AsyncPictureLoader extends AsyncTaskLoader<ArrayList<MyPicture>> {

    private static final String DEBUG_TAG = "AsyncPictureLoader";
    private final PictureEntry dataSource;

    public AsyncPictureLoader(@NonNull Context context) {
        super(context);

        Log.d(DEBUG_TAG, "AsyncHistoryLoad: Thread ID: " + Thread.currentThread().getId());
        dataSource = new PictureEntry(context);
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
    public ArrayList<MyPicture> loadInBackground() {
        Log.d(DEBUG_TAG, "loadInBackground: Thread ID: " + Thread.currentThread().getId());
        return dataSource.getAllPictures();
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        Log.d(DEBUG_TAG, "onForceLoad: Thread ID: " + Thread.currentThread().getId());
    }
}
