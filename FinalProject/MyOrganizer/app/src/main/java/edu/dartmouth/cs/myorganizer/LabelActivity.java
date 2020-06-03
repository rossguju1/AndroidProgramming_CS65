package edu.dartmouth.cs.myorganizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.dartmouth.cs.myorganizer.adapters.PictureAdapter;
import edu.dartmouth.cs.myorganizer.database.AsyncPictureLoader;
import edu.dartmouth.cs.myorganizer.database.FuegoBaseEntry;
import edu.dartmouth.cs.myorganizer.database.MyPicture;
import edu.dartmouth.cs.myorganizer.database.PictureEntry;

public class LabelActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<MyPicture>> {
    private static final String DEBUG = "LabelActivity";

    private ArrayList<MyPicture> mInput;
    private int clickedLabel;
    RecyclerView recyclerView;
    private PictureAdapter mAdapter;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private static final int ALL_COMMENTS_LOADER_ID = 1;
    private PictureEntry ex;
    private AsyncDelete delete_task = null;
    private int prev;
    private int result_pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);


        Intent i = getIntent();
        clickedLabel = i.getIntExtra("label", -1);
        Log.d(DEBUG, "clicked Label" + clickedLabel);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewLabel);
        // set a GridLayoutManager with 2 number of columns , horizontal gravity and false value for reverseLayout to show the items from start to end
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        recyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    @Override
    public void onStart() {
        super.onStart();
        if (clickedLabel < 0) {
            Log.d(DEBUG, "label error: label negative");
            return;
        }

        ex = new PictureEntry(this);
        ex.open();
        LoaderManager.getInstance(this).initLoader(ALL_COMMENTS_LOADER_ID, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(DEBUG, "onResume");
    }

    @NonNull
    @Override
    public Loader<ArrayList<MyPicture>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(DEBUG, "onCreateLoader: Thread ID: " + Thread.currentThread().getId());
        if (id == ALL_COMMENTS_LOADER_ID) {
            return new AsyncPictureLoader(this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<MyPicture>> loader, ArrayList<MyPicture> data) {
        if (loader.getId() == ALL_COMMENTS_LOADER_ID) {
            mInput = new ArrayList<MyPicture>();
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getmLabel() == clickedLabel) {
                    mInput.add(data.get(i));
                }
            }
            if (mInput.isEmpty() || mInput == null) {
                Log.d(DEBUG, "(mInput.isEmpty() || mInput == null)");
                mAdapter = new PictureAdapter(this, mInput);
                recyclerView.setAdapter(mAdapter);

            } else {
                mAdapter = new PictureAdapter(this, mInput);
                recyclerView.setAdapter(mAdapter);
                PictureEntry mEntry = new PictureEntry(this);
                for (int i = 0; i < mInput.size(); i++) {
                    mEntry.printPicture(mInput.get(i));
                }
                mEntry.close();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<MyPicture>> loader) {
        Log.d(DEBUG, "onLoaderReset: Thread ID: " + Thread.currentThread().getId());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                prev = mInput.size();
                final long result_id = data.getLongExtra("result", -1);
                result_pos = data.getIntExtra("pos", -1);
                mFirebaseAuth = FirebaseAuth.getInstance();
                mFirebaseUser = mFirebaseAuth.getCurrentUser();

                String mUserId = mFirebaseUser.getUid();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("user_" + mUserId);
                mDatabase.child("picture_entries").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            //getting userinfo
                            FuegoBaseEntry entry = postSnapshot.getValue(FuegoBaseEntry.class);
                            long current_id = Long.parseLong(entry.getId());
                            Log.d(DEBUG, "Found item: " + current_id);
                            if (current_id == result_id) {
                                postSnapshot.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                delete_task = new AsyncDelete(result_id);
                delete_task.execute();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    class AsyncDelete extends AsyncTask<Void, String, Void> {
        int pos;
        private long id;

        AsyncDelete(long id) {
            this.id = id;

        }

        @Override
        protected Void doInBackground(Void... unused) {
            PictureEntry mEntry = new PictureEntry(getApplicationContext());
            mEntry.open();

            int ret = mEntry.deletePicture(id);
            mEntry.close();

            if (ret > 0) {
                Log.d("DEBUG", "DeleteWorked and removed: " + id);

            } else {
                Log.d("DEBUG", "Delete Failed to remove: " + id);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... name) {
            if (!isCancelled()) {
                //mAdapter.add(name[0]);
            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            delete_task = null;
            mInput.remove(result_pos);
            mAdapter.notifyItemRangeRemoved(prev, 1);
            mAdapter.notifyDataSetChanged();
        }
    }
}
