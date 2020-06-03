package edu.dartmouth.cs.myorganizer.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;

import edu.dartmouth.cs.myorganizer.LoginFireBaseActivity;
import edu.dartmouth.cs.myorganizer.ML.TextProcessing;
import edu.dartmouth.cs.myorganizer.MainActivity;
import edu.dartmouth.cs.myorganizer.R;
import edu.dartmouth.cs.myorganizer.adapters.LabelAdapter;
import edu.dartmouth.cs.myorganizer.adapters.PictureAdapter;
import edu.dartmouth.cs.myorganizer.database.FuegoBaseEntry;
import edu.dartmouth.cs.myorganizer.database.MyPicture;
import edu.dartmouth.cs.myorganizer.database.PictureEntry;

public class LabelsFragment extends Fragment {

    private static final String DEBUG = "LabelsFragment";
    ArrayList<String> mLabels;
    public SharedPreferences sharedPreferences;
    public RecyclerView recyclerView;
    public LabelAdapter mAdapter;
    private ArrayList<Integer> itemsData;
    private ProgressBar progressBar;

    private ArrayList<Integer> current_labels;

    private static final String BIO = "BIO";
    private static final String MATH = "MATH";
    private static final String HISTORY = "HISTORY";
    private static final String PHYSICS = "PHYSICS";
    private static final String THERMO = "THERMO";
    private static final String SMARTPHONE = "SMARTPHONE";

    Hashtable<String, Integer> FREQUENCY_COUNTS = new Hashtable<>();
    Hashtable<String, Integer> LABEL_CONSTANTS = new Hashtable<>();
    Hashtable<Integer, String> LABELS_TO_STRING = new Hashtable<>();

    private String[] bio;
    private String[] math;
    private String[] physics;
    private String[] history;
    private String[] thermo;
    private String[] smartphone;

    private AsyncInsert task;
    private AsyncView Vtask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(DEBUG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_labels, container, false);
        setHasOptionsMenu(true);
        progressBar = v.findViewById(R.id.progressBarLabelsFrag);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerLabels);
        current_labels = new ArrayList<Integer>();
        itemsData= new ArrayList<Integer>();
