package edu.dartmouth.cs.myruns2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import edu.dartmouth.cs.myruns2.HistoryFragment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;


public class MainMyRunsActivity extends AppCompatActivity {


    public static final String MAIN_ITEM_TO_DELETE = "item";
    public static final String MAIN_ITEM_TO_INSERT = "INSERTINGITEM";
    private FloatingActionButton mStartButton;
    private Spinner mInput;
    private Spinner mActivity;
    private String input = "";
    private String activity = "";

    private static final String DEBUG_TAG = "MainMyRunsActivity";
    private ActionBar actionBar;

    private static final String TAG = "from_main_activity";


    //Tab stuff
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private ArrayList<Fragment> fragments;
    private ActionTabsViewPagerAdapter myViewPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Main Activity");
        setContentView(R.layout.main_activity);

        // Get viewPager instance
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // create a fragment list in order.
        fragments = new ArrayList<Fragment>();
        fragments.add(new StartFragment());
        fragments.add(new HistoryFragment());


        // use FragmentPagerAdapter to bind the TabLayout (tabs with different titles)
        // and ViewPager (different pages of fragment) together.
        myViewPageAdapter = new ActionTabsViewPagerAdapter(getSupportFragmentManager(),
                fragments);
        // add the PagerAdapter to the viewPager
        viewPager.setAdapter(myViewPageAdapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_start:

                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.navigation_history:

                                viewPager.setCurrentItem(1);
                                break;
                        }
                        return false;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_settings_menu) {

            // Launch our settings activity that serves as a wrapper for the settings fragment
            Intent intent = new Intent(MainMyRunsActivity.this, SettingsActivity.class);
            startActivity(intent);


        }
        //https://stackoverflow.com/questions/9664108/how-to-finish-parent-activity-from-child-activity
        else if (id == R.id.main_edit_profile_menu) {
            Intent intent = new Intent(MainMyRunsActivity.this, RegisterProfileActivity.class);

            intent.putExtra(RegisterProfileActivity.INTENT_FROM, TAG);
            startActivity(intent);


        } else if (id == android.R.id.home) {         //On home button click
            //finish();
            //return true;
            //.onBackPressed();
            // ((AppCompatActivity)getActivity()).onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(DEBUG_TAG, "onACTIVITYResult in MainMyRuns:   " +requestCode);

        if (requestCode == 1) {
            HistoryFragment.mAdapter.onActivityResult(-10);
//        } else if (requestCode == 2){
//
//            //Intent i =getIntent();
//            //String ii = i.getStringExtra(MAIN_ITEM_TO_INSERT);
//            //Bundle bund =  data.getExtras();
//            String _id = data.getDataString();
//            //String _id = bund.getString(MAIN_ITEM_TO_INSERT);
//            long id = Long.parseLong(_id);
//            Log.d(DEBUG_TAG, "onACTIVITYResult in MainMyRuns:  ID= " + id);
//
//            HistoryFragment.mAdapter.onActivityResult(0);
        }
    }

}
