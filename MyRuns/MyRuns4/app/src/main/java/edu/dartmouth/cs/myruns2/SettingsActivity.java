package edu.dartmouth.cs.myruns2;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import edu.dartmouth.cs.myruns2.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "SettingsActivity";

//Look at this link
//https://developer.android.com/guide/topics/ui/settings/use-saved-values
// also this link
// https://developer.android.com/reference/android/support/v7/preference/PreferenceFragmentCompat.html

// https://guides.codepath.com/android/settings-with-preferencefragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();

        //SharedPreferences.OnSharedPreferenceChangeListener

        //Setup the appropriate action bar with title and back button
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Settings");
        setContentView(R.layout.activity_settings);

        //Now load in our settings as a fragment
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        SettingsFragment mPrefsFragment = new SettingsFragment();
        mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
        mFragmentTransaction.commit();




    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(DEBUG_TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(DEBUG_TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(DEBUG_TAG, "onPause");
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        Log.d(DEBUG_TAG, "onDestroy");
    }



}
