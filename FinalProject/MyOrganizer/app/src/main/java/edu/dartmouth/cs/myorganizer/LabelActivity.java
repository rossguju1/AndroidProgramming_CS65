package edu.dartmouth.cs.myorganizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Arrays;

import edu.dartmouth.cs.myorganizer.adapters.PictureAdapter;
import edu.dartmouth.cs.myorganizer.database.AsyncPictureLoader;
import edu.dartmouth.cs.myorganizer.database.MyPicture;
import edu.dartmouth.cs.myorganizer.database.PictureEntry;

public class LabelActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<MyPicture>> {
    private static final String DEBUG = "LabelActivity";
    private ArrayList<String> itemsData;
    private ArrayList<MyPicture> mInput;
    private int clickedLabel;
    RecyclerView recyclerView;
    private PictureAdapter mAdapter;

    private static final int ALL_COMMENTS_LOADER_ID = 1;
    private PictureEntry ex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);

        itemsData= new ArrayList<String>();
        itemsData.add("Math");
        itemsData.add("Phyics");
        itemsData.add("Biology");
        itemsData.add("Chemistry");

        Intent i = getIntent();
        clickedLabel = i.getIntExtra("label", -1);
        Log.d(DEBUG, "clicked Label" + clickedLabel);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewLabel);
        // set a GridLayoutManager with 2 number of columns , horizontal gravity and false value for reverseLayout to show the items from start to end
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        // mAdapter= new PictureAdapter(getContext(),  mInput);
        recyclerView.setItemAnimator(new DefaultItemAnimator());



       // TextView item1 =  findViewById(R.id.ui_sameple_image);

        //item1.setText("Classified Images ");


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


        super.onActivityResult(requestCode, resultCode, data);


    }



}
