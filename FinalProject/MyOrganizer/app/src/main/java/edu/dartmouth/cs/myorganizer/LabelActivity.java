package edu.dartmouth.cs.myorganizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Iterator;

import edu.dartmouth.cs.myorganizer.ML.TextProcessing;
import edu.dartmouth.cs.myorganizer.adapters.PictureAdapter;
import edu.dartmouth.cs.myorganizer.database.AsyncPictureLoader;
import edu.dartmouth.cs.myorganizer.database.FuegoBaseEntry;
import edu.dartmouth.cs.myorganizer.database.MyPicture;
import edu.dartmouth.cs.myorganizer.database.PictureEntry;
import edu.dartmouth.cs.myorganizer.fragments.PictureGridFragment;

public class LabelActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<MyPicture>> {
    private static final String DEBUG = "LabelActivity";
//    private static final String BIO = "BIO";
//    private static final String MATH = "MATH";
//    private static final String HISTORY = "HISTORY";
//    private static final String PHYSICS = "PHYSICS";
//    private static final String THERMO = "THERMO";
//    private static final String SMARTPHONE = "SMARTPHONE";
//
//    Hashtable<String, Integer> FREQUENCY_COUNTS = new Hashtable<>();
//    Hashtable<String, Integer> LABEL_CONSTANTS = new Hashtable<>();
//    Hashtable<Integer, String> LABELS_TO_STRING = new Hashtable<>();
//
//    private String[] bio;
//    private String[] math;
//    private String[] physics;
//    private String[] history;
//    private String[] thermo;
//    private String[] smartphone;


    private ArrayList<String> itemsData;
    private ArrayList<MyPicture> mInput;
    private int clickedLabel;
    RecyclerView recyclerView;
    private PictureAdapter mAdapter;
    private static final String AUTHORITY="edu.dartmouth.cs.myorganizer";
    //private long id;
    private int LAUNCH_TEXT_ACTIVITY = 1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private static final int ALL_COMMENTS_LOADER_ID = 1;
    private PictureEntry ex;
    private AsyncDelete delete_task = null;
    private int prev;
    private int result_pos;
    private ProgressBar progressBar;
    private Resources resources;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);
        itemsData = new ArrayList<String>();
        itemsData.add("Biology");
        itemsData.add("Math"); // 0
        itemsData.add("History");
        itemsData.add("Phyics"); // 1
        itemsData.add("Thermodynamics");
        itemsData.add("Smartphone");


        Intent i = getIntent();
        clickedLabel = i.getIntExtra("label", -1);
        Log.d(DEBUG, "clicked Label" + clickedLabel);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewLabel);
        // set a GridLayoutManager with 2 number of columns , horizontal gravity and false value for reverseLayout to show the items from start to end
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        // mAdapter= new PictureAdapter(getContext(),  mInput);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(DEBUG, "onStart");
        if (clickedLabel<0){

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
        if (id == ALL_COMMENTS_LOADER_ID){
            return new AsyncPictureLoader(this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<MyPicture>> loader, ArrayList<MyPicture> data) {
        Log.d(DEBUG, "onLoadFinished: Thread ID: " + Thread.currentThread().getId());
        if (loader.getId() == ALL_COMMENTS_LOADER_ID) {

            Log.d(DEBUG, "onLoadFinished: dataSize: " + data.size());
          mInput = new ArrayList<MyPicture>();

            for (int i = 0; i < data.size(); i++){

                if (data.get(i).getmLabel() == clickedLabel){

                    mInput.add(data.get(i));
                }
            }


            if (mInput.isEmpty() || mInput == null){

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
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Log.d(DEBUG, "delete selected");
                prev = mInput.size();
                final long result_id =data.getLongExtra("result", -1);
                result_pos=data.getIntExtra("pos", -1);
                Log.d(DEBUG, "delete selected: result_id and result_pos :" + result_id + "  " + result_pos);

                mFirebaseAuth = FirebaseAuth.getInstance();
                mFirebaseUser = mFirebaseAuth.getCurrentUser();

                String mUserId = mFirebaseUser.getUid();
                Log.d(DEBUG, "indelete userId: " + mUserId);
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("user_" + mUserId);
                mDatabase.child("picture_entries").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {


                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            //getting userinfo
                            FuegoBaseEntry entry = postSnapshot.getValue(FuegoBaseEntry.class);
                            long current_id = Long.parseLong(entry.getId());
                            Log.d(DEBUG, "Found item: " + current_id);
                            if (current_id == result_id){
                                Log.d(DEBUG, "Found item: " + current_id);

                                postSnapshot.getRef().removeValue();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                delete_task=new AsyncDelete(result_id);
                delete_task.execute();
//                mInput.remove(result_pos);
//                mAdapter.notifyItemRangeRemoved(prev, 1);
//                mAdapter.notifyDataSetChanged();
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
        AsyncDelete(long id){
            this.id = id;

        }
        @Override
        protected Void doInBackground(Void... unused) {

            Log.d(DEBUG, "USER HIT DELETE! and wants to Delete: " + id);



            PictureEntry mEntry = new PictureEntry(getApplicationContext());
            mEntry.open();

            int ret = mEntry.deletePicture(id);
            mEntry.close();

            if (ret>0){
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
           // Log.d(DEBUG, "Delete Done:   " + pos);
            delete_task = null;

            mInput.remove(result_pos);
            mAdapter.notifyItemRangeRemoved(prev, 1);
            mAdapter.notifyDataSetChanged();


        }
    }


}
