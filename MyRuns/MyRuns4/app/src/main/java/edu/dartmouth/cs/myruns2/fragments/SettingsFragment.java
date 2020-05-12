package edu.dartmouth.cs.myruns2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;

import edu.dartmouth.cs.myruns2.database.ExerciseEntry;
import edu.dartmouth.cs.myruns2.R;
import edu.dartmouth.cs.myruns2.SigninActivity;
import edu.dartmouth.cs.myruns2.models.Exercise;
import edu.dartmouth.cs.myruns2.models.MyGlobals;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String DEBUG_TAG = "SettingsFragment";
    public Context context;
    private MyGlobals globs;
    public ArrayList<Exercise> ex;
    public AsyncTask task;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);

        context = getActivity();
        globs = new MyGlobals();

        Preference signOut = findPreference("sign_out_preference");
        signOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try{
                    Intent intent = new Intent(getActivity(), SigninActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                } catch(Exception e){
                    //Return false if our sign out intent fails
                    return false;
                }
            }
        });
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
       Thread t= new UpdateUnitThread();
       t.run();
        boolean mPriv = sharedPreferences.getBoolean("checkbox_preference", false);
        String priv = String.valueOf(mPriv);
        Log.d(DEBUG_TAG, "SETTINGS FRAGMENT THREAD ID: " + priv);

        AsyncTask<String, Void, Void> task =  new UpdatePrivacyTask();
        task.execute(priv);
    }

    public class UpdateUnitThread extends Thread {
        @Override
        public void run() {
            Log.d(DEBUG_TAG, "SETTINGS FRAGMENT THREAD ID: " + Thread.currentThread().getId());

            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String mPrivList = sharedPreferences.getString("list_preference", "");
            globs.CURRENT_UNITS = globs.getValue_int(globs.UNIT_TABLE, mPrivList);
        }
    }

    class UpdatePrivacyTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... priv) {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Log.d(DEBUG_TAG, "SETTINGS FRAGMENT  UpdatePrivacyTask THREAD ID: " + Thread.currentThread().getId());
            boolean p = Boolean.parseBoolean(priv[0]);
                ExerciseEntry mEntry = new ExerciseEntry(context);
                mEntry.open();
                ex = mEntry.getAllExercises();

                for (int i = 0; i < ex.size(); i++) {
                    Exercise e = ex.get(i);
                    if (p) {
                        e.setmPrivacy(1);
                        mEntry.updateExercise(e);
                    } else {
                        e.setmPrivacy(0);
                        mEntry.updateExercise(e);
                    }
                }
                mEntry.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            task = null;
        }

    }
}