//        itemsData.add("Biology");
//        itemsData.add("Math"); // 0
//        itemsData.add("History");
//        itemsData.add("Physics"); // 1
//        itemsData.add("Thermodynamics");
//        itemsData.add("Smartphone");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        LABEL_CONSTANTS.put(BIO, 0);
        LABEL_CONSTANTS.put(MATH, 1);
        LABEL_CONSTANTS.put(HISTORY, 2);
        LABEL_CONSTANTS.put(PHYSICS, 3);
        LABEL_CONSTANTS.put(THERMO, 4);
        LABEL_CONSTANTS.put(SMARTPHONE, 5);

        LABELS_TO_STRING.put(0, BIO);
        LABELS_TO_STRING.put(1, MATH);
        LABELS_TO_STRING.put(2, HISTORY);
        LABELS_TO_STRING.put(3, PHYSICS);
        LABELS_TO_STRING.put(4, THERMO);
        LABELS_TO_STRING.put(5, SMARTPHONE);

        bio = getVocab("biologyCleaned.txt");
        math = getVocab("mathCleaned.txt");
        physics = getVocab("physicsCleaned.txt");
        history = getVocab("historyCleaned.txt");
        thermo = getVocab("thermoCleaned.txt");
        smartphone = getVocab("smartphoneCleaned.txt");

        return v;
    }

    private String[] getVocab(String filename){

       // File f=new File(getFilesDir(), filename);
        AssetManager assets=getContext().getAssets();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(assets.open(filename)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line = "";
        StringBuilder sb = new StringBuilder();
        while (true) {
            try {
                if (!((line = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }

            sb.append(line).append(" ");
        }

        String vocab = sb.toString();
        Log.d(DEBUG, String.valueOf(vocab.length()));
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] splited = vocab.split("\\s+");

        return splited;

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
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if (id == R.id.action_syncro) {


         //  recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
           progressBar.setVisibility(View.VISIBLE);
           task = new AsyncInsert();
           task.execute();




           SaveState(1);


           return true;
       }


        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onDestroy(){
        Log.d(DEBUG, "onDestroy");

        super.onDestroy();
    }

    @Override
    public void onPause() {

        Log.d(DEBUG, "onPause");
        super.onPause();
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
    @Override
    public void onStart() {
        super.onStart();

        Log.d(DEBUG, "onStart");
        if (LoadState() == 1){
            //progressBar.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            Vtask = new AsyncView();
            Vtask.execute();

        } else {
//            itemsData = new ArrayList<Integer>();
//            recyclerView.setAdapter(mAdapter);

        }


    }

    int FindLabel(){
        int Label = -1;
        int max = -1;
        Iterator hmIterator = FREQUENCY_COUNTS.entrySet().iterator();
        while (hmIterator.hasNext()) {
            Hashtable.Entry mapElement = (Hashtable.Entry) hmIterator.next();
            int counts = (int) mapElement.getValue();
            if (counts > max) {
                max = counts;
                Label = LABEL_CONSTANTS.get(mapElement.getKey());
            }
        }

        Log.d(DEBUG, "\n\nClassified as Label" + LABELS_TO_STRING.get(Label));

        return Label;

    }


    class AsyncInsert extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            Log.d(DEBUG, "AsyncInsert doInBackground()");
            PictureEntry mEntry=new PictureEntry(getContext());
            mEntry.open();

            final ArrayList<MyPicture> data = mEntry.getAllPictures();

            for ( int i = 0; i < data.size(); i++) {

                TextProcessing TP = new TextProcessing();
                int biocounter = 0;
                String[] tokens = TP.preprocess(data.get(i).getmText());
                for (int t = 0; t < bio.length; t++) {
                    for (int tt = 0; tt < tokens.length; tt++) {

                        if (tokens[tt].equals(bio[t])) {

                            biocounter++;
                        }
                    }
                }
                FREQUENCY_COUNTS.put(BIO, biocounter);
                Log.d(DEBUG, "biologoy frequency counts: " + biocounter);


                int mathcounter = 0;
                for (int t = 0; t < math.length; t++) {
                    for (int tt = 0; tt < tokens.length; tt++) {

                        if (tokens[tt].equals(math[t])) {

                            mathcounter++;
                        }
                    }
                }
                FREQUENCY_COUNTS.put(MATH, mathcounter);
                Log.d(DEBUG, "math frequency counts: " + mathcounter);


                int historycounter = 0;
                for (int t = 0; t < history.length; t++) {
                    for (int tt = 0; tt < tokens.length; tt++) {

                        if (tokens[tt].equals(history[t])) {

                            historycounter++;
                        }
                    }
                }
                FREQUENCY_COUNTS.put(HISTORY, historycounter);
                Log.d(DEBUG, "history frequency counts: " + historycounter);
                int physicscounter = 0;
                for (int t = 0; t < physics.length; t++) {
                    for (int tt = 0; tt < tokens.length; tt++) {

                        if (tokens[tt].equals(physics[t])) {

                            physicscounter++;
                        }
                    }
                }
                Log.d(DEBUG, "physics frequency counts: " + physicscounter);
                FREQUENCY_COUNTS.put(PHYSICS, physicscounter);
                int thermocounter = 0;
                for (int t = 0; t < thermo.length; t++) {
                    for (int tt = 0; tt < tokens.length; tt++) {

                        if (tokens[tt].equals(thermo[t])) {

                            thermocounter++;
                        }
                    }
                }
                Log.d(DEBUG, "thermo frequency counts: " + thermocounter);

                FREQUENCY_COUNTS.put(THERMO, thermocounter);


                int smartphonecounter = 0;
                for (int t = 0; t < smartphone.length; t++) {
                    for (int tt = 0; tt < tokens.length; tt++) {

                        if (tokens[tt].equals(smartphone[t])) {

                            smartphonecounter++;
                        }
                    }
                }
                Log.d(DEBUG, "smartphone frequency counts: " + smartphonecounter);

                FREQUENCY_COUNTS.put(SMARTPHONE, smartphonecounter);


                final int retLabel = FindLabel();
                addLabelAdapter(retLabel);
                data.get(i).setmLabel(retLabel);
                final long old_id = data.get(i).getId();
                final long new_id = mEntry.insertEntry(data.get(i));
                data.get(i).setId(new_id);

                mEntry.deletePicture(old_id);


                FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

                final String mUserId = mFirebaseUser.getUid();

                FirebaseDatabase.getInstance().getReference("user_" + mUserId).child("picture_entries").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {


                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            FuegoBaseEntry entry = postSnapshot.getValue(FuegoBaseEntry.class);
                            if (old_id == Long.parseLong(entry.getId())) {


                                FuegoBaseEntry FuegoEntry = new FuegoBaseEntry(entry.getEmail(), String.valueOf(new_id), entry.getImageUri(), entry.getImageBase64(), entry.getText(), String.valueOf(retLabel), entry.getDate(), entry.getSynced());

                                //entry.setLabel(String.valueOf(all_pics.get(i).getmLabel()));
                                postSnapshot.getRef().removeValue();

                                FirebaseDatabase.getInstance().getReference("user_" + mUserId).child("picture_entries").push().setValue(FuegoEntry).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(DEBUG, "successfully inserted entry");
                                        // Write was successful!
                                        // ...
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(DEBUG, "Failed to inserted entry");

                                        // Write failed
                                        // ...
                                    }
                                });
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            mEntry.close();




            return null;
        }


        @Override
        protected void onProgressUpdate(String... name) {
            if (!isCancelled()) {
                // ((MainActivity) context).onResult(result);
                //mAdapter.add(name[0]);

            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(DEBUG, "INSERT THREAD DONE");
            task = null;
            progressBar.setVisibility(View.GONE);
            mAdapter = new LabelAdapter(getContext(), itemsData);
            //recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(mAdapter);


        }

    }

    void addLabelAdapter(int label) {
        if (itemsData.contains(label)) {
            return;
        } else {

            itemsData.add(label);
            return;
        }
    }

    class AsyncView extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... unused) {

            PictureEntry p = new PictureEntry(getContext());
            p.open();
           ArrayList<MyPicture> pics =  p.getAllPictures();
           p.close();
           for (int i = 0; i < pics.size(); i++){
               addLabelAdapter(pics.get(i).getmLabel());
           }
           return null;
        }

        @Override
        protected void onProgressUpdate(String... name) {
            if (!isCancelled()) {
                // ((MainActivity) context).onResult(result);
                //mAdapter.add(name[0]);

            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(DEBUG, "INSERT THREAD DONE");
            Vtask = null;

            progressBar.setVisibility(View.GONE);
            mAdapter = new LabelAdapter(getContext(), itemsData);
            //recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(mAdapter);


        }

    }


}
