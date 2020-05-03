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
    public Context context;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);


        context = getActivity();

//            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
//
//            String  check_box = prefs.getString("checkbox_preference", null);
//            String  units = prefs.getString("list_preference", null);


        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean mPriv = prefs.getBoolean("checkbox_preference", false);

        if (mPriv) {
            Log.d("Preferences on Create", "**>>> Privacy ChECKED!");

        } else {

            Log.d("Preferences on Create", "**>>>> Privacy NOTTTT CHECKED :(");

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key)
    {
   // Asyc task goes here
        //goes through and updates units and privacy
        // does unit conversion

        MyGlobals globs = new MyGlobals();


        boolean mPriv = sharedPreferences.getBoolean("checkbox_preference", false);
        String mPrivList = sharedPreferences.getString("list_preference", "");

        ExerciseEntry mEntry = new ExerciseEntry(context);



        Log.d("EXERCISE","Current UNITS  " + globs.CURRENT_UNITS);

        globs.CURRENT_UNITS = globs.getValue_int(globs.UNIT_TABLE, mPrivList);

        Log.d("EXERCISE","Changed UNITS " + globs.CURRENT_UNITS);

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