package edu.dartmouth.cs.myruns2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;

import edu.dartmouth.cs.myruns2.models.Exercise;
import edu.dartmouth.cs.myruns2.models.MyGlobals;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String DEBUG_TAG = "SettingsFragment";
    public Context context;
    private MyGlobals globs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);


        context = getActivity();
        globs = new MyGlobals();

//            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
//
//            String  check_box = prefs.getString("checkbox_preference", null);
//            String  units = prefs.getString("list_preference", null);


        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);



        boolean mPriv = prefs.getBoolean("checkbox_preference", false);


        String readUnit = prefs.getString("list_preference", "");
        Log.d("DEBUG", "READING UNITS: " + readUnit);

       if (readUnit.equals("")){
           globs.CURRENT_UNITS = 0;
           Log.d(DEBUG_TAG, "SETTINGS PREFS ARE EMPTY");
           prefs.edit().putString("list_preference", globs.getValue_str(globs.UNIT_TABLE, 0));
           prefs.edit().putBoolean("checkbox_preference", true);
           prefs.edit().commit();



       }
        if (mPriv) {
            Log.d("DEBUG", "**>>>> Privacy CHECKED :(");
        } else {

            Log.d("DEBUG", "**>>>> Privacy NOTTTT CHECKED :(");

        }




    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(DEBUG_TAG, "onResume");

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(DEBUG_TAG, "onResume");


        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(DEBUG_TAG, "onStart");
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d(DEBUG_TAG, "onDestroy");
    }




    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
        // Asyc task goes here
        //goes through and updates units and privacy
        // does unit conversion


        boolean mPriv = sharedPreferences.getBoolean("checkbox_preference", false);
        String mPrivList = sharedPreferences.getString("list_preference", "");


        if (mPrivList.equals("")) {
            globs.CURRENT_UNITS = 0;

            sharedPreferences.edit().putString("list_preference", globs.getValue_str(globs.UNIT_TABLE, 0));


            sharedPreferences.edit().putBoolean("checkbox_preference", true);
            sharedPreferences.edit().commit();


        } else {

            ExerciseEntry mEntry = new ExerciseEntry(context);

            Log.d("EXERCISE", "Current UNITS  " + globs.CURRENT_UNITS);

            globs.CURRENT_UNITS = globs.getValue_int(globs.UNIT_TABLE, mPrivList);

            Log.d("EXERCISE", "Changed UNITS " + globs.CURRENT_UNITS);

            mEntry.open();

            ArrayList<Exercise> ex = mEntry.getAllExercises();

            for (int i = 0; i < ex.size(); i++) {

                Exercise e = ex.get(i);
                if (mPriv) {
                    Log.d("EXERCISE", "Current Privacy: " + e.getmPrivacy());
                    e.setmPrivacy(1);
                    mEntry.updateExercise(e);

                    mEntry.printExercise(e);


                } else {
                    Log.d("EXERCISE", "Current Privacy: " + e.getmPrivacy());
                    e.setmPrivacy(0);
                    mEntry.updateExercise(e);
                    mEntry.printExercise(e);

                }
            }


            mEntry.close();
        }
    }

}